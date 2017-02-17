package com.demo;

import com.jackson.bean.ContextSrc;
import com.jackson.common.control.ProxyController;
import com.jackson.db.po.Proxy;
import com.jackson.db.po.Url;
import com.jackson.db.service.UrlService;
import com.jackson.task.parser.IParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Created by Jackson on 2017/2/16.
 * 解析的Demo， responseHandle里返回的参数有的可能为null，参数是否为null 要根据Url设置来
 * 解析完成后 返回true代表正常解析  返回false表示解析失败，返回false 会将url重新加入到urlService中
 */
public class ParserDemo implements IParser {
    @Override
    public boolean responseHandle(Proxy proxy, ProxyController proxyController, Url url, UrlService urlService, ContextSrc contextSrc, HttpRequestBase request, CloseableHttpResponse response, String content) {
        System.out.print(content);

        return true;
    }
}
