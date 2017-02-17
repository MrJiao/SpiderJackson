package com.jackson.task.parser;

import com.jackson.bean.ContextSrc;
import com.jackson.common.control.ProxyController;
import com.jackson.db.po.Proxy;
import com.jackson.db.po.Url;
import com.jackson.db.service.UrlService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Created by Jackson on 2016/11/7.
 * IParser创建的对象是单例存在并多线程使用的，请注意线程安全问题
 * 作用：请求完成后的处理，包括 数据解析、存储、解析失败时新任务重创建等
 */
public interface IParser {
    /**
     *
     * @param proxy
     * @param proxyController
     * @param url
     * @param urlService
     * @param contextSrc
     * @param request 请求参数
     * @param response
     * @param content 请求完成的内容html
     * @return true:解析成功 false:解析失败
     */
    boolean responseHandle(Proxy proxy, ProxyController proxyController, Url url, UrlService urlService, ContextSrc contextSrc, HttpRequestBase request, CloseableHttpResponse response, String content);
}
