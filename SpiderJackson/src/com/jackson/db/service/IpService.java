package com.jackson.db.service;

import com.jackson.db.SqlUtil;
import com.jackson.db.dao.IpDao;
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
public enum IpService implements IDaoService<Ip> {
instance;
    private static Logger logger = LogManager.getLogger(IpService.class.getName());
    private final SqlSessionFactory factory;
    private LinkedBlockingQueue<Ip> queue;

    private final HashSet insertIpsSet;
    private final LinkedList insertIpsList;
    private final HashSet updateIpsSet;
    private final LinkedList updateIpsList;
    private int getIpSize = 100;
    private int minIpCatch = 20;

    public IpService setGetIpSize(int getIpSize) {
        this.getIpSize = getIpSize;
        return this;
    }

    public IpService setMinIpCatch(int minIpCatch) {
        this.minIpCatch = minIpCatch;
        return this;
    }

    IpService() {
        factory = SqlUtil.getFactory();
        queue = new LinkedBlockingQueue<>();

        insertIpsSet = new HashSet<Ip>();
        insertIpsList = new LinkedList<Ip>();

        updateIpsSet = new HashSet<>();
        updateIpsList = new LinkedList<>();
        init();
    }

    /**
     * 添加到数据库
     * @param ips
     */
    public void insert(List<Ip> ips){
        synchronized(insertIpsSet){
            insertIpsSet.addAll(ips);
            logger.info("添加新ip size:{} insertIpsSet size{}",ips.size(),insertIpsSet.size());
            DBSingleExecutor.instance.execute(new InsertTask());

        }
    }

    /**
     * 添加到数据库
     * @param ip
     */
    public void insert(Ip ip){
        synchronized(insertIpsSet){
            insertIpsSet.add(ip);
            logger.debug("添加新ipinsertIpsSet size{}",insertIpsSet.size());
            DBSingleExecutor.instance.execute(new InsertTask());

        }
    }


    private void init(){
        setTask2Free();
        createTable();
    }

    private void createTable() {
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            IpDao mapper = sqlSession.getMapper(IpDao.class);
            mapper.createTable();
            sqlSession.commit();
        } catch (Exception e){
            logger.error(e.toString());
        } finally{
            sqlSession.close();
        }
    }

    private void setTask2Free() {
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            IpDao mapper = sqlSession.getMapper(IpDao.class);
            mapper.setTakeState2Free();
            sqlSession.commit();
        } catch (Exception e){
            logger.error(e.toString());
        } finally{
            sqlSession.close();
        }
    }


    /**
     * 存入数据库
     * @param ips
     */
    private void insertIpsToDatabase(List<Ip> ips) {
        logger.info("ip存入数据库 insertIpsSet size:{}", ips.size());
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            IpDao mapper = sqlSession.getMapper(IpDao.class);
            mapper.insertsIgnore(ips);
            sqlSession.commit();
        } catch (Exception e) {
            logger.error(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 从数据库读取proxy 到测试队列中
     */
    private synchronized void databaseToQueue() {
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            IpDao mapper = sqlSession.getMapper(IpDao.class);
            List<Ip> ips = mapper.findFreeMinTestTime(getIpSize);

            logger.debug("数据库取出的 ips size:{}", ips.size());

            if (ips.size() > 0) {
                mapper.updateIpsToTakenOut(ips);
                sqlSession.commit();
                synchronized (queue){
                    queue.addAll(ips);
                }
            }

            logger.info("当前缓存ips queue size:{}", queue.size());
        } catch (Exception e) {
            logger.error(e.toString());
        } finally {
            if (sqlSession != null)
                sqlSession.close();
        }
    }


    private  void updateIpsToDatabase(List<Ip> ips) {
        logger.info("更新ip size is {}", updateIpsSet.size());
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            IpDao mapper = sqlSession.getMapper(IpDao.class);
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

    public void updateIp(Ip ip) {
        logger.debug("更新Ip ip:{}:{}",ip.getHost(),ip.getPort());
        synchronized (updateIpsSet){
            updateIpsSet.add(ip);
        }
        DBSingleExecutor.instance.execute(new UpdateTask());
    }


    //task线程执行
    private void updateIpsToDatabase(){
        synchronized (updateIpsSet){

            if(updateIpsSet.size()>0){
                updateIpsList.addAll(updateIpsSet);
                updateIpsToDatabase(updateIpsList);
                updateIpsSet.clear();//存入数据库方法成功后才会清空
                updateIpsList.clear();
            }
        }

    }


    //task线程执行
    private void insertIpsToDatabase(){
        synchronized (insertIpsSet){

            if(insertIpsSet.size()>0){
                insertIpsList.addAll(insertIpsSet);
                insertIpsToDatabase(insertIpsList);
                insertIpsSet.clear();//存入数据库方法成功后才会清空
                insertIpsList.clear();
            }
        }
    }



    public Ip take() {

        if (queue.size() < minIpCatch) {
            synchronized (this){
                if(queue.size()<minIpCatch){
                    DBSingleExecutor.instance.execute(new Database2Queue());
                }
            }
        }
        logger.info("queue size:{}", queue.size());
        return queue.poll();
    }

    /**
     * 添加到待测试queue中
     * @param ips
     */
    public void add(List<Ip> ips){
        queue.addAll(ips);
    }


    private class UpdateTask extends DBSingleExecutor.DBTask{
        @Override
        public void run() {
            updateIpsToDatabase();
        }
    }

    private class InsertTask extends DBSingleExecutor.DBTask {
        @Override
        public void run() {
            insertIpsToDatabase();
        }
    }

    private class Database2Queue extends DBSingleExecutor.DBTask {
        @Override
        public void run() {
            databaseToQueue();
        }
    }
}
