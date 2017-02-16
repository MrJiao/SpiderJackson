package com.jackson.common.control;

import com.jackson.common.source.CommonSource;
import com.jackson.db.service.IDaoService;
import com.jackson.reservoir.HttpGetPool;
import com.jackson.reservoir.HttpPostPool;
import com.jackson.reservoir.ProxyPool;
import com.jackson.reservoir.TimedUrlPool;
import com.jackson.reservoir.TimedUrlPool.TimedUrl;
import com.jackson.task.CreateTaskThread;
import com.jackson.task.timer.TimedTask;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Jackson on 2016/12/27.
 * 间隔重复请求管理类
 */
public class TimerRequestControl {

    private final TimedUrlPool timedUrlPool;
    private final ScheduledThreadPoolExecutor threadPool;
    private final TaskThread taskThread;
    private final CommonSource source;
    private final ProxyController proxyController;
    private ProxyPool proxyPool;
    private int threadSize=2;

    public static TimerRequestControl newInstance(){
        return new TimerRequestControl(null);
    }

    public static TimerRequestControl newInstance(ProxyController proxyController){
        return new TimerRequestControl(proxyController);
    }

    private TimerRequestControl(ProxyController proxyController){
        this.proxyController = proxyController;
        timedUrlPool = new TimedUrlPool();
        source = CommonSource.newInstance();
        threadPool = source.getThreadPool();
        taskThread = new TaskThread(timedUrlPool, threadPool);
    }


    public void start(){
        if(proxyController!=null){
            proxyPool = proxyController.getProxyPool();
            proxyController.start();
        }
        taskThread.setMinTaskCache(timedUrlPool.size()-1);
        taskThread.setMaxTaskCache(timedUrlPool.size()+1);
        taskThread.start();
    }

    public TimerRequestControl setThreadSize(int threadSize){
        this.threadSize = threadSize;
        return this;
    }




    public void addTimedUrl(TimedUrl timedUrl) {
        timedUrlPool.offer(timedUrl);
    }

    private class TaskThread extends CreateTaskThread<TimedUrl> {

        public TaskThread(IDaoService<TimedUrl> service, ScheduledThreadPoolExecutor threadPool) {
            super(service, threadPool);
        }

        @Override
        protected Runnable getTask(TimedUrl timedUrl) {
            if(proxyController != null){
                return new TimedTask(timedUrl, timedUrlPool,proxyController,proxyPool.take(),source);
            }else {
                return new TimedTask(timedUrl, timedUrlPool,source);
            }
        }

        @Override
        protected long getDelay(TimedUrl timedUrl) {
            return timedUrl.getDelay();
        }

    }

    public void setHttpGetConfigHandler(HttpGetPool.HttpGetConfigHandler handler){
        source.getHttpGetPool().setConfigHandler(handler);
    }

    public void setHttpPostConfigHandler(HttpPostPool.HttpPostConfigHandler handler){
        source.getHttpPostPool().setConfigHandler(handler);
    }

}
