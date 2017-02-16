package com.jackson.db.dao;

import com.jackson.db.po.Url;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jackson on 2016/11/4.
 */
public interface UrlDao {
    /**
     * 批量插入url，相同的url 就不存 直接忽略
     * url.state 默认为 Url.STATE_FREE
     * url.createTime 数据库自己存
     * url.id 数据库自己存
     * @param urls
     */
    void insertUrls(@Param("tableName")String tableName,@Param("list")List<Url> urls);


    /**
     * 获取urls，按优先级高的优先取出,同时更改取出状态
     * @param size 取出的数量
     * @return
     */
    List<Url> findUrls(@Param("tableName")String tableName,@Param("size")int size);


    /**
     * 批量更新URL 的state字段
     * @param urls
     */
    void updateUrlsToComplete(@Param("tableName")String tableName,@Param("list")List<Url> urls);


    /**
     * 批量更新URL 的state字段
     * @param urls
     */
    //void updateUrlsToFree(@Param("tableName")String tableName,@Param("list")List<Url> urls);

    /**
     * 批量更新URL 的state字段
     * @param urls
     */
    void updateUrlsToTakenOut(@Param("tableName")String tableName,@Param("list")List<Url> urls);

    /**
     * 将去状态变成可用状态
     */
    void setTakeState2Free(@Param("tableName")String tableName);



    void createTable(@Param("tableName")String tableName);
}
