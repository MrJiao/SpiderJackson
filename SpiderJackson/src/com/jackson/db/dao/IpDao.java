package com.jackson.db.dao;

import com.jackson.db.po.Ip;

import java.util.List;

/**
 * Created by Jackson on 2016/11/4.
 */
@Deprecated
public interface IpDao {

    /**
     * 不可重复插入
     * @param ips
     */
    void insertsIgnore(List<Ip> ips);

    /**
     * 获取可用的,测试次数最少的ip
     * @param size 长度
     * @return
     */
    List<Ip> findFreeMinTestTime(int size);

    void update(Ip ip);

    /**
     * 批量更新Ip 的state字段
     * @param ips
     */
    void updateIpsToTakenOut(List<Ip> ips);



    void setTakeState2Free();

    /**
     * 批量更新Ip 的state字段
     * @param ips
     */
    // void updateIpsToComplete(List<Ip> ips);


    /**
     * 批量更新Ip 的state字段
     * @param Ip
     */
    // void updateIpsToFree(List<Ip> ips);
    //void delete(Ip ip);

    void createTable();
}
