package com.jackson.db.test;

import com.jackson.db.SqlUtil;
import com.jackson.db.dao.ProxyDao;
import com.jackson.db.po.Proxy;
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
public class ProxyDaoTest {
    private static Logger logger = LogManager.getLogger(ProxyDaoTest.class.getName());
    private static String tableName = "total_ips";



    @org.junit.Test
    public void createTable(){
        SqlSession sqlSession =null;
        try {
            sqlSession = getSqlSession();
            ProxyDao mapper = sqlSession.getMapper(ProxyDao.class);
            mapper.createTable(tableName);
            sqlSession.commit();
        }catch (Exception e){
            logger.error(e.toString());
        }finally {
            sqlSession.close();
        }
    }





    @org.junit.Test
    public void insertsIgnore() {
        SqlSession sqlSession =null;
        try {
            sqlSession = getSqlSession();
            ProxyDao mapper = sqlSession.getMapper(ProxyDao.class);
            mapper.insertsIgnore(tableName,getIps(0,100));
            sqlSession.commit();
        }catch (Exception e){
            logger.error(e.toString());
        }finally {
            sqlSession.close();
        }
    }

    @org.junit.Test
    public void timeTest(){
        System.out.print(System.currentTimeMillis());
    }

    @org.junit.Test
    public void findFreeMinTestTime() {
        SqlSession sqlSession = getSqlSession();
        ProxyDao mapper = sqlSession.getMapper(ProxyDao.class);
        List<Proxy> freeMinTestTime = mapper.findFreeMinTestTime(tableName,100);
        System.out.println("查到的长度:"+freeMinTestTime.size());
        System.out.println("ip:"+freeMinTestTime.get(0).toString());
        Proxy s = freeMinTestTime.get(0);
        sqlSession.close();
    }

    @org.junit.Test
    public void findFreeMaxSpeed() {
        SqlSession sqlSession = getSqlSession();
        ProxyDao mapper = sqlSession.getMapper(ProxyDao.class);
        List<Proxy> freeMinTestTime = mapper.findFreeMaxSpeed(tableName,100);
        sqlSession.close();
    }

    @org.junit.Test
    public void update() {
        SqlSession sqlSession = getSqlSession();
        ProxyDao mapper = sqlSession.getMapper(ProxyDao.class);

        Proxy ip = Proxy.newHttpProxy("hostsdfsss"+1,22222);
        ip.setId(Long.parseLong("1"));
        mapper.update(tableName,ip);
        sqlSession.commit();
        sqlSession.close();
    }

    @org.junit.Test
    public void updateIpsToTakenOut() {
        SqlSession sqlSession = getSqlSession();
        ProxyDao mapper = sqlSession.getMapper(ProxyDao.class);
        mapper.update2TakenOut(tableName,getIps(0,10));

        sqlSession.commit();
        sqlSession.close();
    }

    @org.junit.Test
    public void setTakeState2Free() {
        SqlSession sqlSession = getSqlSession();
        ProxyDao mapper = sqlSession.getMapper(ProxyDao.class);
        mapper.setTakeState2Free(tableName);

        sqlSession.commit();
        sqlSession.close();
    }

    private Proxy getIp(int index){
        Proxy ip = Proxy.newHttpProxy("hostsss"+index,2101);
        ip.setProtocolState(Proxy.PROTOCOL_STATE_HTTPS);
        return ip;
    }

    private List<Proxy> getIps(int start,int size){
        ArrayList arr = new ArrayList();
        for(int i=0;i<size;i++){
            arr.add(getIp(start+i));
        }
        return arr;
    }

    private SqlSession getSqlSession() {
        SqlSessionFactory factory = SqlUtil.getFactory();
        return factory.openSession();
    }
}
