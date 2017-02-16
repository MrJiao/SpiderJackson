package com.jackson.executor;

import com.jackson.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Jackson on 2016/12/23.
 * 单线程执行，对数据库的操作
 *
 * 添加任务的时候,如果任务类相同,将老的移除,变成新的
 */
public enum DBSingleExecutor {
    instance;
    private Logger logger = LogManager.getLogger(DBSingleExecutor.class.getName());
    LinkedBlockingQueue<DBTask> taskQueue;
    private final Thread executor;

    DBSingleExecutor(){
        taskQueue = new LinkedBlockingQueue();
        executor = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        DBTask task = taskQueue.take();
                        logger.debug("执行任务:{}",task.getClass().getName());
                        task.run();
                    } catch (Exception e) {
                        logger.error(e.toString());
                    }
                }
            }
        };
        executor.start();
    }

    public static abstract class DBTask implements Runnable{
        @Override
        public boolean equals(Object obj) {
            return StringUtil.equals(obj.getClass().getName(),this.getClass().getName());
        }
    }


    public void execute(DBTask task){
        if(!taskQueue.contains(task)){
            taskQueue.offer(task);
            logger.debug("插入任务成功:{} 当前任务数:{}",task.getClass().getName(),taskQueue.size());
        }
    }


}
