package com.jackson.db.service;

import com.jackson.db.SqlUtil;
import com.jackson.db.dao.ProxyDao;
import com.jackson.db.po.Proxy;
import com.jackson.executor.DBSingleExecutor;
import com.jackson.utils.ListUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Jackson on 2017/1/13.
 */
public class ProxyService implements IDaoService<Proxy>{

    private static Logger logger = LogManager.getLogger(ProxyService.class.getName());
    private final SqlSessionFactory factory;
    private final String tableName;

    private final HashSet insertProxysSet;
    private final LinkedList insertProxysList;
    private final HashSet updateProxysSet;
    private final LinkedList updateProxysList;
    private final TakeMethod takeMethod;
    private int proxySize = 100;
    private int minProxyCatch = 20;

    private final LinkedBlockingQueue<Proxy> queue;


    /**
     * 设置每次从数据库读取条数
     * @param proxySize 每次读取条数
     * @return
     */
    public ProxyService setProxySize(int proxySize) {
        this.proxySize = proxySize;
        return this;
    }

    /**
     * 设置最小缓存，如果低于这个值，就去数据库取数据
     * @param minProxyCatch
     * @return
     */
    public ProxyService setMinProxyCatch(int minProxyCatch) {
        this.minProxyCatch = minProxyCatch;
        return this;
    }

    /**
     *
     * @param tableName 创建的表名
     * @param takeMethod 根据什么条件取出数据
     */
    public ProxyService(String tableName,TakeMethod takeMethod) {
        this.tableName = tableName;
        this.takeMethod = takeMethod;
        queue = new LinkedBlockingQueue<>();
        factory = SqlUtil.getFactory();

        insertProxysSet = new HashSet<Proxy>();
        insertProxysList = new LinkedList<Proxy>();

        updateProxysSet = new HashSet<>();
        updateProxysList = new LinkedList<>();

        init();
    }

    public String getTableName(){
        return this.tableName;
    }

    /**
     * 添加到数据库
     * @param proxys
     */
    public void insert(List<Proxy> proxys){
        if(ListUtil.isEmpty(proxys))return;
        synchronized(insertProxysSet){
            //这里的作用是为了唤醒操作queue的线程
            if(queue.size()==0){
                Proxy proxy = proxys.remove(proxys.size() - 1);
                proxy.setState(Proxy.STATE_TAKEN_OUT);
                queue.offer(proxy);
            }

            insertProxysSet.addAll(proxys);
            logger.info("表{} 添加新Proxy size:{} insertProxysSet size{}",tableName,proxys.size(),insertProxysSet.size());
            DBSingleExecutor.instance.execute(new InsertTask());

        }
    }

    /**
     * 添加到数据库
     * @param proxy
     */
    public void insert(Proxy proxy){
        synchronized(insertProxysSet){
            if(queue.size()==0){
                //这里的作用是为了唤醒操作queue的线程
                proxy.setState(Proxy.STATE_TAKEN_OUT);
                queue.offer(proxy);
            }
            insertProxysSet.add(proxy);
            logger.debug("表{} 添加新ProxyinsertProxysSet size{}",tableName,insertProxysSet.size());
            DBSingleExecutor.instance.execute(new InsertTask());

        }
    }


    private void init(){
        createTable();
        setTask2Free();
    }

    private void createTable() {
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            ProxyDao mapper = sqlSession.getMapper(ProxyDao.class);
            mapper.createTable(tableName);
            sqlSession.commit();
        } catch (Exception e){
            logger.error("表{} {}",tableName,e.toString());
        } finally{
            sqlSession.close();
        }
    }

    private void setTask2Free() {
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            ProxyDao mapper = sqlSession.getMapper(ProxyDao.class);
            mapper.setTakeState2Free(tableName);
            sqlSession.commit();
        } catch (Exception e){
            logger.error("表{} {}",tableName,e.toString());
        } finally{
            sqlSession.close();
        }
    }


    /**
     * 存入数据库
     * @param Proxys
     */
    private void insertProxysToDatabase(List<Proxy> Proxys) {
        logger.info("表{} Proxy存入数据库 insertProxysSet size:{}",tableName, Proxys.size());
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            ProxyDao mapper = sqlSession.getMapper(ProxyDao.class);
            mapper.insertsIgnore(tableName,Proxys);
            sqlSession.commit();
        } catch (Exception e) {
            logger.error("表{} {}",tableName,e.toString());
        } finally {
            sqlSession.close();
        }
    }




    private  void updateProxysToDatabase(List<Proxy> Proxys) {
        logger.info("表{} 更新Proxy size is {}",tableName, updateProxysSet.size());
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            ProxyDao mapper = sqlSession.getMapper(ProxyDao.class);
            for (Proxy ProxyTemp : Proxys) {
                mapper.update(tableName,ProxyTemp);
            }
            sqlSession.commit();
        } catch (Exception e) {
            logger.error("表{} {}",tableName,e.toString());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 带有id的proxy才能被更新，也就是说只有从数据库里读取出来的proxy调用此方法才有效，不建议手动设置id
     * @param Proxy
     */
    public void updateProxy(Proxy Proxy) {
        logger.debug("表{} 更新Proxy Proxy:{}:{}",tableName,Proxy.getHost(),Proxy.getPort());
        synchronized (updateProxysSet){
            updateProxysSet.add(Proxy);
        }
        DBSingleExecutor.instance.execute(new UpdateTask());
    }


    //task线程执行
    private void updateProxysToDatabase(){
        synchronized (updateProxysSet){

            if(updateProxysSet.size()>0){
                updateProxysList.addAll(updateProxysSet);
                updateProxysToDatabase(updateProxysList);
                updateProxysSet.clear();//存入数据库方法成功后才会清空
                updateProxysList.clear();
            }
        }

    }


    //task线程执行
    private void insertProxysToDatabase(){
        synchronized (insertProxysSet){

            if(insertProxysSet.size()>0){
                insertProxysList.addAll(insertProxysSet);
                insertProxysToDatabase(insertProxysList);
                insertProxysSet.clear();//存入数据库方法成功后才会清空
                insertProxysList.clear();
            }
        }
    }


    /**
     * 非阻塞
     * @return
     */
    public Proxy take() {
        return poll();
    }


    private Proxy poll(){
        if (queue.size() < minProxyCatch) {
            synchronized (this){
                if(queue.size()<minProxyCatch){
                    DBSingleExecutor.instance.execute(new Database2Queue(takeMethod));
                }
            }
        }
        logger.info("表{} queue size:{}",tableName, queue.size());
        return queue.poll();
    }

    private class UpdateTask extends DBSingleExecutor.DBTask{
        @Override
        public void run() {
            updateProxysToDatabase();
        }
    }

    private class InsertTask extends DBSingleExecutor.DBTask {
        @Override
        public void run() {
            insertProxysToDatabase();
        }
    }

    private class Database2Queue extends DBSingleExecutor.DBTask {
        private final TakeMethod method;

        public Database2Queue(TakeMethod method) {
            this.method = method;
        }

        @Override
        public void run() {
            databaseToQueue(method);
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
        void handle(List<Proxy> list,LinkedBlockingQueue queue);
    }

    /**
     * 从数据库读取proxy 到测试队列中
     * @param method
     */
    private synchronized void databaseToQueue(TakeMethod method) {
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            ProxyDao mapper = sqlSession.getMapper(ProxyDao.class);
            List<Proxy> proxys = method.getProxyFromDatabase(tableName,mapper,proxySize);
            logger.debug("表{} 数据库取出的 Proxys size:{}",tableName, proxys.size());
            if(handler!=null)handler.handle(proxys,queue);
            if (proxys.size() > 0) {
                mapper.update2TakenOut(tableName,proxys);
                sqlSession.commit();
                queue.addAll(proxys);
            }
            logger.debug("表{} 当前缓存proxys queue size:{}",tableName, queue.size());
        } catch (Exception e) {
            logger.error("表{} error:{}",tableName,e.toString());
        } finally {
            if (sqlSession != null)
                sqlSession.close();
        }
    }

    public enum TakeMethod{
        /**
         * 取出测试次数最少的
         */
        MIN_TEST_TIME {
            @Override
            public List<Proxy> getProxyFromDatabase(String tableName, ProxyDao mapper, int size) {
                return mapper.findFreeMinTestTime(tableName, size);
            }
        },
        /**
         * 取出速度最快的
         */
        MAX_SPEED{
            @Override
            public List<Proxy> getProxyFromDatabase(String tableName, ProxyDao mapper, int size) {
                return mapper.findFreeMaxSpeed(tableName,size);
            }
        };
        abstract List<Proxy> getProxyFromDatabase(String tableName, ProxyDao mapper, int size);
    }
}
