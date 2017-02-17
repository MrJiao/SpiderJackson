package com.get_proxy_demo;

import com.jackson.bean.ContextSrc;
import com.jackson.common.control.ProxyController;
import com.jackson.db.po.Proxy;
import com.jackson.db.po.Url;
import com.jackson.db.service.UrlService;
import com.jackson.task.parser.IParser;
import com.jackson.utils.ProxyUtil;
import com.jackson.utils.RegexUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Jackson on 2016/11/7.
 */
public class YouDaiLiPage2 implements IParser {
    private static Logger logger = LogManager.getLogger(YouDaiLiPage2.class.getName());

    @Override
    public boolean responseHandle(Proxy proxy, ProxyController proxyController, Url url, UrlService urlService, ContextSrc contextSrc, HttpRequestBase request, CloseableHttpResponse response, String content) {
        List<String> matechIp = RegexUtil.matechIp(content);
        Source.getProxyController().insert(ProxyUtil.getHttpProxy(matechIp));
        if(!url.getUrl().contains("_"))
            startChildUrls(url,content,urlService);
        logger.info("matechIp size{} url:{}",matechIp.size(),url.getUrl());

        if(matechIp.size()==0){
            logger.info("parserFailureTime:{} url:{}",url.getParserFailureTime(),url.getUrl());
            if(url.getParserFailureTime()<10){
                url.setParserFailureTime(url.getParserFailureTime()+1);
                urlService.add(url);
            }
        }
        if(matechIp.size()>0){
            if(proxy!=null)
                logger.info("成功url:{} proxy{}:{} 时间:{}",url.getUrl(),proxy.getHost(),proxy.getPort(),System.currentTimeMillis());
            else
                logger.info("成功url:{}",url.getUrl());
        }
        return matechIp.size()>0;
    }

    private void startChildUrls(Url url, String html, UrlService urlService){
        String rgx = "共\\d+页";
        List<String> arr = RegexUtil.match(Pattern.compile(rgx), html);
        ArrayList<Url> urls = new ArrayList<>();
        int page=0;
        if(arr.size()!=0) {
            String pageStr = arr.get(0);
            pageStr = pageStr.substring(1, pageStr.length()-1);
            page = Integer.parseInt(pageStr);
        }
        for(int i=page;i>1;i--){
            String newRequestDate = url.getUrl().replace(".html", "_"+i+".html");
            Url url1 = Url.newHttpGetUrl(newRequestDate, YouDaiLiPage2.class);
            url1.setPriority(url.getPriority());
            urls.add(url1);
        }
        urlService.insert(urls);
    }
}
