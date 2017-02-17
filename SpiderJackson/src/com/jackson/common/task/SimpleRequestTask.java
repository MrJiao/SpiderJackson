package com.jackson.common.task;

import com.jackson.bean.ContextSrc;
import com.jackson.common.control.ProxyController;
import com.jackson.common.source.CommonSource;
import com.jackson.db.po.Proxy;
import com.jackson.db.po.Url;
import com.jackson.db.service.UrlService;
import com.jackson.task.RequestParserTask;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Jackson on 2017/1/11.
 * 当请求失败，将url重放入，下次换了proxy 再请求
 * 当请求成功，解析不成功时，不会处理
 * 当请求成功，解析成功时，会将url设置成完成状态
 */
public class SimpleRequestTask  extends RequestParserTask{

    private static Logger logger = LogManager.getLogger(SimpleRequestTask.class.getName());
    private final UrlService urlService;
    private final Url url;
    private final ProxyController proxyController;
    private Proxy proxy;
    private final CommonSource source;

    //proxyController 传入的作用是回收proxy
    public SimpleRequestTask(Proxy proxy,ProxyController proxyController, Url url, UrlService urlService,CommonSource source){
        this.url = url;
        this.proxy = proxy;
        this.proxyController = proxyController;
        this.source = source;
        this.urlService = urlService;
    }

    public SimpleRequestTask(Url url, UrlService urlService,CommonSource source){
        this(null,null,url,urlService,source);
    }

    public SimpleRequestTask(Url url,CommonSource source){
        this(null,null,url,null,source);
    }


    @Override
    public HttpRequestBase getHttpRequest() {
        if(getUrl().getRequestState()==Url.REQUEST_STATE_GET){
            return source.pollHttpGet(getUrl());
        }
        if(getUrl().getRequestState() == Url.REQUEST_STATE_POST){
           return source.pollHttpPost(getUrl());
        }
        throw new RuntimeException("SimpleRequestTask 请求对象获取错误");
    }

    @Override
    public void onRequestException(Exception e, Url url, HttpRequestBase httpRequest) {
        if(getProxy()!=null){
            logger.error("请求失败 url:{} proxy:{}:{} Exception:{}", url.getUrl(),getProxy().getHost(),getProxy().getPort(),e.toString());
        }else {
            logger.error("请求失败 url:{} Exception:{}", url.getUrl(),e.toString());
        }
    }

    @Override
    public void onRequestFinish(boolean isRequestOk, CloseableHttpClient httpClient, HttpRequestBase httpRequest, Url url) {
        if (!isRequestOk) {
            if(getUrlService()!=null)
                getUrlService().add(getUrl());//如果失败，就重新放入数据库，等着下次接着用,如果成功等着解析，解析成功后回收
        }
        if(url.getRequestState() == Url.REQUEST_STATE_GET){
            source.offerHttpGet((HttpGet) httpRequest);
        }
        if(url.getRequestState() == Url.REQUEST_STATE_POST){
            source.offerHttpPost((HttpPost) httpRequest);
        }
    }

    @Override
    protected Url getUrl() {
        return url;
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
        return urlService;
    }

    @Override
    protected boolean onStatusCode(int statusCode, CloseableHttpResponse response) {
        if(!(statusCode >= 200 && statusCode<300)){
            if(getProxy()!=null){
                logger.error("请求失败 statusCode:{} url:{} proxy:{}", statusCode,getUrl().getUrl(),proxy);
            }else {
                logger.error("请求失败 statusCode:{} url:{}", statusCode,getUrl().getUrl());
            }
        }
        return statusCode >= 200 &&statusCode<300;
    }

    @Override
    protected ContextSrc getContextSrc() {
        return getUrl().getContextSrc();
    }

    @Override
    protected void onParseException(Exception e, Url url, String content) {
        logger.error("解析异常 url:{},parser:{},Exception{},content:{}", url.getUrl(),url.getParserClass(),e.toString(), content);//记录错误日志，方便排查问题，改解析代码
    }

    @Override
    protected void onParseFinish(boolean parseSuccess, Url url, String content) {
        if (parseSuccess){
            if(getUrlService()!=null)
                getUrlService().completeUrl(getUrl());//设置成完成状态
            if(proxy!=null && proxyController!=null){
                proxyController.getProxyPool().offer(proxy);
            }
        }
    }


}
