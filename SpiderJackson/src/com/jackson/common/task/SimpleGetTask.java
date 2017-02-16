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
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Jackson on 2016/11/7.
 * 不要使用，会有问题的
 */
@Deprecated
public class SimpleGetTask extends RequestParserTask {

    private static Logger logger = LogManager.getLogger(SimpleGetTask.class.getName());
    private final UrlService urlService;
    private final Url url;
    private Proxy proxy;
    private final CommonSource source;
    public SimpleGetTask(Proxy proxy, Url url, UrlService urlService,CommonSource source) {
        this.url = url;
        this.proxy = proxy;
        this.source = source;
        this.urlService = urlService;
    }

    public SimpleGetTask(Url url, UrlService urlService,CommonSource source) {
        this(null,url,urlService,source);
    }

    @Override
    public HttpRequestBase getHttpRequest() {
        return source.pollHttpGet(getUrl());
    }


    @Override
    public void onRequestException(Exception e, Url url, HttpRequestBase httpRequest) {
        logger.error("请求异常 url:{} Exception:{}", url.getUrl(),e.toString());
    }

    @Override
    public void onRequestFinish(boolean isRequestOk, CloseableHttpClient httpClient, HttpRequestBase httpRequest, Url url) {
        if (!isRequestOk) {
            urlService.add(getUrl());//如果失败，就重新放入数据库，等着下次接着用,如果成功等着解析，解析成功后回收
        }
        source.offerHttpGet((HttpGet) httpRequest);
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
        return null;
    }


    @Override
    protected void onParseException(Exception e, Url url, String content) {
        logger.error("解析异常 url:{},parser:{},Exception{},content:{}", url.getUrl(),url.getParserClass(),e.toString(), content);//记录错误日志，方便排查问题，改解析代码
    }

    @Override
    protected void onParseFinish(boolean parseSuccess, Url url, String content) {

        //解析失败一次,proxy 就放弃使用
        //解析失败超过4次 ,url 放弃使用

        if (parseSuccess){
            urlService.completeUrl(getUrl());//设置成完成状态
            if(proxy!=null){
              //  ProxyController.getInstance().getProxyPool().offer(proxy);

            }
        }
        else {
            if(url.getParserFailureTime()<4){
                url.setParserFailureTime(url.getParserFailureTime()+1);
                urlService.add(url);//存入内存,下次接着爬取
                logger.error("解析错误　错误次数为：{} proxy:{}:{},url:{},parser:{}",url.getParserFailureTime(),proxy.getHost(),proxy.getPort(),url.getUrl(),url.getParserClass());//记录错误日志，方便排查问题，改解析代码
            }else {
                if(proxy!=null)
                    logger.error("解析失败 url:{},proxy host:{} proxy port:{},parserName{},content:{}", url.getUrl(),proxy.getHost(),proxy.getPort(), url.getParserClass(), content);//记录错误日志，方便排查问题，改解析代码
                else
                    logger.error("解析失败 url:{},parserName{},content:{}", url.getUrl(), url.getParserClass(), content);//记录错误日志，方便排查问题，改解析代码
            }
        }
    }
}
