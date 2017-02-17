package com.jackson.reservoir;

import com.jackson.bean.ContextSrc;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Jackson on 2017/1/6.
 */
public class ContextSrcPool {

    private ContextSrcPool() {
        map = new HashMap<>();

    }

    private HashMap<String, LinkedBlockingQueue<ContextSrc>> map;

    public static ContextSrcPool newInstance(){
        return new ContextSrcPool();
    }

    /**
     * 获取新的ContextSrc
     * @return
     */
    public synchronized ContextSrc take() {
        return new ContextSrc();
    }

    public synchronized void offer(String tag, ContextSrc contextSrc){
        if(!map.containsKey(tag)){
            map.put(tag,new LinkedBlockingQueue<>());
        }
        map.get(tag).offer(contextSrc);
    }


    public synchronized ContextSrc take(String tag){
        if(!map.containsKey(tag))return null;
        ContextSrc contextSrc = null;
        try {
            contextSrc = map.get(tag).take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return contextSrc;
    }

}
