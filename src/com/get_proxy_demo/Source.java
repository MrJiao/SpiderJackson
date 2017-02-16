package com.get_proxy_demo;

import com.jackson.common.control.ProxyController;
import com.jackson.db.po.Url;
import com.jackson.db.service.ProxyService;

/**
 * Created by Jackson on 2017/1/16.
 */
public class Source {

    private static ProxyController proxyController;

    public static ProxyController getProxyController(){
        if(proxyController==null){
            synchronized (ProxyController.class){
                if (proxyController==null){
                    proxyController = ProxyController.newInstance(new ProxyService("total_ips", ProxyService.TakeMethod.MIN_TEST_TIME), Url.newHttpGetUrl("http://www.youdaili.net/"));
                }
            }
        }

        return proxyController;
    }
}
