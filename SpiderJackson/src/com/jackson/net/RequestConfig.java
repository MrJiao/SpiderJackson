package com.jackson.net;

import com.jackson.db.po.Url;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Created by Jackson on 2017/1/11.
 * 用来设置post参数和配置的回调接口
 */
public interface RequestConfig<T extends HttpRequestBase> {
    T setConfig(T httpRequest,Url url);
}
