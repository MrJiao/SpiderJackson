package com.jackson.db.test;

/**
 * Created by Jackson on 2016/12/12.
 */
public class IpServiceTest {


    public void start(){
        new Thread(){
            @Override
            public void run() {
                while (!interrupted()){
                    addIp();


                }
            }
        }.start();
    }

    private void addIp(){

    }


}
