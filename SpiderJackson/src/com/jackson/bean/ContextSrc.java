package com.jackson.bean;

import com.jackson.db.po.Account;
import com.jackson.db.po.Ip;
import com.jackson.db.po.Proxy;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.HttpContext;


/**
 * Created by Jackson on 2017/1/6.
 * 请求相关对象上下文
 */
public class ContextSrc {
    private Account account;
    private HttpClientContext httpContext ;
    private String userAgent;
    private Proxy proxy;

    public ContextSrc(){
        httpContext = HttpClientContext.create();
        httpContext.setCookieStore(new BasicCookieStore());
    }

    public Account getAccount() {
        return account;
    }

    public ContextSrc setAccount(Account account) {
        this.account = account;
        return this;
    }

    public HttpClientContext getHttpContext() {
        return httpContext;
    }

    public ContextSrc setHttpContext(HttpClientContext httpContext) {
        this.httpContext = httpContext;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public ContextSrc setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public ContextSrc setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }
}
