package com.jackson.task.queue;

import com.jackson.db.service.IDaoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by Jackson on 2017/2/9.
 * 用来装QueueTaskCollection的容器
 * 线程不安全的
 */
public class QueueTaskCollections implements IDaoService<RequestQueueTask> {

    private static Logger logger = LogManager.getLogger(QueueTaskCollections.class.getName());
    public static QueueTaskCollections newInstance() {
        return new QueueTaskCollections();
    }

    private QueueTaskCollections() {
        list = new ArrayList<>();
    }

    @Override
    public RequestQueueTask take() {
        RequestQueueTask task = null;
        ListIterator<QueueTaskCollection> iterator = list.listIterator();
        while (iterator.hasNext()) {
            QueueTaskCollection collection = iterator.next();
            if (collection.getState() == QueueTaskCollection.CollectionState.CAN_TAKE) {
                task = collection.getTask();
                break;
            }
            if (collection.getState() == QueueTaskCollection.CollectionState.COMPLETE) {
                iterator.remove();
                continue;
            }
            if (collection.getState() == QueueTaskCollection.CollectionState.CANT_TAKE) {
                continue;
            }
        }
        return task;
    }

    private ArrayList<QueueTaskCollection> list;

    public void add(QueueTaskCollection collection) {
        list.add(collection);
    }

}
