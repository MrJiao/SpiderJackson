package com.jackson.db.service;

import com.jackson.db.SqlUtil;
import com.jackson.db.dao.IpOkDao;
import com.jackson.db.po.Ip;
import com.jackson.executor.DBSingleExecutor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Jackson on 2016/11/6.
 */
@Deprecated
public enum IpOkService implements IDaoService<Ip>{
instance;
    private static Logger logger = LogManager.getLogger(IpOkService.class.getName());
    private final SqlSessionFactory factory;
    private LinkedBlockingQueue<Ip> queue;


    private final HashSet insertIpsSet;
    private final LinkedList insertIpsList;
    private final HashSet updateIpsSet;
    private final LinkedList updateIpsList;

    public IpOkService setGetIpSize(int getIpSize) {
        this.getIpSize = getIpSize;
        return this;
    }

    public IpOkService setMinIpOkCatch(int minIpOkCatch) {
        this.minIpOkCatch = minIpOkCatch;
        return this;
    }

    private int getIpSize = 100;
    private int minIpOkCatch = 20;
    IpOkService() {
        factory = SqlUtil.getFactory();
        queue = new LinkedBlockingQueue<>();

        insertIpsSet = new HashSet<Ip>();
        insertIpsList = new LinkedList<Ip>();

        updateIpsSet = new HashSet<>();
        updateIpsList = new LinkedList<>();
        init();
    }

    public void insert(Ip ip){
        synchronized(insertIpsSet){
            insertIpsSet.add(ip);
            logger.info("添加新ip{}:{} insertIpsSet size{}",ip.getHost(),ip.getPort(),insertIpsSet.size());
            DBSingleExecutor.instance.execute(new InsertTask());

        }
    }


    //task线程执行
    private void insertIpsToDatabase(){
        synchronized (insertIpsSet){
            logger.info("存入数据库 insertIpsSet size:{}", insertIpsSet.size());
            if(insertIpsSet.size()>0){
                insertIpsList.addAll(insertIpsSet);
                insertIpsToDatabase(insertIpsList);
                insertIpsList.clear();
                insertIpsSet.clear();//存入数据库方法成功后才会清空

            }
        }
    }

    private void insertIpsToDatabase(List<Ip> ips){
        logger.info("ips size:{}",ips.size());
        SqlSession sqlSession = null;
        try{
            sqlSession = factory.openSession();
            IpOkDao mapper = sqlSession.getMapper(IpOkDao.class);
            mapper.insertsIgnore(ips);
            sqlSession.commit();
        }catch (Exception e){
            logger.error(e.toString());
        }finally {
            sqlSession.close();
        }
    }


    public void insert(List<Ip> ips){
        synchronized(insertIpsSet){
            insertIpsSet.addAll(ips);
            logger.info("添加新ip size:{} insertIpsSet size{}",ips.size(),insertIpsSet.size());
            DBSingleExecutor.instance.execute(new InsertTask());

        }
    }

    private void databaseToQueue() {
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            IpOkDao mapper = sqlSession.getMapper(IpOkDao.class);
            List<Ip> ips = mapper.findFreeMinTestTime(getIpSize);
            logger.debug("从数据库取出来ips size:{}", ips.size());
            if(handler!=null)handler.handle(ips);
            if(ips.size()>0){
                mapper.updateIpsToTakenOut(ips);
                sqlSession.commit();
                synchronized (queue){
                    queue.addAll(ips);
                }
            }
            logger.debug("queue中的 size:{}", queue.size());
        } catch (Exception e){
            logger.error(e.toString());
        }finally {
            if (sqlSession != null)
                sqlSession.close();
        }
    }

    private DatabaseToQueueHandler handler;

    /**
     * 当从数据库里取出数据后的回调，这个回调在数据存入queue之前
     * @param handler
     */
    public void setDatabaseToQueueHandler(DatabaseToQueueHandler handler){
        this.handler = handler;
    }

    public interface DatabaseToQueueHandler{
        void handle(List<Ip> list);
    }


    public void updateIp(Ip ip) {
        logger.debug("updateIpsSet size:{}", updateIpsSet.size());
        synchronized (updateIpsSet){
            updateIpsSet.add(ip);
        }
        DBSingleExecutor.instance.execute(new UpdateTask());
    }


    //task线程执行
    private void updateIpsToDatabase(){
        synchronized (updateIpsSet){
            logger.debug("updateIpsSet size is {}", updateIpsSet.size());
            if(updateIpsSet.size()>0){
                updateIpsList.addAll(updateIpsSet);
                updateIpsToDatabase(updateIpsList);
                updateIpsSet.clear();//存入数据库方法成功后才会清空
                updateIpsList.clear();
            }
        }

    }


    private  void updateIpsToDatabase(List<Ip> ips) {
        logger.debug("更新ips size:{}",ips.size());
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            IpOkDao mapper = sqlSession.getMapper(IpOkDao.class);
            for (Ip ipTemp : ips) {
                mapper.update(ipTemp);
            }
            sqlSession.commit();
        } catch (Exception e) {
            logger.error(e.toString());
        } finally {
            sqlSession.close();
        }
    }



    public Ip take() {
        if (queue.size() < minIpOkCatch) {
            synchronized (this){
                if(queue.size() < minIpOkCatch)
                    DBSingleExecutor.instance.execute(new Database2Queue());

            }
        }
        logger.debug("queue size:{}", queue.size());
        return queue.poll();
    }

    /**
     * 添加到待测试queue中
     * @param ips
     */
    public void add(List<Ip> ips){
        queue.addAll(ips);
    }


    private void init(){
        setTakeState2Free();
        createTable();
    }

    private void createTable() {
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            IpOkDao mapper = sqlSession.getMapper(IpOkDao.class);
            mapper.createTable();
            sqlSession.commit();
        } catch (Exception e){
            logger.error(e.toString());
        } finally{
            sqlSession.close();
        }
    }

    private void setTakeState2Free() {
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            IpOkDao mapper = sqlSession.getMapper(IpOkDao.class);
            mapper.setTakeState2Free();
            sqlSession.commit();
        } catch (Exception e){
            logger.error(e.toString());
        } finally{
            sqlSession.close();
        }
    }

    private class InsertTask extends DBSingleExecutor.DBTask {
        @Override
        public void run() {
            insertIpsToDatabase();
        }
    }

    private class UpdateTask extends DBSingleExecutor.DBTask{
        @Override
        public void run() {
            updateIpsToDatabase();
        }
    }

    private class Database2Queue extends DBSingleExecutor.DBTask {
        @Override
        public void run() {
            databaseToQueue();
        }
    }

}
