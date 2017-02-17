package com.jackson.task;

import com.jackson.db.po.Proxy;
import com.jackson.db.po.Url;
import com.jackson.net.HttpClientCreater;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by Jackson on 2016/11/17.
 * 只做请求的task
 */
public abstract class RequestTask implements ITask{
    private static Logger logger = LogManager.getLogger(RequestTask.class.getName());


    protected abstract Url getUrl();

    private long startTime;
    private long endTime;



    protected abstract Proxy getProxy();


    /**
     * @param statusCode 状态码
     * @param response   @return true:状态正确  false：状态错误，返回false 则代表请求失败，onRequestFinisn()里第一个参数为false
     *                   <p>
     *                   并且不会回调
     *                   getParser()、onParseSuccess(Url url, String content)、onParseException(Exception e, Url url, String content)
     */
    protected abstract boolean onStatusCode(int statusCode, CloseableHttpResponse response);

    protected abstract void onInit();

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


    @Override
    public String doRequest(CloseableHttpClient client, HttpRequestBase requestBase) throws IOException {
        logger.info("请求 url:{}",getUrl().getUrl());
        startTime = System.currentTimeMillis();
        if(getProxy()!=null){
            response = client.execute(new HttpHost(getProxy().getHost(),getProxy().getPort()),requestBase);
        }else {
            response = client.execute(requestBase);
        }
        response = client.execute(requestBase);
        endTime = System.currentTimeMillis();
        logger.info("endTime:",endTime);
        //HttpEntity entity = response.getEntity();
        return "";//EntityUtils.toString(entity, "utf-8");
    }

    CloseableHttpResponse response = null;
    @Override
    public void run() {
        boolean isRequestOk = true;
        onInit();
        final HttpRequestBase httpRequest = getHttpRequest();
        final CloseableHttpClient httpClient = getHttpClient();
        try{
            doRequest(httpClient, httpRequest);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            isRequestOk = onStatusCode(statusCode, response);
        } catch (SocketTimeoutException e) {
            isRequestOk = false;
            onRequestException(e,getUrl(), httpRequest);
        } catch (ClientProtocolException e) {
            isRequestOk = false;
            onRequestException(e,getUrl(), httpRequest);
        } catch (ConnectTimeoutException e) {
            isRequestOk = false;
            onRequestException(e,getUrl(), httpRequest);
        } catch (IOException e) {
            isRequestOk = false;
            onRequestException(e,getUrl(), httpRequest);
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            onRequestFinish(isRequestOk,httpClient,httpRequest,getUrl());
        }
    }



    protected long getSpeed(){
        logger.info("ip:{}:{} 速度为 {}",getProxy().getHost(),getProxy().getPort(),endTime-startTime);
        return endTime-startTime;
    }


}
