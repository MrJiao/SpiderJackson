package com.jackson.common.control;

import com.jackson.common.source.CommonSource;
import com.jackson.db.po.Proxy;
import com.jackson.db.po.Url;
import com.jackson.db.service.IDaoService;
import com.jackson.db.service.ProxyService;
import com.jackson.task.ITask;

import com.jackson.reservoir.ProxyPool;
import com.jackson.task.CreateTaskThread;
import com.jackson.task.proxy.ProxyValidateOkTask;
import com.jackson.task.proxy.ProxyValidateTask;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Jackson on 2016/11/16.
 * 用来控制验证proxy是否可用的管理类
 */
public class ProxyController {
    private final CommonSource source;
    private final CreateProxyTestTaskThread createProxyTestTaskThread;

    private final ScheduledThreadPoolExecutor threadPool;
    private final ProxyPool proxyPool;
    private final ProxyService proxyService;
    private final ProxyService proxyOkService;
    private boolean isOkServiceEnd = false;
    private Url url ;
    private ProxyController(ProxyService proxyService,Url testUrl) {
        this.proxyService = proxyService;
        this.url = testUrl;
        proxyOkService = new ProxyService(proxyService.getTableName() + "_ok", ProxyService.TakeMethod.MAX_SPEED);

        source = CommonSource.newInstance();

        proxyPool = new ProxyPool();

        threadPool = source.getThreadPool();
        createProxyTestTaskThread = new CreateProxyTestTaskThread(proxyOkService, threadPool);

        proxyOkService.setDatabaseToQueueHandler(new ProxyService.DatabaseToQueueHandler() {
            @Override
            public void handle(List<Proxy> list, LinkedBlockingQueue queue) {
                if (list.size() == 0 &&queue.size()==0) {
                    if (!createProxyTestTaskThread.isAlive()) {
                        createProxyTestTaskThread.start();
                    }
                    isOkServiceEnd = true;
                    createProxyTestTaskThread.setService(ProxyController.this.proxyService);
                }
            }
        });
    }

    public static ProxyController newInstance(ProxyService proxyService,Url testUrl){
        return new ProxyController(proxyService,testUrl);
    }

    private ControlConfig controlConfig;


    private void initConfig() {
        if(controlConfig ==null){
            controlConfig = getDefaultConfig();
        }
        proxyService.setProxySize(controlConfig.getGetServiceCatchSize())
                .setMinProxyCatch(controlConfig.getMinServiceCatch());

        proxyOkService.setProxySize(controlConfig.getGetServiceCatchSize())
                .setMinProxyCatch(controlConfig.getMinServiceCatch());

        createProxyTestTaskThread.setMinTaskCache(controlConfig.getMinTaskCache())
                .setMaxTaskCache(controlConfig.getMaxTaskCache());

        threadPool.setCorePoolSize(controlConfig.getCorePoolSize());
    }

    private void setControlConfig(ControlConfig controlConfig){
        this.controlConfig = controlConfig;
    }

    /**
     * 在调用start()方法之前设置，否则无效
     * @param threadSize
     * @return
     */
    public ProxyController setThreadSize(int threadSize){
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

    private ControlConfig getDefaultConfig(){
        return ControlConfig.builder()
                .setCorePoolSize(10)
                .setMaxTaskCache(80)
                .setMinTaskCache(20)
                .setGetServiceCatchSize(100)
                .setMinServiceCatch(20)
                .build();
    }

    /**
     * 添加代理到数据库，非及时插入
     * @param proxy
     */
    public void insert(List<Proxy> proxy) {
        proxyService.insert(proxy);
    }


    public void start(){
        initConfig();
        createProxyTestTaskThread.start();
    }

    public ProxyService getProxyOkService() {
        return proxyOkService;
    }


    /**
     * 用来创建 从没连接成功的ip 的验证任务
     */
    private  class CreateProxyTestTaskThread extends CreateTaskThread<Proxy>{
        public CreateProxyTestTaskThread(IDaoService<Proxy> service, ScheduledThreadPoolExecutor threadPool) {
            super(service, threadPool);
        }

        @Override
        protected Runnable getTask(Proxy obj) {
            return getProxyTestTask(obj);
        }

        @Override
        protected long getDelay(Proxy proxy) {
            return 0;
        }
    }

    private ITask getProxyTestTask(Proxy proxy){
        if(isOkServiceEnd){
            return new ProxyValidateTask(ProxyController.this,url,proxy);
        }else {
            return new ProxyValidateOkTask(ProxyController.this,url,proxy);
        }
    }

    public ProxyService getProxyService() {
        return proxyService;
    }


    /**
     * @return 存放可用proxy的池子
     */
    public ProxyPool getProxyPool() {
        return proxyPool;
    }

    public CommonSource getCommonSource() {
        return source;
    }

}
