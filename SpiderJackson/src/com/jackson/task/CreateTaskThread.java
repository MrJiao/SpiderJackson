package com.jackson.task;

import com.jackson.db.service.IDaoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jackson on 2016/11/16.
 * 功能：从service里取出bean，变成task，交给threadPool 执行，内部维护threadPool的缓存大小，
 * 当到达设定缓存大小时，周期性查看缓存大小如果符合条件则添加任务
 */
public abstract class CreateTaskThread<E> extends Thread{
    private IDaoService<E> service;
    private ScheduledThreadPoolExecutor threadPool;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());
    /**
     * 最大任务缓存数量
     */
    private int max_task_cache = 40;

    /**
     * 最小任务缓存数量，当小于这个数时，就开始创建任务
     */
    private int min_task_cache = 15;


    /**
     * 当没有满足缓存条件时的刷新周期。单位：毫秒
     */
    private long flush_cycle = 1000;


    public CreateTaskThread(IDaoService<E> service, ScheduledThreadPoolExecutor threadPool) {
        this.service = service;
        this.threadPool = threadPool;
    }

    public CreateTaskThread setMaxTaskCache(int cache){
        max_task_cache = cache;
        return this;
    }

    public void setService(IDaoService<E> service) {
        this.service = service;
    }

    public CreateTaskThread setMinTaskCache(int cache){
        min_task_cache = cache;
        return this;
    }

    public CreateTaskThread setFlushCycle(long cycleMillisecond){
        flush_cycle = cycleMillisecond;
        return this;
    }

    @Override
    public void run() {
        while(!interrupted()){
            if(threadPool.getQueue().size()>max_task_cache){
                while(threadPool.getQueue().size()>min_task_cache){
                    try {
                        logger.info("创建任务线程休息");
                        Thread.sleep(flush_cycle);
                    } catch (InterruptedException e) {
                        logger.error(e.toString());
                    }
                }
            }

            E take = service.take();
            if(take==null){
                try {
                    if(interrupted())return;
                    Thread.sleep(flush_cycle);
                } catch (InterruptedException e) {
                    logger.error(e.toString());
                }
            }else {
                long delay = getDelay(take);
                if(delay<=0){
                    threadPool.execute(getTask(take));
                }else {
                    threadPool.schedule(getTask(take),delay, TimeUnit.MILLISECONDS);
                }
                logger.debug("taskCount:{}",threadPool.getQueue().size());
            }
        }
    }

    //protected abstract ITask getTask(E obj);

    protected abstract Runnable getTask(E obj);

    protected abstract long getDelay(E obj);





}
