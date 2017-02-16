package com.jackson.task.proxy;

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
 * Created by Jackson on 2017/1/18.
 */
public class ProxyValidateOkTask extends RequestParserTask {

    private final Url url;
    private final Proxy proxy;
    private static Logger logger = LogManager.getLogger(ProxyValidateOkTask.class.getName());
    private final CommonSource commonSource;
    private final ProxyController proxyController;

    private long duration;

    public ProxyValidateOkTask(ProxyController proxyController, Url url, Proxy proxy) {
        this.url = url;
        this.proxy = proxy;
        this.proxyController = proxyController;
        commonSource = proxyController.getCommonSource();
    }


    @Override
    public HttpRequestBase getHttpRequest() {
        duration = System.currentTimeMillis();
        if (getUrl().getRequestState() == Url.REQUEST_STATE_GET) {
            return commonSource.pollHttpGet(getUrl());
        }
        if (getUrl().getRequestState() == Url.REQUEST_STATE_POST) {
            return commonSource.pollHttpPost(getUrl());
        }
        throw new RuntimeException("SimpleRequestTask 请求对象获取错误");
    }

    @Override
    public void onRequestException(Exception e, Url url, HttpRequestBase httpRequest) {

    }

    @Override
    public void onRequestFinish(boolean isRequestOk, CloseableHttpClient httpClient, HttpRequestBase httpRequest, Url url) {
        duration = System.currentTimeMillis() - duration;

        if (url.getRequestState() == Url.REQUEST_STATE_GET) {
            commonSource.offerHttpGet((HttpGet) httpRequest);
        }
        if (url.getRequestState() == Url.REQUEST_STATE_POST) {
            commonSource.offerHttpPost((HttpPost) httpRequest);
        }

        long currentTimeMillis = System.currentTimeMillis();
        if (isRequestOk) {
            //记录
            proxy.setLastTestTime(currentTimeMillis);
            proxy.setLastAvailableTime(currentTimeMillis);
            proxy.setSpeed(duration);
            proxy.setAvailableCount(proxy.getAvailableCount() + 1);
            proxy.setTestCount(proxy.getTestCount() + 1);
            proxy.setState(Proxy.STATE_TAKEN_OUT);
            //更新可用表
            proxyController.getProxyOkService().updateProxy(proxy);
            //存入内存中
            proxyController.getProxyPool().offer(proxy);
            logger.info("插入proxy:{}:{} 可用proxy数量:{}", proxy.getHost(), proxy.getPort(), proxyController.getProxyPool().size());
        } else {
            //1、记录
            proxy.setLastTestTime(currentTimeMillis);
            proxy.setTestCount(proxy.getTestCount() + 1);
            proxy.setState(Proxy.STATE_TAKEN_OUT);
            proxyController.getProxyOkService().updateProxy(proxy);
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
        return null;
    }

    @Override
    protected ContextSrc getContextSrc() {
        return null;
    }

    @Override
    protected void onParseException(Exception e, Url url, String content) {

    }

    @Override
    protected void onParseFinish(boolean parseSuccess, Url url, String content) {

    }

    @Override
    protected boolean onStatusCode(int statusCode, CloseableHttpResponse response) {
        logger.debug("satusCode{},{}:{}",statusCode,getProxy().getHost(),getProxy().getPort());
        return statusCode>=200 && statusCode<300;
    }
}
