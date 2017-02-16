package com.demo;

import com.jackson.common.control.TimerRequestControl;
import com.jackson.common.control.UserAgentControl;
import com.jackson.db.po.Url;
import com.jackson.reservoir.HttpGetPool;
import com.jackson.reservoir.HttpPostPool;
import com.jackson.reservoir.TimedUrlPool;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

/**
 * Created by Jackson on 2017/2/16.
 * 定时请求例子
 */
public class TimerRequestDemo {

    public static void main(String[] args){
        //创建定时任务
        TimedUrlPool.TimedUrl task1= new TimedUrlPool.TimedUrl() {
            @Override
            public Url getUrl() {
                return  Url.newHttpGetUrl("http://www.youdaili.net",ParserDemo.class);
            }

            @Override
            public long getDelay() {
                return 3000;
            }
        };
        //创建定时任务
        TimedUrlPool.TimedUrl task2= new TimedUrlPool.TimedUrl() {
            @Override
            public Url getUrl() {
                return Url.newHttpGetUrl("http://www.17k.com/",ParserDemo.class);
            }

            @Override
            public long getDelay() {
                return 3000;
            }
        };

        TimerRequestControl timerRequestControl = TimerRequestControl.newInstance();
        //添加定时任务
        timerRequestControl.addTimedUrl(task1);
        timerRequestControl.addTimedUrl(task2);
        timerRequestControl.setHttpGetConfigHandler(new HttpGetPool.HttpGetConfigHandler() {
            @Override
            public HttpGet setConfig(HttpGet httpGet, Url url) {
                httpGet.setHeader("User-Agent", UserAgentControl.getInstance().next(UserAgentControl.UserAgentType.PC));
                return httpGet;
            }
        });

        timerRequestControl.setHttpPostConfigHandler(new HttpPostPool.HttpPostConfigHandler() {
            @Override
            public HttpPost setConfig(HttpPost httpPost, Url url) {
                httpPost.setHeader("User-Agent", UserAgentControl.getInstance().next(UserAgentControl.UserAgentType.PC));
                return httpPost;
            }
        });
        //设置请求线程数
        timerRequestControl.setThreadSize(10);
        timerRequestControl.start();

    }


    /**
     * 如果定时请求任务要通过代理，可以通过构造方法传入proxyController来构造TimerRequestControl
     */
    public void useProxy(){
        TimerRequestControl.newInstance(ProxyDemo.getProxyController());
    }
}
