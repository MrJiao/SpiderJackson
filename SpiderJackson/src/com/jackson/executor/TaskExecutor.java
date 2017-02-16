package com.jackson.executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 队列任务执行者，可控制最大执行线程数 {最大并发数 @ maxThread}
 *
 * @author Jackson
 */
@Deprecated
public class TaskExecutor {

    private Thread queueThread;//用来获取队列任务的线程
    private ExecutorService threadPool;//执行任务的线程池
    private Logger logger = LogManager.getLogger(getClass().getName());
    public TaskExecutor(int maxThread) {
        this.maxThread = maxThread;
    }

    private int maxThread = 3;//默认最大执行线程数
    private LinkedBlockingQueue<Runnable> taskQueue;//任务队列

    public TaskExecutor() {
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

        threadPool = Executors.newFixedThreadPool(maxThread);

        taskQueue = new LinkedBlockingQueue<>();
        queueThread.start();
    }

    private Runnable take() {
        try {
            return taskQueue.take();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    public void addTask(Runnable task) {
        try {
            //System.out.println("taskQueue size"+taskQueue.size());
            taskQueue.put(task);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
