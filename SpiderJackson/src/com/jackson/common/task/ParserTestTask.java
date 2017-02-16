package com.jackson.common.task;

import com.jackson.bean.ContextSrc;
import com.jackson.common.control.ProxyController;
import com.jackson.common.source.CommonSource;
import com.jackson.common.source.GlobalSource;
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
 * Created by Jackson on 2016/11/7.
 */
public class ParserTestTask extends RequestParserTask {


    private static Logger logger = LogManager.getLogger(ParserTestTask.class.getName());

    private Proxy proxy;
    private final CommonSource commonSource;
    private final Url url;
    public ParserTestTask(Proxy proxy, Url url) {
        this.url = url;
        this.proxy = proxy;
        commonSource = CommonSource.newInstance();

    }

    public ParserTestTask(Url url) {
        this.url = url;
        commonSource = CommonSource.newInstance();

    }

    @Override
    public HttpRequestBase getHttpRequest() {
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
        logger.error("请求异常 url:{} Exception:{}", url.getUrl(),e.toString());
    }

    @Override
    public void onRequestFinish(boolean isRequestOk, CloseableHttpClient httpClient, HttpRequestBase httpRequest, Url url) {
        if (!isRequestOk) {
            logger.error("请求失败 url{}",url.getUrl());
        }

        if (url.getRequestState() == Url.REQUEST_STATE_GET) {
            commonSource.offerHttpGet((HttpGet) httpRequest);
        }
        if (url.getRequestState() == Url.REQUEST_STATE_POST) {
            commonSource.offerHttpPost((HttpPost) httpRequest);
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
        return null;
    }


    @Override
    protected UrlService getUrlService() {
        return null;
    }

    @Override
    protected boolean onStatusCode(int statusCode, CloseableHttpResponse response) {
        return statusCode >= 200 && statusCode<300;
    }

    @Override
    protected ContextSrc getContextSrc() {
        return null;
    }


    @Override
    protected void onParseException(Exception e, Url url, String content) {
        logger.error("解析异常 url:{},Exception{}", url.getUrl(),e.getMessage());//记录错误日志，方便排查问题，改解析代码
    }

    @Override
    protected void onParseFinish(boolean parseSuccess, Url url, String content) {
        if (parseSuccess){
            logger.info("解析成功 url:{}",getUrl().getUrl());
        }
        else {
            if(proxy!=null)
                logger.error("解析失败 url:{},proxy host:{} proxy port:{},parserName{}", url.getUrl(),proxy.getHost(),proxy.getPort(), url.getParserClass());//记录错误日志，方便排查问题，改解析代码
            else
                logger.error("解析失败 url:{},parserName{}", url.getUrl(), url.getParserClass());//记录错误日志，方便排查问题，改解析代码

        }
    }
}
