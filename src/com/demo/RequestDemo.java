package com.demo;

import com.jackson.common.control.SimpleRequestControl;
import com.jackson.db.po.Url;
import com.jackson.db.service.UrlService;
import com.jackson.reservoir.HttpGetPool;
import org.apache.http.client.methods.HttpGet;

/**
 * Created by Jackson on 2017/2/16.
 * 请求例子，介绍使用SimpleRequestControl 请求并解析的例子
 */
public class RequestDemo {


    public static void main(String[] args){
        //创建url表
        UrlService url_db_name = new UrlService("url_db_name");
        //创建请求对象
        Url url = Url.newHttpGetUrl("http://www.baidu.com");
        //将请求对象添加到表里
        url_db_name.insert(url);
        //创建请求管理对象
        SimpleRequestControl simpleRequestControl = SimpleRequestControl.newInstance(url_db_name);
        //设置请求线程数
        simpleRequestControl.setThreadSize(10);
        //设置任务间隔时间
        simpleRequestControl.setDelay(new SimpleRequestControl.DelayHandle() {
            @Override
            public long getDelay(Url url) {
                // Random 不固定定时也可以
                return 3000;
            }
        });
        //设置请求参数， 这里的设置相对于url的RequestConfig 会被回调，通常用作全局性的header设置等
        simpleRequestControl.setHttpGetConfigHandler(new HttpGetPool.HttpGetConfigHandler() {
            @Override
            public HttpGet setConfig(HttpGet httpGet, Url url) {
                httpGet.setHeader("user-agent","BaiduSpider");
                return httpGet;
            }
        });
        //开启任务，在开启任务前设置好参数
        simpleRequestControl.start();

    }

}
