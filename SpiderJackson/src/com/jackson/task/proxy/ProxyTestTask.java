package com.jackson.task.proxy;

import com.jackson.common.source.CommonSource;
import com.jackson.common.control.ProxyController;
import com.jackson.db.po.Proxy;
import com.jackson.db.po.Url;
import com.jackson.task.RequestTask;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Jackson on 2016/11/17.
 */
@Deprecated
public class ProxyTestTask extends RequestTask {

    private static final String urlStr = "http://www.111cn.net";
    private static final  Url url = Url.newHttpGetUrl(urlStr);
    private final Proxy proxy;

    private static Logger logger = LogManager.getLogger(ProxyTestTask.class.getName());
    private final CommonSource commonSource;
    private final ProxyController proxyController;

    public ProxyTestTask(ProxyController proxyController,Proxy proxy) {
        this.proxy=proxy;
        this.proxyController = proxyController;
        commonSource = proxyController.getCommonSource();
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
    public HttpRequestBase getHttpRequest() {
        return commonSource.pollHttpGet(getUrl());
    }



    @Override
    public void onRequestFinish(boolean isRequestOk, CloseableHttpClient httpClient, HttpRequestBase httpRequest, Url url) {
        commonSource.offerHttpGet((HttpGet) httpRequest);
        long currentTimeMillis = System.currentTimeMillis();
        if(isRequestOk){

            if(proxy.getId()==0){
                proxyController.getProxyOkService().insert(proxy);
                proxyController.getProxyService().insert(proxy);
            }else {
                //记录
                proxy.setLastTestTime(currentTimeMillis);
                proxy.setLastAvailableTime(currentTimeMillis);
                proxy.setSpeed(getSpeed());
                proxy.setAvailableCount(proxy.getAvailableCount()+1);
                proxy.setTestCount(proxy.getTestCount()+1);
                proxy.setState(Proxy.STATE_TAKEN_OUT);
                //存入内存中
                proxyController.getProxyPool().offer(proxy);
                logger.info("插入proxy:{}:{} 可用proxy数量:{}",proxy.getHost(),proxy.getPort(),proxyController.getProxyPool().size());
                //存到可用表
                proxyController.getProxyOkService().insert(proxy);
                proxyController.getProxyService().updateProxy(proxy);
            }
        }else {
            if(proxy.getId()==0){
                proxyController.getProxyService().insert(proxy);
            }else {
                //1、记录
                proxy.setLastTestTime(currentTimeMillis);
                proxy.setTestCount(proxy.getTestCount()+1);
                proxy.setState(Proxy.STATE_TAKEN_OUT);
                proxyController.getProxyService().updateProxy(proxy);
            }
        }

    }

    @Override
    public void onRequestException(Exception e, Url url, HttpRequestBase httpRequest) {

    }

    @Override
    protected boolean onStatusCode(int statusCode, CloseableHttpResponse response) {
        logger.info("satusCode{},{}:{}",statusCode,getProxy().getHost(),getProxy().getPort());
        return statusCode>=200 && statusCode<300;
    }

    @Override
    protected void onInit() {

    }
}
