package com.jackson.db.service;

import com.jackson.db.SqlUtil;
import com.jackson.db.dao.UrlDao;
import com.jackson.db.po.Url;
import com.jackson.executor.DBSingleExecutor;
import com.jackson.utils.ListUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by Jackson on 2016/11/4.
 */
public class UrlService implements IDaoService<Url> {
    private static Logger logger = LogManager.getLogger(UrlService.class.getName());
    private final SqlSessionFactory factory;
    private LinkedBlockingQueue<Url> queue;
    private final HashSet<Url> updateCompleteUrsSet;
    private final HashSet<Url> insertUrsSet;
    private final LinkedList<Url> insertUrsList;
    private LinkedList<Url> updateCompleteUrsList;

    private final String tableName;
    private int minUrlCatch = 10;
    private int getUrlSize = 100;

    /**
     *
     * @param tableName 表名
     */
    public UrlService(String tableName) {
        this.tableName = tableName;
        queue = new LinkedBlockingQueue<>();
        factory = SqlUtil.getFactory();
        updateCompleteUrsSet = new HashSet<>();
        updateCompleteUrsList = new LinkedList<>();

        insertUrsSet = new HashSet<>();
        insertUrsList = new LinkedList<>();
        init();
    }

    public UrlService setMinUrlCatch(int minUrlCatch) {
        this.minUrlCatch = minUrlCatch;
        return this;
    }

    /**
     * 每次从数据库获取url的条数
     * @param getUrlSize
     * @return
     */
    public UrlService setGetUrlSize(int getUrlSize) {
        this.getUrlSize = getUrlSize;
        return this;
    }

    public  void insert(List<Url> urls){
        if(ListUtil.isEmpty(urls))return;
        synchronized(insertUrsSet){
            if(queue.size()==0){
                Url url = urls.remove(urls.size() - 1);
                queue.offer(url);
            }
            insertUrsSet.addAll(urls);
            DBSingleExecutor.instance.execute(new InsertTask());
        }
    }

    public void insert(Url url){
        synchronized(insertUrsSet){
            if(queue.size()==0){
                url.setState(Url.STATE_TAKE_OUT);
                queue.offer(url);
            }
            insertUrsSet.add(url);
            DBSingleExecutor.instance.execute(new InsertTask());
        }
    }



    private void insertUrl2Database(List<Url> urls) {
        logger.info("插入url到数据库 urlSize:{}", urls.size());
        SqlSession sqlSession = null;

        try {
            sqlSession = factory.openSession();
            UrlDao mapper = sqlSession.getMapper(UrlDao.class);
            mapper.insertUrls(tableName,urls);
            sqlSession.commit();
        } catch (Exception e) {
            logger.error(e.toString());
        }finally {
            if (sqlSession != null)
                sqlSession.close();
        }
    }

    public Url take() {
        logger.info("queue size:{}", queue.size());

        if (queue.size() < minUrlCatch) {
            synchronized (this){
                if(queue.size()<minUrlCatch){
                    //databaseToQueue();
                    DBSingleExecutor.instance.execute(new Database2QueueTask());
                }
            }
        }
        Url temp = null;
        try {
            temp = queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return temp;
    }

//task线程执行
    protected void insertUrlsToDatabase(){
        synchronized (insertUrsSet){
            if(insertUrsSet.size()>0){
                insertUrsList.addAll(insertUrsSet);
                insertUrl2Database(insertUrsList);
                insertUrsSet.clear();//存入数据库方法成功后才会清空
                insertUrsList.clear();//存入数据库方法成功后才会清空
            }
        }
    }


//task线程执行
    protected void completeUrlsToDatabase(){
        synchronized (updateCompleteUrsSet){
            if(updateCompleteUrsSet.size()>0){
                updateCompleteUrsList.addAll(updateCompleteUrsSet);
                completeUrlsToDatabase(updateCompleteUrsList);
                updateCompleteUrsSet.clear();//存入数据库方法成功后才会清空
                updateCompleteUrsList.clear();
            }
        }

    }


    private void completeUrlsToDatabase(List<Url> completeUrls) {
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession(ExecutorType.BATCH);
            UrlDao mapper = sqlSession.getMapper(UrlDao.class);
            logger.info("完成的url存入数据库completeUrls size:{}", completeUrls.size());
            mapper.updateUrlsToComplete(tableName,completeUrls);
            sqlSession.commit();
        } catch (Exception e) {
            logger.error(e.toString());
        }finally {
            sqlSession.close();
        }

    }


    private void databaseToQueue() {
        if(queue.size()>minUrlCatch)return;
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            UrlDao mapper = sqlSession.getMapper(UrlDao.class);
            List<Url> urls = mapper.findUrls(tableName,getUrlSize);
            logger.info("从数据库获取 urls size:{}", urls.size());
            if (urls.size() > 0) {
                queue.addAll(urls);
                mapper.updateUrlsToTakenOut(tableName,urls);
                sqlSession.commit();
            }
            logger.info("addAfter queue size:{}", queue.size());
        } catch (Exception e) {
            logger.error(e.toString());
        }finally {
            if (sqlSession != null)
                sqlSession.close();
        }
    }


    public void completeUrl(Url url) {
        url.setState(Url.STATE_COMPLETE);
        synchronized (updateCompleteUrsSet){
            updateCompleteUrsSet.add(url);
            DBSingleExecutor.instance.execute(new CompleteTask());
        }

        logger.info("设置完成状态 updateCompleteUrsSet size:{} url:{}", updateCompleteUrsSet.size(),url.getUrl());
    }

    /**
     * 放入queue，等待被取出
     * @param url
     */
    public void add(Url url) {
        logger.info("放入内存queue中 url is {}",url);
        queue.offer(url);

    }

    /**
     * 放入queue，等待被取出
     * @param urls
     */
    public void add(List<Url> urls) {
        logger.info("放入内存queue中 url size {}",urls.size());
        queue.addAll(urls);
    }

    /**
     * 将上次取出但是没执行的,变成free状态
     */
    private void init(){
        createTable();
        setTakeState2Free();
    }

    private void setTakeState2Free(){
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            UrlDao mapper = sqlSession.getMapper(UrlDao.class);
            mapper.setTakeState2Free(tableName);
            sqlSession.commit();
        } catch (Exception e){
            logger.error(e.toString());
        } finally{
            sqlSession.close();
        }
    }

    private void createTable(){
        SqlSession sqlSession = null;
        try {
            sqlSession = factory.openSession();
            UrlDao mapper = sqlSession.getMapper(UrlDao.class);
            mapper.createTable(tableName);
            sqlSession.commit();
        } catch (Exception e){
            logger.error(e.toString());
        } finally{
            sqlSession.close();
        }
    }

    private class InsertTask extends DBSingleExecutor.DBTask{

        @Override
        public void run() {
            insertUrlsToDatabase();
        }
    }

    private class CompleteTask extends DBSingleExecutor.DBTask{

        @Override
        public void run() {
            completeUrlsToDatabase();
        }
    }

    private class Database2QueueTask extends DBSingleExecutor.DBTask{

        @Override
        public void run() {
            databaseToQueue();
        }
    }
}
