package com.demo;

import com.jackson.db.po.Url;
import com.jackson.net.RequestConfig;
import org.apache.http.client.methods.HttpGet;

/**
 * Created by Jackson on 2017/2/16.
 * 这个类是用来设置请求参数的
 */
public class RequestConfigDemo implements RequestConfig<HttpGet> {
    @Override
    public HttpGet setConfig(HttpGet httpRequest, Url url) {
        httpRequest.setHeader("user-agent","BaiduSpider");
        return httpRequest;
    }
}
