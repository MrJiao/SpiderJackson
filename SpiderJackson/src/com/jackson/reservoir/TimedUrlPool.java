package com.jackson.reservoir;

import com.jackson.db.po.Url;
import com.jackson.db.service.IDaoService;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Jackson on 2016/12/27.
 * 用来存放url的， 在定时任务里用到
 */
public class TimedUrlPool implements IDaoService<TimedUrlPool.TimedUrl> {

    private final LinkedBlockingQueue<TimedUrl> queueTimedUrls;

    public TimedUrlPool(){
        queueTimedUrls = new LinkedBlockingQueue<>();
    }

    public TimedUrl take(){
        try {
            return queueTimedUrls.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void offer(TimedUrl timedUrl){
        queueTimedUrls.offer(timedUrl);
    }

    public int size(){
        return queueTimedUrls.size();
    }

    public static abstract class TimedUrl{
        public abstract Url getUrl();
        public abstract long getDelay();
    }


}
