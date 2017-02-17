package com.jackson.common.control;

import com.jackson.common.source.CommonSource;
import com.jackson.common.task.SimpleRequestTask;
import com.jackson.db.po.Url;
import com.jackson.db.service.IDaoService;
import com.jackson.db.service.UrlService;
import com.jackson.reservoir.HttpGetPool;
import com.jackson.reservoir.HttpPostPool;
import com.jackson.reservoir.ProxyPool;
import com.jackson.task.CreateTaskThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Jackson on 2016/12/21.
 */
public class SimpleRequestControl {

    private final ProxyController proxyController;
    private ProxyPool proxyPool;
    private DelayHandle delayHandle;

    public static SimpleRequestControl newInstance(UrlService urlService){
        return new SimpleRequestControl(urlService,null);
    }

    public static SimpleRequestControl newInstance(UrlService urlService,ProxyController proxyController){
        return new SimpleRequestControl(urlService,proxyController);
    }

    private static Logger logger = LogManager.getLogger(SimpleRequestControl.class.getName());

    private final CreateTaskThread createTaskThread;
    private final CommonSource source;
    private final UrlService urlService;

    private ControlConfig controlConfig;

    private SimpleRequestControl(UrlService urlService,ProxyController proxyController) {
        this.urlService = urlService;
        this.proxyController = proxyController;
        source = CommonSource.newInstance();
        createTaskThread = new TaskThread(urlService,source.getThreadPool());
    }

    public void start(){
        initConfig();
        if(proxyController !=null){
            proxyPool = proxyController.getProxyPool();
            proxyController.start();
        }
        createTaskThread.start();
    }

    public SimpleRequestControl setDelay(DelayHandle delayHandle){
        this.delayHandle = delayHandle;
        return this;
    }

    private void initConfig() {
        if(controlConfig ==null){
            controlConfig = getDefaultConfig();
        }
        source.getThreadPool().setCorePoolSize(controlConfig.getCorePoolSize());
        createTaskThread.setMaxTaskCache(controlConfig.getMaxTaskCache())
                .setMinTaskCache(controlConfig.getMinTaskCache());
        urlService.setGetUrlSize(controlConfig.getGetServiceCatchSize());
        urlService.setMinUrlCatch(controlConfig.getMinServiceCatch());
    }

    private class TaskThread extends CreateTaskThread<Url>{

        public TaskThread(IDaoService<Url> service, ScheduledThreadPoolExecutor threadPool) {
            super(service, threadPool);
        }

        @Override
        protected Runnable getTask(Url url) {
            if(proxyController!=null){
                return new SimpleRequestTask(proxyPool.take(),proxyController,url,urlService,source);
            }else {
                return new SimpleRequestTask(url,urlService,source);
            }
        }

        @Override
        protected long getDelay(Url url) {
            if(delayHandle!=null){
                return delayHandle.getDelay(url);
            }else {
                return 0;
            }
        }

    }


    public void setControlConfig(ControlConfig controlConfig){
        this.controlConfig = controlConfig;
    }

    /**
     * 设置线程数，其他变量根据线程数自动配置,在start()方法前调用
     * @param threadSize
     */
    public SimpleRequestControl setThreadSize(int threadSize){
        ControlConfig controlConfig = ControlConfig.builder()
                .setCorePoolSize(threadSize)
                .setMaxTaskCache(3*threadSize)
                .setMinTaskCache(threadSize+1)
                .setGetServiceCatchSize(6*threadSize)
                .setMinServiceCatch(2*threadSize+1)
                .build();
        setControlConfig(controlConfig);
        return this;
    }


    public ControlConfig getDefaultConfig(){
        return ControlConfig.builder()
                .setCorePoolSize(10)
                .setMaxTaskCache(30)
                .setMinTaskCache(11)
                .setGetServiceCatchSize(60)
                .setMinServiceCatch(21)
                .build();
    }

    public void setHttpGetConfigHandler(HttpGetPool.HttpGetConfigHandler handler){
        source.getHttpGetPool().setConfigHandler(handler);
    }

    public void setHttpPostConfigHandler(HttpPostPool.HttpPostConfigHandler handler){
        source.getHttpPostPool().setConfigHandler(handler);
    }

    public interface DelayHandle{
        long getDelay(Url url);
    }

}
