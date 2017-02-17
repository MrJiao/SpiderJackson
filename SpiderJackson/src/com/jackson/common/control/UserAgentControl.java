package com.jackson.common.control;

import com.jackson.utils.StringUtil;
import org.apache.ibatis.io.Resources;

import java.io.*;
import java.util.*;

/**
 * Created by Jackson on 2017/2/7.
 * 1、分类遍历获取user-agent
 */
public class UserAgentControl {
    private static UserAgentControl instance;
    private UserAgentControl() {}

    /**
     * 控制资源文件存放位置的
     */
    private static final String FILE_PATH = "config/";

    public static UserAgentControl getInstance() {
        if (instance == null) {
            synchronized (UserAgentControl.class) {
                if (instance == null) {
                    instance = new UserAgentControl();
                }
            }
        }
        return instance;
    }

    private int currentPosition;
    /**
     * @return 传入的类型的user-agent
     */
    public String next(UserAgentType ... userAgentTypes){
        String s = userAgentTypes[currentPosition].next();
        if(s==null){
            if(userAgentTypes.length-1<=currentPosition){//到最后了
                currentPosition=0;
                s = userAgentTypes[currentPosition].next();
            }else {
                s = userAgentTypes[++currentPosition].next();
            }
        }
        return s;
    }


    public  enum UserAgentType{
        PC("pc_user_agent"),ANDROID_1_0("android1.0_user_agent"),ANDROID_2_0("android2.0_user_agent"),
        ANDROID_3_0("android3.0_user_agent"),ANDROID_4_0("android4.0_user_agent"),IPHONE("iphone_user_agent"),
        MAC_OS("macos_user_agent"),OLD_PHONE("oldphone_user_agent"), WAP("wap_user_agent"),
        SYMBIANOS9("symbianos9"),CLDC("cldc_user_agent");

        String fileName;
        ArrayList<String> userAgentList;
        int position;
        UserAgentType(String fileName){
            this.fileName = fileName;
        }

        public ArrayList<String> getUserAgentList() {
            return userAgentList;
        }

        String next(){
            loadRes();
            if(getUserAgentList().size()-1<position){
                position=0;
                return null;
            }
            return getUserAgentList().get(position++);
        }

        private void loadRes(){
            if(userAgentList!=null)return;

            userAgentList = new ArrayList<>();
            BufferedReader br = loadReader(FILE_PATH+fileName);
            String s = null;
            try {
                while ((s = br.readLine()) != null) {
                    if(!StringUtil.isEmpty(s)){
                        getUserAgentList().add(s.trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private BufferedReader loadReader(String res) {
            //得到配置文件的流
            InputStream is = null;
            try {
                is = Resources.getResourceAsStream(res);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new BufferedReader(new InputStreamReader(is));
        }
    }

}
