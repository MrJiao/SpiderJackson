package com.jackson.task;

import com.jackson.bean.ContextSrc;
import com.jackson.common.control.ProxyController;
import com.jackson.db.po.Proxy;
import com.jackson.db.po.Url;
import com.jackson.db.service.UrlService;
import com.jackson.net.HttpClientCreater;
import com.jackson.reservoir.ParserPool;
import com.jackson.task.parser.IParser;
import com.jackson.utils.StringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by Jackson on 2016/11/7.
 * 请求并解析的task
 */
public abstract class RequestParserTask implements ITask {

    private static Logger logger = LogManager.getLogger(RequestParserTask.class.getName());
    private static ParserPool parserPool= new ParserPool();

    public RequestParserTask(){
    }

    /**
     * 确保每次获取的url是同一个,不允许为空
     * @return
     */
    protected abstract Url getUrl();
    /**
     * 确保每次获取的proxy是同一个
     * @return 允许返回null
     */
    protected abstract Proxy getProxy();


    protected abstract ProxyController getProxyController();
    /**
     *
     * @return 允许返回null，IPaser里的urlService通过这里返回
     */
    protected abstract UrlService getUrlService();

    /**
     * 请求前的回调
     */
    protected void onInit(Url url,Proxy proxy) {
    }


    /**
     * @param statusCode 状态码
     * @param response   @return true:状态正确  false：状态错误，返回false 则代表请求失败，onRequestFinisn()里第一个参数为false
     *                   <p>
     *                   并且不会回调
     *                   getParser()、onParseSuccess(Url url, String content)、onParseException(Exception e, Url url, String content)
     */
    protected boolean onStatusCode(int statusCode, CloseableHttpResponse response) {
        return true;
    }


    /**
     * 获取解析器
     *
     * @return
     */
    public IParser getParser() {
        return parserPool.getParser(getUrl().getParserClass());
    }

    /**
     *
     * @return 允许返回null,表示不使用cookie
     */
    protected abstract ContextSrc getContextSrc();

    /**
     * 解析异常出现的回调。通常设置url为完成状态，记录错误日志。出现这样的情况是代码问题，这样做可以避免继续解析错误，并方便排查问题。
     *
     * @param e
     * @param url
     * @param content
     */
    protected abstract void onParseException(Exception e, Url url, String content);


    /**
     * 请求并解析成功后的回调， 通常用来回收url并设置成完成状态
     *
     * @param parseSuccess
     * @param url
     * @param content
     */
    protected abstract void onParseFinish(boolean parseSuccess, Url url, String content);


    @Override
    public CloseableHttpClient getHttpClient() {
        if(getUrl().getProtocolState()==Url.PROTOCOL_STATE_HTTP){
            return HttpClientCreater.instance.getHttpClient();
        }
        if(getUrl().getProtocolState()==Url.PROTOCOL_STATE_HTTPS){
            return HttpClientCreater.instance.getHttpsClient();
        }
        throw new RuntimeException("RequestParserTask 协议类型错误");
    }

    CloseableHttpResponse response = null;

    @Override
    public String doRequest(CloseableHttpClient client, HttpRequestBase requestBase) throws ClientProtocolException, SocketTimeoutException, ConnectTimeoutException, IOException {
        if(getContextSrc()!=null){
            if(!StringUtil.isEmpty(getContextSrc().getUserAgent()))
                requestBase.setHeader("User-Agent",getContextSrc().getUserAgent());

            if(getContextSrc().getProxy()!=null){

                Proxy proxy = getContextSrc().getProxy();
                if(proxy.getProtocolState()!=getUrl().getProtocolState()){
                    throw new RuntimeException("RequestParserTask proxy协议类型和 url需要的协议类型不匹配");
                }
                HttpHost httpHost = new HttpHost(proxy.getHost(), proxy.getPort(),getUrl().getProtocolScheme());
                logger.info("请求 url:{} proxy:{}:{} userAgent:{}",getUrl().getUrl(),proxy.getHost(),proxy.getPort(),getContextSrc().getUserAgent());
                response = client.execute(httpHost,requestBase,getContextSrc().getHttpContext());
            }else {
                logger.info("请求 url:{} userAgent:{}",getUrl().getUrl(),getContextSrc().getUserAgent());
                response = client.execute(requestBase,getContextSrc().getHttpContext());
            }
        }else {
            if(getProxy()!=null){
                Proxy proxy = getProxy();
                if(proxy.getProtocolState()!=getUrl().getProtocolState()){
                    throw new RuntimeException("RequestParserTask proxy协议类型和 url需要的协议类型不匹配");
                }
                HttpHost httpHost = new HttpHost(getProxy().getHost(), getProxy().getPort(),getUrl().getProtocolScheme());
                logger.info("请求 url:{} proxy:{}:{}",getUrl().getUrl(),proxy.getHost(),proxy.getPort());
                response = client.execute(httpHost,requestBase);
            }else {
                logger.info("请求 url:{}",getUrl().getUrl());
                response = client.execute(requestBase);
            }
        }
        if(getUrl().getParserClass()==null)return "没有解析器";//如果没有解析器就返回""
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity, "utf-8");
    }

    @Override
    public void run() {
        //发起请求
        onInit(getUrl(),getProxy());
        String content = null;
        boolean isRequestOk = false;

        HttpRequestBase httpRequest = null;
        CloseableHttpClient httpClient = null;

        try {
            httpRequest = getHttpRequest();
            httpClient = getHttpClient();
            content = doRequest(httpClient, httpRequest);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            isRequestOk = onStatusCode(statusCode, response);
        } catch (Exception e) {
            isRequestOk = false;
            onRequestException(e, getUrl(), httpRequest);
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            onRequestFinish(isRequestOk, httpClient, httpRequest, getUrl());
        }
        //下面开始解析
        if (!isRequestOk) return;
        boolean parseSuccess = false;
        try {
            IParser parser = getParser();
            if (parser != null)
                parseSuccess = parser.responseHandle(getProxy(),getProxyController(),getUrl(),getUrlService(),getContextSrc(), httpRequest, response, content);
        } catch (RuntimeException e) {
            onParseException(e, getUrl(), content);
            parseSuccess = false;
        } catch (Exception e) {
            onParseException(e, getUrl(), content);
            parseSuccess = false;
        } finally {
            onParseFinish(parseSuccess, getUrl(), content);
        }

    }

}
