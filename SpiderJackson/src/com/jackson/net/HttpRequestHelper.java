package com.jackson.net;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;

/**
 * Created by Jackson on 2016/10/26.
 */
public class HttpRequestHelper {
    private static Logger logger = LogManager.getLogger(HttpRequestHelper.class.getName());
    public static HttpGet getDefaultGet(String url){
        HttpGet httpGet = new HttpGet(url);
        logger.info("创建HttpGet{}",httpGet.toString());
        return httpGet;
    }

    public static HttpPost getPost(String url,String cookie){
        HttpPost httpPost = new HttpPost(url);
        RequestConfig config =  RequestConfig.custom()
                .setCookieSpec(cookie)
                .build();
        httpPost.setConfig(config);
        return httpPost;
    }

    public static HttpGet getGet(String url,String hostName,int port){
        HttpGet httpGet = new HttpGet();
        return HttpRequestHelper.setRequestProxy(httpGet,url,hostName,port);
    }

    public static<T extends HttpRequestBase> T setRequestProxy(T request,String url,String host,int port){
        request.setURI(URI.create(url));
        request.setConfig(RequestConfig.custom()
        .setProxy(new HttpHost(host,port))
        .build()
        );
        return request;
    }


}
