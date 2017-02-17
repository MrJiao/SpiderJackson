package com.jackson.db.po;

import com.jackson.utils.StringUtil;

/**
 * Created by Jackson on 2016/11/3.
 */
@Deprecated
public class Ip {
    public static int STATE_FREE = 0;//空闲
    public static int STATE_TAKEN_OUT = 1;//被取出
    public static int STATE_COMPLETE = 2;//完成

    public static int PROTOCOL_STATE_HTTP =0;
    public static int PROTOCOL_STATE_HTTPS=1;


    private Long id;
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

    public int getProtocolState() {
        return protocolState;
    }

    public void setProtocolState(int protocolState) {
        this.protocolState = protocolState;
    }

    //请求协议类型 http/https
    private int protocolState = PROTOCOL_STATE_HTTP;

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

    private int type;//http 类型还是https类型

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
        return "Ip{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", speed=" + speed +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        Ip ip = (Ip) obj;
        return port == ip.getPort() && StringUtil.equals(host,ip.getHost());
    }
}
