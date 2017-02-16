package com.jackson.reservoir;

import com.jackson.db.po.Proxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Jackson on 2016/11/17.
 * 用来存放可用proxy的池子
 */
public class ProxyPool {
    private static Logger logger = LogManager.getLogger(ProxyPool.class.getName());

    private LinkedBlockingQueue<Proxy> queue = new LinkedBlockingQueue<>();
    public void offer(Proxy proxy){
        if(!queue.contains(proxy))
            queue.offer(proxy);
        logger.debug("插入proxy:host{},port{},可用数量{}",proxy.getHost(),proxy.getPort(), queue.size());
    }

    public Proxy take(){
        Proxy proxy = null;
        try {
            proxy = queue.take();
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
        logger.info("取出proxy host:{} port:{}",proxy.getHost(),proxy.getPort());
        return proxy;
    }


    public Proxy poll(){
        return queue.poll();
    }

    public int size(){
        return queue.size();
    }

}
