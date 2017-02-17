package com.demo;

import com.jackson.common.control.UserAgentControl;

/**
 * Created by Jackson on 2017/2/16.
 * 获取user-agent的例子
 * 库里内置了很多user-agent，可以轻松的获取
 */
public class UserAgentDemo {


    public static void main(String[] args){
        UserAgentControl userAgentControl = UserAgentControl.getInstance();
        for(int i=0;i<100;i++){
            //每次next都会获取不同的userAgent，入参是选择userAgent的类型(wap类型的 ，pc类型的，android类型的等）
            //可以传入一种类型也可传入多种
            String userAgent = userAgentControl.next(UserAgentControl.UserAgentType.PC);
            System.out.println(userAgent);
        }

    }
}
