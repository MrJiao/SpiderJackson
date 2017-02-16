package com.jackson.reservoir;

import com.jackson.db.po.Url;
import com.jackson.utils.StringUtil;
import org.apache.http.HeaderIterator;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.LinkedList;

/**
 * Created by Jackson on 2016/10/27.
 */
public class HttpPostPool {

    private static Logger logger = LogManager.getLogger(HttpPostPool.class.getName());
    private final LinkedList<HttpPost> pool;
    private HttpPostConfigHandler handler;
    private final static RequestConfigPool<HttpPost> requestConfigPool = new RequestConfigPool();
    public HttpPostPool() {
        pool = new LinkedList<>();
    }

    public synchronized HttpPost poll(Url url) {
        if(url.getRequestState()!=Url.REQUEST_STATE_POST){
            throw new RuntimeException("url 不为post请求，不能获取httpPost");
        }
        logger.info("获取HttpPost url:{}", url.getUrl());
        HttpPost httpPost = null;

        if (pool.size() == 0) {
            httpPost = new HttpPost(url.getUrl());
        } else {
            httpPost = pool.poll();
            httpPost.setURI(URI.create(url.getUrl()));
        }
        if(handler!=null)
            handler.setConfig(httpPost,url);
        if(url.getRequestConfigClass()!=null){
            httpPost = requestConfigPool.getRequestConfig(url.getRequestConfigClass()).setConfig(httpPost,url);
        }
        return httpPost;
    }

    public synchronized void offer(HttpPost httpPost) {
        httpPost.setURI(null);//插入时清空数据
        httpPost.setEntity(null);
        httpPost.setConfig(null);
        clearHeader(httpPost);
        pool.offer(httpPost);
        logger.debug("回收HttpPost{},当前HttpPostPool里HttpPost的数量为:{}", httpPost.toString(), pool.size());
    }

    private void clearHeader(HttpPost httpPost){
        for (final HeaderIterator i = httpPost.headerIterator(); i.hasNext(); ) {
            i.nextHeader();
            i.remove();
        }
    }

    public interface HttpPostConfigHandler {
        /**
         * 如果同时有设置了handler 和 Url里的postConfigClassName ，会先执行 handler的设置再执行 postConfigClassName里的设置
         * @param httpPost
         * @param url
         * @return
         */
        HttpPost setConfig(HttpPost httpPost, Url url);
    }

    public synchronized HttpPostPool setConfigHandler(HttpPostConfigHandler handler) {
        this.handler = handler;
        return this;
    }

}
