package com.jackson.db.test;

import com.jackson.db.po.Url;
import com.jackson.db.service.UrlService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jackson on 2016/11/6.
 */
public class UrlServiceTest {


    private UrlService urlService;




    public class AddThread extends Thread {
        private long sleepTime=0;

        public AddThread setSleepTime(long sleepTime){
            this.sleepTime = sleepTime;
            return this;
        }
        @Override
        public void run() {
            while(!isInterrupted()){
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                urlService.insert(getUrls(10));
            }
        }
    }


    public class GetThread extends Thread {
        private long sleepTime=0;

        public GetThread setSleepTime(long sleepTime){
            this.sleepTime = sleepTime;
            return this;
        }
        @Override
        public void run() {
            while(!isInterrupted()){
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Url url = urlService.take();
                urlService.completeUrl(url);
            }
        }
    }

    private static int id=1;
    private List<Url> getUrls(int size){
        ArrayList<Url> arr = new ArrayList<>();
        for(int i=0;i<size;i++){
            Url url = Url.newHttpGetUrl("UrlDaoTest");
            url.setState(0);
            url.setUrl("urlurlurl"+(++id));
            url.setPriority(i);
            arr.add(url);
        }
        return arr;
    }



}
