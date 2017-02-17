package com.demo;

import com.jackson.db.po.Url;

/**
 * Created by Jackson on 2017/2/16.
 * 获取Url对象的例子
 * Url里配置请求的url链接，请求参数（RequestConfig），请求完成后的解析（IParser)
 * 请求参数和解析器都可以不设置
 */
public class SetUrlDemo {

    public static void main(String[] args){
        Url url1 = Url.newHttpGetUrl("http://www.baidu.com", RequestConfigDemo.class, ParserDemo.class);
        Url url2 = Url.newHttpPostUrl("http://www.baidu.com", RequestConfigDemo.class, ParserDemo.class);
        Url url3 = Url.newHttpsGetUrl("http://www.baidu.com", RequestConfigDemo.class, ParserDemo.class);
        Url url4 = Url.newHttpsPostUrl("http://www.baidu.com", RequestConfigDemo.class, ParserDemo.class);
        Url url5 = Url.newHttpGetUrl("http://www.baidu.com");
    }
}
