package com.demo;

import com.jackson.common.control.ProxyController;
import com.jackson.db.po.Proxy;
import com.jackson.db.po.Url;
import com.jackson.db.service.ProxyService;

/**
 * Created by Jackson on 2017/2/16.
 */
public class ProxyDemo {
    public static void main(String[] args){
        //创建代理对象，代理一般通过网站爬取
        Proxy proxy = Proxy.newHttpProxy("192.168.1.1", 5060);
        /**
         * 创建proxyService的时候会根据表名创建一张proxy表，表在数据库里，不会因为停止程序数据消失，下次可以接着用。
         * 刚创建的proxyService里是没有proxy数据的，需要自己去爬取网站获取。Demo里的例子就是爬去proxy数据的，可以直接
         * 使用它。
         */
        //TaskMethod最好用 MIN_TEST_TIME 因为新添加的proxy没有被测试过，没有速度可以过滤
        ProxyService proxyService = new ProxyService("proxy_table_name", ProxyService.TakeMethod.MIN_TEST_TIME);
        //添加代理到表里
        proxyService.insert(proxy);
        //创建代理管理器
        ProxyController proxyController = ProxyController.newInstance(proxyService, Url.newHttpGetUrl("http://www.baidu.com"));
        //设置线程数
        proxyController.setThreadSize(10);
        //开启验证代理可用性
        proxyController.start();

        while(true){
            //验证可用的proxy通过这个方法获取
            Proxy p = proxyController.getProxyPool().take();
        }

    }


    public static ProxyController getProxyController(){
        ProxyService proxyService = new ProxyService("proxy_table_name", ProxyService.TakeMethod.MIN_TEST_TIME);
        ProxyController proxyController = ProxyController.newInstance(proxyService, Url.newHttpGetUrl("http://www.baidu.com"));
        return proxyController;
    }
}
