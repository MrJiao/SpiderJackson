package com.jackson.db.dao;

import com.jackson.db.po.Ip;

import java.util.List;

/**
 * Created by Jackson on 2016/11/4.
 */
@Deprecated
public interface IpOkDao {
    void insertOne(Ip ip);
    void insertsIgnore(List<Ip> ips);
    List<Ip> findFreeMinTestTime(int size);

    void update(Ip ip);

    /**
     * 批量更新Ip 的state字段
     * @param ips
     */
    void updateIpsToTakenOut(List<Ip> ips);


    void setTakeState2Free();

    void createTable();
}
