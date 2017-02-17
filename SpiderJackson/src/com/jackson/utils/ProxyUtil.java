package com.jackson.utils;

import com.jackson.db.po.Proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jackson on 2016/11/7.
 */
public class ProxyUtil {


    /**
     *
      * @param ipStr 格式是234.3423.43244.432:9090
     */
    public static Proxy getHttpProxy(String ipStr){
        String[] ipS = ipStr.split(":");
        Proxy proxy = Proxy.newHttpProxy(ipS[0], Integer.valueOf(ipS[1]));
        return proxy;
    }


    /**
     *
     * @param ips 格式是234.3423.43244.432:9090
     */
    public static List<Proxy> getHttpProxy(List<String> ips){
        ArrayList<Proxy> arr = ips.stream().map(ProxyUtil::getHttpProxy).collect(Collectors.toCollection(ArrayList::new));
        return arr;
    }
}
