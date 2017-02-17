package com.jackson.task.timer;

import com.jackson.bean.ContextSrc;
import com.jackson.common.control.ProxyController;
import com.jackson.common.source.CommonSource;
import com.jackson.db.po.Proxy;
import com.jackson.db.po.Url;
import com.jackson.db.service.UrlService;
import com.jackson.reservoir.TimedUrlPool;
import com.jackson.task.RequestParserTask;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Jackson on 2016/12/27.
 * 用来做定时请求的task
 */
public class TimedTask extends RequestParserTask {

    private final TimedUrlPool timedUrlPool;
    private final TimedUrlPool.TimedUrl timedUrl;
    private final ProxyController proxyController;
    private Proxy proxy;
    private final CommonSource source;
    private static Logger logger = LogManager.getLogger(TimedTask.class.getName());

    public TimedTask(TimedUrlPool.TimedUrl timedUrl, TimedUrlPool timedUrlPool,CommonSource source) {
        this(timedUrl,timedUrlPool,null,null,source);
    }


    public TimedTask(TimedUrlPool.TimedUrl timedUrl, TimedUrlPool timedUrlPool, ProxyController proxyController,Proxy proxy,CommonSource source) {
        this.timedUrl = timedUrl;
        this.timedUrlPool = timedUrlPool;
        this.proxyController = proxyController;
        this.proxy = proxy;
        this.source = source;
    }

    @Override
    public HttpRequestBase getHttpRequest() {
        return source.pollHttpGet(getUrl());
    }


    @Override
    protected Url getUrl() {
        return timedUrl.getUrl();
    }

    @Override
    protected Proxy getProxy() {
        return proxy;
    }

    @Override
    protected ProxyController getProxyController() {
        return proxyController;
    }

    @Override
    protected UrlService getUrlService() {
        return null;
    }

    @Override
    protected boolean onStatusCode(int statusCode, CloseableHttpResponse response) {
        if(!(statusCode >= 200 && statusCode<300)){
            logger.error("请求失败 statusCode:{}", statusCode,response);
        }
        return statusCode >= 200 && statusCode<300;
    }

    @Override
    public void onRequestException(Exception e, Url url, HttpRequestBase httpRequest) {
        logger.error("请求异常 url:{} Exception:{}", url.getUrl(),e.toString());
    }

    @Override
    public void onRequestFinish(boolean isRequestOk, CloseableHttpClient httpClient, HttpRequestBase httpRequest, Url url) {
        if (!isRequestOk) {
            //timedUrlPool.offer(timedUrl);//如果失败，就重新放入，等着下次接着用,如果成功等着解析，解析成功后回收
            addImmediateTask(getUrl(),0);
        }
        if(url.getRequestState() == Url.REQUEST_STATE_GET){
            source.offerHttpGet((HttpGet) httpRequest);
        }
        if(url.getRequestState() == Url.REQUEST_STATE_POST){
            source.offerHttpPost((HttpPost) httpRequest);
        }
    }



    @Override
    protected ContextSrc getContextSrc() {
        return null;
    }


    @Override
    protected void onParseException(Exception e, Url url, String content) {
        logger.error("解析异常 url:{},Exception{},content:{}", url.getUrl(),e.toString(), content);//记录错误日志，方便排查问题，改解析代码
    }

    @Override
    protected void onParseFinish(boolean parseSuccess, Url url, String content) {
        //解析失败一次,proxy 就放弃使用


        if (parseSuccess){
            url.setParserFailureTime(0);
            if(proxy!=null){
                proxyController.getProxyPool().offer(proxy);
            }
            timedUrlPool.offer(timedUrl);
        }
        else {
            url.setParserFailureTime(url.getParserFailureTime()+1);
            if(url.getParserFailureTime()>20){
                logger.error("解析失败连续超过20次url:{} parserName:{} content:{}", url.getUrl(),url.getParserClass(), content);//记录错误日志，方便排查问题，改解析代码
                return;//不插入进去了，停止该定时任务
            }

            if(proxy!=null)
                logger.error("解析失败 url:{},proxy host:{} proxy port:{},parserName{}", url.getUrl(),proxy.getHost(),proxy.getPort(), url.getParserClass());//记录错误日志，方便排查问题，改解析代码
            else
                logger.error("解析失败 url:{},parserName{}", url.getUrl(), url.getParserClass());//记录错误日志，方便排查问题，改解析代码
            addImmediateTask(getUrl(),500);
        }
    }


    private void addImmediateTask(Url url ,long delay){
        long useDelay =timedUrl.getDelay()<delay?timedUrl.getDelay():delay;
        timedUrlPool.offer(new Task(url,useDelay));
    }

    private class Task extends TimedUrlPool.TimedUrl{

        private final Url url;
        private final long delay;

        public Task(Url url, long delay) {
            this.url = url;
            this.delay = delay;
        }

        @Override
        public Url getUrl() {
            return url;
        }

        @Override
        public long getDelay() {
            return delay;
        }
    }


}
