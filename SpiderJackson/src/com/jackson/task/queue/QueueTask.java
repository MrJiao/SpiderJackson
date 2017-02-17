package com.jackson.task.queue;

import com.jackson.bean.Bundle;

/**
 * Created by Jackson on 2017/2/9.
 */
public abstract class QueueTask implements Runnable{

    QueueTaskCollection collection;
    Bundle bundle;

    void setCollection(QueueTaskCollection collection) {
        this.collection = collection;
    }

    void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }


    @Override
    public void run() {
        collection.onProcess(doTask(bundle));
    }

    protected abstract QueueTaskCollection.TaskProcess doTask(Bundle bundle);

}
