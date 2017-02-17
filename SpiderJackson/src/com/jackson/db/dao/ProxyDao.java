package com.jackson.db.dao;

import com.jackson.db.po.Proxy;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jackson on 2017/1/13.
 */
public interface ProxyDao {

/**
     * 不可重复插入
     * @param proxies
     */
    void insertsIgnore(@Param("tableName")String tableName,@Param("list")List<Proxy> proxies);

    /**
     * 获取可用的,测试次数最少的Proxy
     * @param size 长度
     * @return
     */
    List<Proxy> findFreeMinTestTime(@Param("tableName")String tableName,@Param("size")int size);


    /**
     * 获取可用的,速度最快的Proxy
     * @param size 长度
     * @return
     */
    List<Proxy> findFreeMaxSpeed(@Param("tableName")String tableName,@Param("size")int size);

    void update(@Param("tableName")String tableName,@Param("proxy")Proxy proxy);

    /**
     * 批量更新Proxy 的state字段
     * @param proxies //TODO这里要改
     */
    void update2TakenOut(@Param("tableName")String tableName,@Param("list")List<Proxy> proxies);


    void setTakeState2Free(@Param("tableName")String tableName);

    void createTable(@Param("tableName")String tableName);

}
