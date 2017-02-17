package com.jackson.db.test;

import com.jackson.db.SqlUtil;
import com.jackson.db.dao.IpDao;
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
public class IpDaoTest {
    private static Logger logger = LogManager.getLogger(IpOkService.class.getName());
    @org.junit.Test
    public void insertsIgnore() {
        SqlSession sqlSession =null;
        try {
            sqlSession = getSqlSession();
            IpDao mapper = sqlSession.getMapper(IpDao.class);
            mapper.insertsIgnore(getIps(1000,1000));
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
        IpDao mapper = sqlSession.getMapper(IpDao.class);
        List<Ip> freeMinTestTime = mapper.findFreeMinTestTime(100);

        sqlSession.close();
    }

    @org.junit.Test
    public void update() {
        SqlSession sqlSession = getSqlSession();
        IpDao mapper = sqlSession.getMapper(IpDao.class);

        Ip ip = new Ip();
        ip.setHost("hostsss"+1);
        ip.setPort(2222);
        ip.setId(Long.parseLong("1599"));
        mapper.update(ip);
        sqlSession.commit();
        sqlSession.close();
    }

    private Ip getIp(int index){
        Ip ip = new Ip();
        ip.setHost("hostsss"+index);
        ip.setPort(2120);
        return ip;
    }

    private List<Ip> getIps(int start,int size){
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
