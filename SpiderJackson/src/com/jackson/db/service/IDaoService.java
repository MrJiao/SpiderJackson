package com.jackson.db.service;

import com.jackson.db.po.Url;

import java.util.List;

/**
 * Created by Jackson on 2016/11/16.
 */
public interface IDaoService<E> {

    /**
     * 如果需要记录取出状态的，通过数据库层直接修改。
     * @return
     */
    E take();




}
