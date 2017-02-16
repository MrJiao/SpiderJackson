package com.jackson.db;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jackson on 2016/11/5.
 */
public class SqlUtil {


    private static SqlSessionFactory factory;

    public static SqlSessionFactory getFactory(){
        if(factory ==null){
            try {
                factory = initFactory();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return factory;
    }

    private static SqlSessionFactory initFactory() throws IOException {
        String resource = "config/SqlMapConfig.xml"; //mybatis配置文件
        //得到配置文件的流
        InputStream inputStream = Resources.getResourceAsStream(resource);
        //创建会话工厂SqlSessionFactory,要传入mybaits的配置文件的流
        return new SqlSessionFactoryBuilder().build(inputStream);
    }
}
