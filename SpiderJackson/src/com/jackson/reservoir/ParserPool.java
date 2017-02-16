package com.jackson.reservoir;

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

import java.util.HashMap;

/**
 * Created by Jackson on 2016/11/7.
 * 解析器都是单例存储的。
 */
public class ParserPool {
    private static Logger logger = LogManager.getLogger(ParserPool.class.getName());
    private String defaultParser = "defaultParser";
    private HashMap<String,IParser> pool;
    public ParserPool(){
        pool = new HashMap<>();
        pool.put("defaultParser",new DefaultParser());
    }

    public IParser getParser(Class<? extends IParser> clzz){
        if(clzz==null)return pool.get(defaultParser);

        IParser iParser = pool.get(clzz.getName());
        if(iParser==null){
            iParser = newInstance(clzz);
            if(iParser==null){
                logger.error("解析parser错误 parserClassName{}",clzz.getName());
                throw new RuntimeException("解析parser错误 parserClassName:"+clzz.getName());
            }
            else
                pool.put(clzz.getName(),iParser);
        }
        logger.debug("获取parser className:{}",clzz.getName());
        return iParser;
    }

    private IParser newInstance(Class<? extends IParser> clzz){
        logger.debug("创建新的parser className:{},当前parser数量为{}",clzz.getName(),pool.size());
        IParser iParser = null;
        try{
            iParser = clzz.newInstance();
        } catch (IllegalAccessException e) {
            logger.error(e.toString());
        } catch (InstantiationException e) {
            logger.error(e.toString());
        }finally {
            return iParser;
        }
    }

    /**
     * 解析器classname为空时返回它
     */
    private static class DefaultParser implements IParser{


        @Override
        public boolean responseHandle(Proxy proxy, ProxyController proxyController, Url url, UrlService urlService, ContextSrc contextSrc, HttpRequestBase request, CloseableHttpResponse response, String content) {
            return true;
        }
    }
}
