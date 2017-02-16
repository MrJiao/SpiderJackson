package com.jackson.executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by Jackson on 2016/10/26.
 */
@Deprecated
public class PriorityTaskExecutor<T extends Runnable> {

    private  Logger logger = LogManager.getLogger(getClass().getName());
    private Thread queueThread;//用来获取队列任务的线程
    private ExecutorService threadPool;//执行任务的线程池

    private int maxThread;//默认最大执行线程数
    private PriorityBlockingQueue<Runnable> taskQueue;//任务队列

    /**
     * 添加任务
     * @param task
     */
    public void addTask(T task) {
        if(task==null)logger.error("task为Null");
        taskQueue.put(task);
    }

    public <T extends Runnable>void addTasks(List<T> tasks) {
        for(T task:tasks){
            taskQueue.put(task);
        }
    }


    /**
     * 创建对象
     * @param <T>
     * @return
     */
    public static <T extends Runnable> Builder builder() {
        return new Builder<T>();
    }

    private PriorityTaskExecutor() {}

    private void init() {
        threadPool = Executors.newFixedThreadPool(maxThread);
        queueThread = new Thread() {
            @Override
            public void run() {
                while (!interrupted()) {
                    Runnable task = take();
                    if (task != null)
                        threadPool.execute(task);
                }
            }
        };
        queueThread.setDaemon(true);
        queueThread.start();
    }

    private Runnable take() {
        Thread.yield();
        try {
            return taskQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Builder<T extends Runnable> {
        Builder() {
            super();
            this.maxThread = 3;
        }

        private int maxThread ;
        private Comparator<T> comparator;

        public Builder setMaxThread(int maxThread) {
            this.maxThread = maxThread;
            return this;
        }

        public Builder setComparator(Comparator comparator) {
            this.comparator = comparator;
            return this;
        }

        public PriorityTaskExecutor<T>  build() {
            PriorityTaskExecutor<T> executor = new PriorityTaskExecutor();
            executor.maxThread = this.maxThread;
            executor.taskQueue = new PriorityBlockingQueue(11, comparator);
            executor.init();
            return executor;
        }
    }
}
