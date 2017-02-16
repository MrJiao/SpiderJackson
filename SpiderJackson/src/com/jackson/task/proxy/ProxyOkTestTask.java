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
public class ProxyOkTestTask extends RequestTask {

    private static final String urlStr = "http://www.baidu.com";
    private static final  Url url = Url.newHttpGetUrl(urlStr);
    private final Proxy proxy;
    private static Logger logger = LogManager.getLogger(ProxyOkTestTask.class.getName());
    private final CommonSource commonSource;
    private final ProxyController proxyController;

    public ProxyOkTestTask(ProxyController proxyController,Proxy proxy) {
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
            if(proxy.getId()==0){//id为0 代表是直接存入内存进行测试的proxy
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
                //更新可用表
                proxyController.getProxyOkService().updateProxy(proxy);
            }
            //存入内存中
            proxyController.getProxyPool().offer(proxy);
            logger.info("插入proxy:{}:{} 可用proxy数量:{}",proxy.getHost(),proxy.getPort(),proxyController.getProxyPool().size());
        }else {
            if(proxy.getId()==0){
                proxyController.getProxyService().insert(proxy);
            }else {
                //1、记录
                proxy.setLastTestTime(currentTimeMillis);
                proxy.setTestCount(proxy.getTestCount()+1);
                proxy.setState(Proxy.STATE_TAKEN_OUT);
                proxyController.getProxyOkService().updateProxy(proxy);
            }
        }
    }

    @Override
    public void onRequestException(Exception e, Url url, HttpRequestBase httpRequest) {
        //logger.error("exception{}",e);
    }

    @Override
    protected boolean onStatusCode(int statusCode, CloseableHttpResponse response) {
        logger.debug("测试proxy satusCode{},{}:{}",statusCode,getProxy().getHost(),getProxy().getPort());
        return statusCode>=200 && statusCode<300;
    }

    @Override
    protected void onInit() {

    }
}
