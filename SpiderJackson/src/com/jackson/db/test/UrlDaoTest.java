package com.jackson.db.test;

import com.jackson.db.SqlUtil;
import com.jackson.db.dao.UrlDao;
import com.jackson.db.po.Url;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jackson on 2016/11/6.
 */
public class UrlDaoTest {
    private static Logger logger = LogManager.getLogger(UrlDaoTest.class.getName());
    private static String urlName = "`urltest`";

    @org.junit.Test
    public void createTable(){
        SqlSession sqlSession = getSqlSession();
        UrlDao mapper = sqlSession.getMapper(UrlDao.class);
        mapper.createTable(urlName);
        sqlSession.commit();
    }

    @org.junit.Test
    public synchronized void insertUrls(){
        SqlSession sqlSession = null;
        try{
            long startTime = System.currentTimeMillis();
             sqlSession = getSqlSession();
            UrlDao mapper = sqlSession.getMapper(UrlDao.class);
            mapper.insertUrls(urlName,getUrlList(0,100));
            sqlSession.commit();

            long endTime = System.currentTimeMillis();
            System.out.println("useTime:"+(endTime-startTime));
        }catch (Exception e){
            logger.error(e.toString());
        }finally {
            sqlSession.close();
        }

    }

    @org.junit.Test
    public synchronized void findUrls(){
        SqlSession sqlSession = getSqlSession();
        UrlDao mapper = sqlSession.getMapper(UrlDao.class);
        List<Url> freeMaxPriority = mapper.findUrls("`url3`",80);
        System.out.print(freeMaxPriority.size());
        sqlSession.commit();
        sqlSession.close();
    }



    @org.junit.Test
    public void updateUrlsToComplete(){
        SqlSession sqlSession = getSqlSession();
        UrlDao mapper = sqlSession.getMapper(UrlDao.class);
        mapper.updateUrlsToComplete("`url`",getUrlList(1,10));
        sqlSession.commit();
    }

    @org.junit.Test
    public void updateUrlsToFree(){
        SqlSession sqlSession = getSqlSession();
        UrlDao mapper = sqlSession.getMapper(UrlDao.class);
        //mapper.updateUrlsToFree("`url`",getUrlList(1,10));
        sqlSession.commit();
    }

    @org.junit.Test
    public void threadTest(){
        start(0);
        start(5001);
        start(10001);
        start(15001);
    }

    @org.junit.Test
    public void setTakeState2Free(){
        SqlSession sqlSession = getSqlSession();
        UrlDao mapper = sqlSession.getMapper(UrlDao.class);
        mapper.setTakeState2Free("`url`");
        sqlSession.commit();
    }

    @org.junit.Test
    public void updateUrlsToTakenOut(){
        SqlSession sqlSession = getSqlSession();
        UrlDao mapper = sqlSession.getMapper(UrlDao.class);
        mapper.updateUrlsToTakenOut("`url`",getUrlList(10,10));
        sqlSession.commit();
    }




    private Url getUrl(){
        Url url = Url.newHttpGetUrl("UrlDaoTest");
        url.setCreateTime(124000000);
        url.setState(33);
        url.setPriority(10);
        return url;
    }

    private Url getUrl(int i){
        Url url = Url.newHttpGetUrl("UrlDaoTest");
       // url.setId((long) i);
        url.setCreateTime(124000000);
        url.setState(0);
        url.setPriority(10);
        url.setRequestState(Url.REQUEST_STATE_GET);
        url.setProtocolState(Url.PROTOCOL_STATE_HTTPS);
        url.setUrl("DaoTesTestDaoTesTestDaoTesTestDaoTesTestDaoTesTestDaoTesTestDaoTesTestDaoTe"+i);
        return url;
    }

    private List<Url> getUrlList(int start,int size){
        ArrayList<Url> arr = new ArrayList<>();
        for(int i=0;i<size;i++){
            arr.add(getUrl(start+i));
        }
        return arr;
    }

    private SqlSession getSqlSession() {
        SqlSessionFactory factory = SqlUtil.getFactory();
        return factory.openSession();
    }


    private void start(final int start){
        new Thread(){
            @Override
            public void run() {
                // insertsIgnore();
            }
        }.start();
    }




}
