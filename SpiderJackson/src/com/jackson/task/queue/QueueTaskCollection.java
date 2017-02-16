package com.jackson.task.queue;

import com.jackson.bean.Bundle;
import com.jackson.db.po.Proxy;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Jackson on 2017/2/9.
 * 串行任务，通过任务的返回值 TaskProcess 来决定任务的执行和取消
 *
 */
public class QueueTaskCollection {


    private ArrayList<RequestQueueTask> taskList;
    private Bundle bundle;
    private int currentTaskPosition;
    private CollectionState state = CollectionState.CAN_TAKE;
    private Proxy proxy;

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public static QueueTaskCollection newInstance(Bundle bundle) {
        return new QueueTaskCollection(bundle);
    }

    private QueueTaskCollection(Bundle bundle) {
        taskList = new ArrayList<>();
        if (bundle != null) {
            this.bundle = bundle;
        } else {
            this.bundle = Bundle.newInstance();
        }
    }

    public void addLast(RequestQueueTask task) {
        task.setBundle(bundle);
        task.setCollection(this);
        taskList.add(task);
    }


    public synchronized RequestQueueTask getTask() {
        if (state == CollectionState.CANT_TAKE) return null;
        if (currentTaskPosition < taskList.size()) {
            RequestQueueTask queueTask = taskList.get(currentTaskPosition);
            if (currentTaskPosition == taskList.size() - 1) {//取到最后一个时
                state = CollectionState.COMPLETE;
            } else {
                state = CollectionState.CANT_TAKE;
            }
            return queueTask;
        } else {
            return null;
        }
    }


    public synchronized void onProcess(TaskProcess taskProcess) {
        if (state == CollectionState.COMPLETE) {
            if (taskProcess == TaskProcess.NEXT || taskProcess == TaskProcess.END)
                return;
        }
        switch (taskProcess) {
            case AGAIN:
                state = CollectionState.CAN_TAKE;

                break;
            case NEXT:
                currentTaskPosition++;
                state = CollectionState.CAN_TAKE;
                break;
            case END:
                currentTaskPosition = taskList.size();
                state = CollectionState.COMPLETE;
                break;
            case ALL_AGAIN:
                currentTaskPosition = 0;
                state = CollectionState.CAN_TAKE;

                break;
        }
    }

    public synchronized CollectionState getState() {
        return state;
    }


    public enum TaskProcess {
        /**
         * 再做一次本任务
         */
        AGAIN,
        /**
         * 下一个任务
         */
        NEXT,
        /**
         * 结束所有任务
         */
        END,
        /**
         * 所有任务重来
         */
        ALL_AGAIN
    }

    public enum CollectionState {
        CAN_TAKE, CANT_TAKE, COMPLETE
    }

}
