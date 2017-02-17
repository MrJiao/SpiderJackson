package com.jackson.reservoir;

import com.jackson.db.po.Url;
import com.jackson.utils.StringUtil;
import org.apache.http.HeaderIterator;
import org.apache.http.client.methods.HttpGet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.LinkedList;

/**
 * Created by Jackson on 2016/10/27.
 */
public class HttpGetPool {

    private static Logger logger = LogManager.getLogger(HttpGetPool.class.getName());
    private final LinkedList<HttpGet> pool;
    private HttpGetConfigHandler handler;
    private final static RequestConfigPool<HttpGet> requestConfigPool = new RequestConfigPool();
    public HttpGetPool() {
        pool = new LinkedList<>();
    }

    public synchronized HttpGet poll(Url url) {
        if(url.getRequestState()!=Url.REQUEST_STATE_GET){
            throw new RuntimeException("url 不为get请求，不能获取httpGet");
        }
        logger.info("获取HttpGet url:{}", url);
        HttpGet httpGet = null;

        if (pool.size() == 0) {
            httpGet = new HttpGet(url.getUrl());
        } else {
            httpGet = pool.poll();
            httpGet.setURI(URI.create(url.getUrl()));
        }

        if (handler != null)
            httpGet = handler.setConfig(httpGet, url);
        if(url.getRequestConfigClass()!=null){
            httpGet = requestConfigPool.getRequestConfig(url.getRequestConfigClass()).setConfig(httpGet,url);
        }
        return httpGet;
    }

    public synchronized void offer(HttpGet httpGet) {
        logger.debug("回收HttpGet{},当前HttpGetPool里HttpGet的数量为:{}", httpGet.toString(), pool.size());
        httpGet.setURI(null);
        httpGet.setConfig(null);
        clearHeader(httpGet);
        pool.offer(httpGet);
    }

    private void clearHeader(HttpGet httpGet){
        for (final HeaderIterator i = httpGet.headerIterator(); i.hasNext(); ) {
            i.nextHeader();
            i.remove();
        }
    }


    public synchronized HttpGetPool setConfigHandler(HttpGetConfigHandler handler) {
        this.handler = handler;
        return this;
    }


    public interface HttpGetConfigHandler {
        HttpGet setConfig(HttpGet httpGet, Url url);
    }

}
