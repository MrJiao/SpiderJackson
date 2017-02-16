package com.jackson.reservoir;

import com.jackson.net.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * Created by Jackson on 2016/11/7.
 * Post参数设置对象都是单例存储的。
 */
public class RequestConfigPool<T extends HttpRequestBase> {
    private static Logger logger = LogManager.getLogger(RequestConfigPool.class.getName());

    private HashMap<String,RequestConfig<T>> pool;
    public RequestConfigPool(){
        pool = new HashMap<>();
    }

    public RequestConfig<T> getRequestConfig(Class<? extends RequestConfig> clzz){
        RequestConfig requestConfig = pool.get(clzz.getName());
        if(requestConfig ==null){
            requestConfig = newInstance(clzz);
            if(requestConfig ==null){
                logger.error("解析PostConfig错误 PostConfigClassName{}",clzz.getName());
                throw new RuntimeException("解析PostConfig错误 PostConfigClassName:"+clzz.getName());
            }
            else
                pool.put(clzz.getName(), requestConfig);
        }
        logger.debug("获取PostConfig className:{}",clzz.getName());
        return requestConfig;
    }

    private RequestConfig newInstance(Class<? extends RequestConfig> clzz){
        logger.debug("创建新的PostConfig className:{},当前PostConfig数量为{}",clzz.getName(),pool.size());
        RequestConfig requestConfig = null;
        try{
            requestConfig = clzz.newInstance();
        } catch (IllegalAccessException e) {
            logger.error(e.toString());
        } catch (InstantiationException e) {
            logger.error(e.toString());
        } finally {
            return requestConfig;
        }
    }
}
