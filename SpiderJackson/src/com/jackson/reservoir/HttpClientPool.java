package com.jackson.reservoir;

import com.jackson.net.HttpClientCreater;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

/**
 * Created by Jackson on 2016/10/27.
 */
@Deprecated
public class HttpClientPool {

    private static Logger logger = LogManager.getLogger(HttpClientPool.class.getName());
    private final LinkedList<CloseableHttpClient> clientPool;
    private CreateHttpClientHandler handler;

    public HttpClientPool(){
        clientPool = new LinkedList<>();
    }

    public synchronized CloseableHttpClient pollHttpClient(){
        /*logger.debug("获取HttpClient");
        if(clientPool.size()==0){
            if(handler!=null){
                return handler.createDefault();
            }else {
                return HttpClientCreater.instance.getDefaultClient();
            }
        }
        return clientPool.poll();*/
        return HttpClientCreater.instance.getHttpClient();
    }





    public synchronized void offerHttpClient(CloseableHttpClient client){
        /*logger.debug("回收httpClient{},当前HttpClientPool里client的数量为:{}",client.toString(),clientPool.size());
        clientPool.offer(client);*/
    }

    public synchronized CloseableHttpClient pollHttpsClient(){
        return HttpClientCreater.instance.getHttpsClient();
    }

    public synchronized void offerHttpsClient(CloseableHttpClient client){
        /*logger.debug("回收httpsClient{},当前HttpClientPool里client的数量为:{}",client.toString(),clientPool.size());
        clientPool.offer(client);*/
    }

    public void setCreateHandler(CreateHttpClientHandler handler){
        this.handler = handler;
    }


    public interface CreateHttpClientHandler {
        CloseableHttpClient createDefault();
    }

}
