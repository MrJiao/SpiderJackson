package com.get_proxy_demo;

import com.jackson.bean.ContextSrc;
import com.jackson.common.control.ProxyController;
import com.jackson.db.po.Proxy;
import com.jackson.db.po.Url;

import com.jackson.db.service.UrlService;
import com.jackson.task.parser.IParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Jackson on 2016/11/7.
 */
public class YouDaiLiPage1 implements IParser {

    private static Logger logger = LogManager.getLogger(YouDaiLiPage1.class.getName());

    @Override
    public boolean responseHandle(Proxy ip, ProxyController proxyController, Url url, UrlService urlService, ContextSrc contextSrc, HttpRequestBase request, CloseableHttpResponse response, String content) {
        ArrayList<Url> urls = new ArrayList<>();
        Document doc = Jsoup.parse(content);
        Elements elements = doc.select("div.chunlist");
        for (Element e : elements) {
            Elements es = e.select("a[href]");
            for (Element el : es) {
                Url url1 = Url.newHttpGetUrl(el.attr("href"), YouDaiLiPage2.class);
                url1.setPriority(url.getPriority() + 1);
                urls.add(url1);
            }
        }
        urlService.insert(urls);
        return true;
    }

}
