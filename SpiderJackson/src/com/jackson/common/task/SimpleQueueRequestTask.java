package com.jackson.common.task;

import com.jackson.bean.Bundle;
import com.jackson.bean.ContextSrc;
import com.jackson.common.control.ProxyController;
import com.jackson.common.source.CommonSource;
import com.jackson.db.po.Proxy;
import com.jackson.db.po.Url;
import com.jackson.task.queue.QueueTaskCollection;
import com.jackson.task.queue.RequestQueueTask;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Jackson on 2017/2/10.
 * 串行请求任务。在该任务中url的parser无效果，解析工作通过 onResponseHandle()处理
 * 不会自动回收proxy 和 url
 *
 * 还没写完
 */
@Deprecated
public abstract class SimpleQueueRequestTask extends RequestQueueTask {
    private static Logger logger = LogManager.getLogger(SimpleQueueRequestTask.class.getName());
    private final Url url;

    public void setProxyController(ProxyController proxyController) {
        this.proxyController = proxyController;
    }

    private ProxyController proxyController;

    private final CommonSource source;

    private Proxy lastProxy;
    public SimpleQueueRequestTask(Url url, CommonSource source){
        this.url = url;
        this.source = source;
    }

    @Override
    protected QueueTaskCollection.TaskProcess doTask(Bundle bundle) {
        if(url.getContextSrc()!=null){
            Proxy proxy = url.getContextSrc().getProxy();
            if(proxy!=null && lastProxy!=null){
                if(proxy.equals(lastProxy))
                    url.getContextSrc().setProxy(proxyController.getProxyPool().take());
            }
        }else {
            if(getProxy()!=null && lastProxy!=null){
                if(getProxy().equals(lastProxy)){
                    getProxy().copyOf(proxyController.getProxyPool().take());
                }
            }
        }

        QueueTaskCollection.TaskProcess taskProcess = super.doTask(bundle);
        lastProxy = getProxy();
        return taskProcess;
    }

    @Override
    public HttpRequestBase getHttpRequest() {
        if(getUrl().getRequestState()==Url.REQUEST_STATE_GET){
            return getHttpRequest(source.pollHttpGet(getUrl()),getBundle());
        }
        if(getUrl().getRequestState() == Url.REQUEST_STATE_POST){
            return getHttpRequest(source.pollHttpPost(getUrl()),getBundle());
        }
        throw new RuntimeException("SimpleRequestTask 请求对象获取错误");
    }


    protected abstract HttpRequestBase getHttpRequest(HttpRequestBase request,Bundle bundle);

    @Override
    public void onRequestException(Exception e, Url url, HttpRequestBase httpRequest) {
        logger.error("请求异常 url:{} Exception:{}", url.getUrl(),e.toString());
    }

    @Override
    public void onRequestFinish(boolean isRequestOk, CloseableHttpClient httpClient, HttpRequestBase httpRequest, Url url) {
        if(url.getRequestState() == Url.REQUEST_STATE_GET){
            source.offerHttpGet((HttpGet) httpRequest);
        }
        if(url.getRequestState() == Url.REQUEST_STATE_POST){
            source.offerHttpPost((HttpPost) httpRequest);
        }
    }

    @Override
    public Url getUrl() {
        return url;
    }


    @Override
    protected boolean onStatusCode(int statusCode, CloseableHttpResponse response) {
        if(!(statusCode >= 200 && statusCode<300)){
            if(getProxy()!=null){
                logger.error("请求失败 statusCode:{} url:{} proxy:{}", statusCode,getUrl().getUrl(),getProxy());
            }else {
                logger.error("请求失败 statusCode:{} url:{}", statusCode,getUrl().getUrl());
            }
        }
        return statusCode >= 200 &&statusCode<300;
    }

    @Override
    public ProxyController getProxyController() {
        return proxyController;
    }

    @Override
    protected ContextSrc getContextSrc() {
        return getUrl().getContextSrc();
    }

    @Override
    protected void onParseException(Exception e, Url url, String content) {
        logger.error("解析异常 url:{},parser:{},Exception{},content:{}", url.getUrl(),url.getParserClass(),e.toString(), content);//记录错误日志，方便排查问题，改解析代码
    }



}
