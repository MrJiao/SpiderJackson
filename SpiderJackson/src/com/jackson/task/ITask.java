package com.jackson.task;

import com.jackson.db.po.Url;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;

/**
 * Created by Jackson on 2016/11/7.
 */
public interface ITask extends Runnable {



    String doRequest(CloseableHttpClient client,HttpRequestBase requestBase) throws IOException;

    HttpRequestBase getHttpRequest();

    CloseableHttpClient getHttpClient();


    /**
     * 网络请求异常出现的回调，用来处理异常的处理。
     *
     * @param e
     * @param url
     * @param httpRequest
     */
    void onRequestException(Exception e, Url url, HttpRequestBase httpRequest);

    /**
     * 请求成功的回调，通常用来回收请求资源。httpClient、httpRequest、url(根据成功与否进行相应操作)
     * @param isRequestOk 代表请求成功
     * @param httpClient
     * @param httpRequest
     * @param url
     */
    void onRequestFinish(boolean isRequestOk, CloseableHttpClient httpClient, HttpRequestBase httpRequest, Url url);




}
