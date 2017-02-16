package com.jackson.db.po;

import com.jackson.utils.StringUtil;

import java.lang.reflect.Field;

/**
 * Created by Jackson on 2017/1/13.
 */
public class Proxy {
    public static int STATE_FREE = 0;//空闲
    public static int STATE_TAKEN_OUT = 1;//被取出
    public static int STATE_COMPLETE = 2;//完成

    public static int PROTOCOL_STATE_HTTP =0;
    public static int PROTOCOL_STATE_HTTPS=1;


    private Long id = -1L;
    private String host;
    private int port;
    private long acquisitionTime ;//采集时间
    private long lastTestTime ;//最后测试时间
    private long lastAvailableTime ;//最后测试并可用时间
    private String address;//所属地址
    private int useTimes ;//使用次数
    private long speed = -1;//访问速度
    private int state = 0;//取出状态
    private int testCount ;//测试次数
    private int availableCount ;//可用次数
    private int protocolState = PROTOCOL_STATE_HTTP;  //请求协议类型 http/https
    private int type;//http 类型还是https类型

    public Proxy(){}

    private Proxy(String host,int port,int type){
        this.host = host;
        this.port = port;
        this.type = type;
    }

    public static Proxy newHttpProxy(String host,int port){
        return new Proxy(host,port,PROTOCOL_STATE_HTTP);
    }

    public static Proxy newHttpSProxy(String host,int port){
        return new Proxy(host,port,PROTOCOL_STATE_HTTPS);
    }

    public int getProtocolState() {
        return protocolState;
    }

    public void setProtocolState(int protocolState) {
        this.protocolState = protocolState;
    }



    public long getLastAvailableTime() {
        return lastAvailableTime;
    }

    public void setLastAvailableTime(long lastAvailableTime) {
        this.lastAvailableTime = lastAvailableTime;
    }

    public int getTestCount() {
        return testCount;
    }

    public void setTestCount(int testCount) {
        this.testCount = testCount;
    }

    public int getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }



    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getAcquisitionTime() {
        return acquisitionTime;
    }

    public void setAcquisitionTime(long acquisitionTime) {
        this.acquisitionTime = acquisitionTime;
    }

    public long getLastTestTime() {
        return lastTestTime;
    }

    public void setLastTestTime(long lastTestTime) {
        this.lastTestTime = lastTestTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUseTimes() {
        return useTimes;
    }

    public void setUseTimes(int useTimes) {
        this.useTimes = useTimes;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Proxy{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", speed=" + speed +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        Proxy proxy = (Proxy) obj;
        return port == proxy.getPort() && StringUtil.equals(host,proxy.getHost());
    }


    public void copyOf(Proxy proxy){
        Field[] fields = Proxy.class.getDeclaredFields();
        for(Field field:fields){
            field.setAccessible(true);
            try {
                field.set(this,field.get(proxy));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(false);
        }
    }
}
