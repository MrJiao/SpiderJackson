package com.jackson.db.test;

import com.jackson.db.SqlUtil;
import com.jackson.db.dao.IpOkDao;
import com.jackson.db.po.Ip;
import com.jackson.db.service.IpOkService;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jackson on 2016/11/6.
 */
public class IpOkDaoTest {

    @org.junit.Test
    public void timeTest(){
        long l = System.currentTimeMillis();
        System.out.print(l);
    }

    @org.junit.Test
    public void insertOne() {
        SqlSession sqlSession = getSqlSession();
        IpOkDao mapper = sqlSession.getMapper(IpOkDao.class);
        mapper.insertOne(getIp("192.","0.","0.","1"));
        sqlSession.commit();
        sqlSession.close();
    }


    private static Logger logger = LogManager.getLogger(IpOkService.class.getName());
    @org.junit.Test
    public void insertsIgnore() {
        SqlSession sqlSession =null;
        try {
            sqlSession = getSqlSession();
            IpOkDao mapper = sqlSession.getMapper(IpOkDao.class);
            mapper.insertsIgnore(getIps(1000,1000));
            sqlSession.commit();
        }catch (Exception e){
            logger.error(e.toString());
        }finally {
            sqlSession.close();
        }
    }

    @org.junit.Test
    public void findFreeMinTestTime() {
        SqlSession sqlSession = getSqlSession();
        IpOkDao mapper = sqlSession.getMapper(IpOkDao.class);
        List<Ip> freeMinTestTimel = mapper.findFreeMinTestTime(100);
        System.out.println("查到的长度:"+freeMinTestTimel.size());
        System.out.println("ip:"+freeMinTestTimel.get(0).toString());
        sqlSession.close();
    }

    @org.junit.Test
    public void update() {
        SqlSession sqlSession = getSqlSession();
        IpOkDao mapper = sqlSession.getMapper(IpOkDao.class);
        List<Ip> freeMinTestTime = mapper.findFreeMinTestTime(100);
        for(Ip ip:freeMinTestTime){
            ip.setPort(1111);
            ip.setTestCount(ip.getTestCount()+1);
            mapper.update(ip);
        }

        sqlSession.commit();
        sqlSession.close();
    }

    @org.junit.Test
    public void updateIpsToTakenOut() {
        SqlSession sqlSession = getSqlSession();
        IpOkDao mapper = sqlSession.getMapper(IpOkDao.class);
        mapper.updateIpsToTakenOut(getIps(1618,30));
        sqlSession.commit();
        sqlSession.close();
    }

    private Ip getIp(String a,String b,String c,String d){
        Ip ip = new Ip();
        StringBuilder sb = new StringBuilder().append(a).append(b).append(c).append(d);
        ip.setHost(sb.toString());
        ip.setPort(2120);
        return ip;
    }

    private List<Ip> getIps(int start,int size){
        ArrayList arr = new ArrayList();
        for(int i=0;i<size;i++){
            arr.add(getIp("192.","0.",start+".",""+i));
        }
        return arr;
    }

    private SqlSession getSqlSession() {
        SqlSessionFactory factory = SqlUtil.getFactory();
        return factory.openSession();
    }
}
