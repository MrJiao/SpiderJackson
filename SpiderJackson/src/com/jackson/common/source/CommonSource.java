package com.jackson.common.source;

import com.jackson.db.po.Url;
import com.jackson.reservoir.HttpGetPool;
import com.jackson.reservoir.HttpPostPool;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Jackson on 2016/12/20.
 */
public class CommonSource {


    private HttpGetPool httpGetPool;
    private static Logger logger = LogManager.getLogger(CommonSource.class.getName());
    private HttpPostPool httpPostPool;

    private CommonSource(){}

    public static CommonSource newInstance(){
        return new CommonSource();
    }

    public HttpRequestBase pollHttpGet(Url url){
        createHttpGetPool();
        return httpGetPool.poll(url);
    }
    

    public void offerHttpGet(HttpGet request){
        createHttpGetPool();
        httpGetPool.offer(request);
    }

    public HttpGetPool getHttpGetPool(){
        createHttpGetPool();
        return httpGetPool;
    }


    public HttpPostPool getHttpPostPool(){
        createHttpPostPool();
        return httpPostPool;
    }

    public HttpPost pollHttpPost(Url url){
        createHttpPostPool();
        return httpPostPool.poll(url);
    }

    public void offerHttpPost(HttpPost httpPost){
        createHttpPostPool();
        httpPostPool.offer(httpPost);
    }

    /**
     * 线程池
     */
    private ScheduledThreadPoolExecutor executor;
    public ScheduledThreadPoolExecutor getThreadPool() {
        if (executor == null) {
            synchronized (ScheduledThreadPoolExecutor.class){
                if(executor==null){
                    logger.debug("创建 ThreadPool");
                    executor = new ScheduledThreadPoolExecutor(10);
                }
            }
        }
        return executor;
    }

    private void createHttpGetPool(){
        if(httpGetPool == null){
            synchronized (HttpGetPool.class){
                if(httpGetPool == null){
                    logger.debug("创建 HttpGetPool");
                    httpGetPool = new HttpGetPool();
                }
            }
        }
    }

    private void createHttpPostPool() {
        if(httpPostPool == null){
            synchronized (HttpPostPool.class){
                if(httpPostPool == null){
                    logger.debug("创建 HttpPostPool");
                    httpPostPool = new HttpPostPool();
                }
            }
        }
    }

}
