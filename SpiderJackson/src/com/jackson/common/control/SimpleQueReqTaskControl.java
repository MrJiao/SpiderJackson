package com.jackson.common.control;

import com.jackson.bean.Bundle;
import com.jackson.common.source.CommonSource;
import com.jackson.common.task.SimpleQueueRequestTask;
import com.jackson.common.task.SimpleRequestTask;
import com.jackson.db.po.Proxy;
import com.jackson.db.po.Url;
import com.jackson.db.service.IDaoService;
import com.jackson.task.CreateTaskThread;
import com.jackson.task.queue.QueueTaskCollection;
import com.jackson.task.queue.QueueTaskCollections;
import com.jackson.task.queue.RequestQueueTask;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.LinkedList;
import java.util.concurrent.ScheduledThreadPoolExecutor;


/**
 * Created by Jackson on 2017/2/13.
 * 还有bug
 */
@Deprecated
public class SimpleQueReqTaskControl {
  /*  private final QueueTaskHandle handle;
    private ProxyController proxyController;
    private int maxTaskCache;
    private TaskThread taskThread;

    public SimpleQueReqTaskControl setMaxTaskCache(int maxTaskCache) {
        this.maxTaskCache = maxTaskCache;
        return this;
    }

    public SimpleQueReqTaskControl setMinTaskCache(int minTaskCache) {
        this.minTaskCache = minTaskCache;
        return this;
    }

    private int minTaskCache;
    private int threadSize;
    private QueueTaskCollections collections;
    private CommonSource source;
    private DelayHandle delayHandle;

    public static SimpleQueReqTaskControl newInstance(QueueTaskHandle handle) {
        return new SimpleQueReqTaskControl(handle);
    }

    public static SimpleQueReqTaskControl newInstance(QueueTaskHandle handle, ProxyController proxyController) {
        return new SimpleQueReqTaskControl(handle, proxyController);
    }


    private SimpleQueReqTaskControl(QueueTaskHandle handle, ProxyController proxyController) {
        this.handle = handle;
        this.proxyController = proxyController;
        collections = QueueTaskCollections.newInstance();
        source = CommonSource.newInstance();
        taskThread = new TaskThread(collections,source.getThreadPool());
    }

    private SimpleQueReqTaskControl(QueueTaskHandle handle) {
        this(handle, null);
    }

    public SimpleQueReqTaskControl setDelay(DelayHandle delayHandle) {
        this.delayHandle = delayHandle;
        return this;
    }

    public void start() {
        taskThread.setMinTaskCache(minTaskCache).setMaxTaskCache(maxTaskCache);
        source.getThreadPool().setCorePoolSize(threadSize);
        taskThread.start();
    }


    public SimpleQueReqTaskControl setThreadSize(int threadSize) {
        this.threadSize = threadSize;
        return this;
    }

    public interface Request {
        Url getUrl();

        HttpRequestBase getHttpRequest(HttpRequestBase request, Bundle bundle);

        QueueTaskCollection.TaskProcess onRequestFailed();

        boolean onResponseHandle(Proxy proxy, ProxyController proxyController, Url url, HttpRequestBase request, CloseableHttpResponse response, String content, Bundle bundle);

        QueueTaskCollection.TaskProcess onParseFinish(boolean parseSuccess, Url url, String content);
    }

    public static class Collection {

        private final Bundle bundle;
        private final LinkedList<Request> list;

        public Collection(Bundle bundle) {
            this.bundle = bundle;
            list = new LinkedList();
        }

        public void add(Request request) {
            list.add(request);
        }

        private Request remove() {
            return list.removeFirst();
        }

        private Bundle getBundle() {
            return bundle;
        }
    }

    private static class SimpleRequest extends SimpleQueueRequestTask {

        private final Request requestObj;

        public SimpleRequest(Request requestObj, CommonSource source) {
            super(requestObj.getUrl(), source);
            this.requestObj = requestObj;
        }

        @Override
        protected HttpRequestBase getHttpRequest(HttpRequestBase request, Bundle bundle) {
            return requestObj.getHttpRequest(request, bundle);
        }

        @Override
        protected QueueTaskCollection.TaskProcess onRequestFailed() {
            return requestObj.onRequestFailed();
        }

        @Override
        protected boolean onResponseHandle(Proxy proxy, ProxyController proxyController, Url url, HttpRequestBase request, CloseableHttpResponse response, String content, Bundle bundle) {
            return requestObj.onResponseHandle(proxy, proxyController, url, request, response, content, bundle);
        }

        @Override
        protected QueueTaskCollection.TaskProcess onParseFinish(boolean parseSuccess, Url url, String content) {
            return requestObj.onParseFinish(parseSuccess, url, content);
        }
    }

    private class TaskThread extends CreateTaskThread<RequestQueueTask> {

        public TaskThread(IDaoService<RequestQueueTask> service, ScheduledThreadPoolExecutor threadPool) {
            super(service, threadPool);
        }

        @Override
        protected Runnable getTask(RequestQueueTask task) {
            if (task == null) {
                Collection collection = handle.getTaskCollection();
                QueueTaskCollection queueTaskCollection = QueueTaskCollection.newInstance(collection.getBundle());
                queueTaskCollection.addLast(new SimpleRequest(collection.remove(), source));
                collections.add(queueTaskCollection);
            }
            if (proxyController != null) {
                task.setProxy();
                return task;
            } else {
                return task;
            }
        }

        @Override
        protected long getDelay(RequestQueueTask task) {
            if (delayHandle != null) {
                return delayHandle.getDelay(task.getUrl());
            } else {
                return 0;
            }
        }

    }

    public interface QueueTaskHandle {
        Collection getTaskCollection();
    }

    public interface DelayHandle {
        long getDelay(Url url);
    }
*/


}
