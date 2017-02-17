package com.jackson.common.control;

import com.jackson.common.source.CommonSource;
import com.jackson.db.po.Url;
import com.jackson.db.service.IDaoService;
import com.jackson.db.service.UrlService;
import com.jackson.reservoir.HttpGetPool;
import com.jackson.reservoir.ProxyPool;
import com.jackson.task.CreateTaskThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Jackson on 2016/12/21.
 */
@Deprecated
public class SimpleGetControl {

    /*private boolean useProxy;
    private ProxyPool proxyPool;

    public static SimpleGetControl newInstance(UrlService urlService){
        return new SimpleGetControl(urlService);
    }
    private static Logger logger = LogManager.getLogger(SimpleGetControl.class.getName());

    private final CreateTaskThread createTaskThread;
    private final CommonSource source;
    private final UrlService urlService;

    private ControlConfig controlConfig;

    private SimpleGetControl(UrlService urlService) {
        this.urlService = urlService;
        source = CommonSource.newInstance();
        createTaskThread = new TaskThread(urlService,source.getThreadPool());
    }

    public void start(){
        initConfig();
        if(useProxy){
            proxyPool = ProxyController.getInstance().getProxyPool();
            ProxyController.getInstance().setThreadSize(10).ipOkThreadStart();
        }
        createTaskThread.start();
    }

    private void initConfig() {
        if(controlConfig ==null){
            controlConfig = getDefaultConfig();
        }
        source.getThreadPool().setCorePoolSize(controlConfig.getCorePoolSize());
        createTaskThread.setMaxTaskCache(controlConfig.getMaxTaskCache())
                .setMinTaskCache(controlConfig.getMinTaskCache());
        urlService.setMinUrlCatch(controlConfig.getMinServiceCatch());
        urlService.setGetUrlSize(controlConfig.getGetServiceCatchSize());
    }

    private class TaskThread extends CreateTaskThread<Url>{

        public TaskThread(IDaoService<Url> service, ScheduledThreadPoolExecutor threadPool) {
            super(service, threadPool);
        }

        @Override
        protected Runnable getTask(Url url) {
            if(useProxy){
                return new SimpleGetTask(proxyPool.take(),url,urlService,source);
            }else {
                return new SimpleGetTask(url,urlService,source);
            }

        }

        @Override
        protected long getDelay(Url url) {
            return 0;
        }

    }


    public void setControlConfig(ControlConfig controlConfig){
        this.controlConfig = controlConfig;
    }

    *//**
     * 设置线程数，其他变量根据线程数自动配置,在start()方法前调用
     * @param threadSize
     *//*
    public SimpleGetControl setThreadSize(int threadSize){
        ControlConfig controlConfig = ControlConfig.builder()
                .setCorePoolSize(threadSize)
                .setMaxTaskCache(2*threadSize)
                .setMinTaskCache(threadSize+1)
                .setGetServiceCatchSize(5*threadSize)
                .setMinServiceCatch(threadSize+1)
                .build();
        setControlConfig(controlConfig);
        return this;
    }


    public ControlConfig getDefaultConfig(){
        return ControlConfig.builder()
                .setCorePoolSize(10)
                .setMaxTaskCache(80)
                .setMinTaskCache(20)
                .setGetServiceCatchSize(100)
                .setMinServiceCatch(20)
                .build();
    }


    public void setHttpGetConfigHandler(HttpGetPool.HttpGetConfigHandler handler){
        source.getHttpGetPool().setHttpGetConfigHandler(handler);
    }

    public SimpleGetControl setUseProxy(boolean isUse){
        this.useProxy = isUse;
        return this;
    }*/

}
