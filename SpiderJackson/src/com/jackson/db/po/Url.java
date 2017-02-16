package com.jackson.db.po;

import com.jackson.bean.Bundle;
import com.jackson.bean.ContextSrc;
import com.jackson.net.RequestConfig;
import com.jackson.task.parser.IParser;
import com.jackson.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Jackson on 2016/11/3.
 */
public class Url {
    private static Logger logger = LogManager.getLogger(Url.class.getName());
    public static int REQUEST_STATE_GET = 0;
    public static int REQUEST_STATE_POST = 1;

    public static int PROTOCOL_STATE_HTTP = 0;
    public static int PROTOCOL_STATE_HTTPS = 1;

    public static int STATE_FREE = 0;//空闲
    public static int STATE_TAKE_OUT = 1;//被取出
    public static int STATE_COMPLETE = 2;//完成
    private Bundle bundle;
    private ContextSrc contextSrc;

    public ContextSrc getContextSrc() {
        return contextSrc;
    }

    public void setContextSrc(ContextSrc contextSrc) {
        this.contextSrc = contextSrc;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long id;
    private String url;
    private int state = 0;//取出状态

    private String parserClassName;

    private int priority = 1;//优先级

    private long createTime;//创建时间

    //请求类型 get/post 默认
    private int requestState = REQUEST_STATE_GET;

    //请求协议类型 http/https
    private int protocolState = PROTOCOL_STATE_HTTP;

    private int parserFailureTime = 0;//解析次数,不存数据库,内存中使用

    private String requestConfigClassName;

    /**
     * 不推荐使用，让他public 主要还是因为mybatis的映射需要，如果要创建url对象可以使用newHttpxxUrl()系列
     */
    private Url() {
    }

    private static Url newUrl(String url, Class<? extends RequestConfig> requestConfigClass, Class<? extends IParser> parserClass, int protocolState, int requestState) {
        Url u = new Url();
        u.setUrl(url);
        u.setRequestConfigClass(requestConfigClass);
        u.setParserClass(parserClass);
        u.setProtocolState(protocolState);
        u.setRequestState(requestState);
        return u;
    }

    //---------
    public static Url newHttpPostUrl(String url, Class<? extends RequestConfig> requestConfigClass, Class<? extends IParser> parserClass) {
        return newUrl(url, requestConfigClass, parserClass, Url.PROTOCOL_STATE_HTTP, Url.REQUEST_STATE_POST);
    }

    public static Url newHttpPostUrl(String url,  Class<? extends IParser> parserClass) {
        return newUrl(url, null, parserClass, Url.PROTOCOL_STATE_HTTP, Url.REQUEST_STATE_POST);
    }

    public static Url newHttpPostUrl(String url) {
        return newUrl(url, null, null, Url.PROTOCOL_STATE_HTTP, Url.REQUEST_STATE_POST);
    }

    //-------------
    public static Url newHttpsPostUrl(String url, Class<? extends RequestConfig> requestConfigClass, Class<? extends IParser> parserClass) {
        return newUrl(url, requestConfigClass, parserClass, Url.PROTOCOL_STATE_HTTPS, Url.REQUEST_STATE_POST);
    }

    public static Url newHttpsPostUrl(String url, Class<? extends IParser> parserClass) {
        return newUrl(url, null, parserClass, Url.PROTOCOL_STATE_HTTPS, Url.REQUEST_STATE_POST);
    }

    public static Url newHttpsPostUrl(String url) {
        return newUrl(url, null, null, Url.PROTOCOL_STATE_HTTPS, Url.REQUEST_STATE_POST);
    }

    //-----------
    public static Url newHttpGetUrl(String url, Class<? extends RequestConfig> requestConfigClass, Class<? extends IParser> parserClass) {
        return newUrl(url, requestConfigClass, parserClass, Url.PROTOCOL_STATE_HTTP, Url.REQUEST_STATE_GET);
    }

    public static Url newHttpGetUrl(String url, Class<? extends IParser> parserClass) {
        return newUrl(url, null, parserClass, Url.PROTOCOL_STATE_HTTP, Url.REQUEST_STATE_GET);
    }

    public static Url newHttpGetUrl(String url) {
        return newUrl(url, null, null, Url.PROTOCOL_STATE_HTTP, Url.REQUEST_STATE_GET);
    }

    //------------
    public static Url newHttpsGetUrl(String url, Class<? extends RequestConfig> requestConfigClass, Class<? extends IParser> parserClass){
        return newUrl(url,requestConfigClass,parserClass,Url.PROTOCOL_STATE_HTTPS,Url.REQUEST_STATE_GET);
    }

    public static Url newHttpsGetUrl(String url, Class<? extends IParser> parserClass){
        return newUrl(url,null,parserClass,Url.PROTOCOL_STATE_HTTPS,Url.REQUEST_STATE_GET);
    }

    public static Url newHttpsGetUrl(String url){
        return newUrl(url,null,null,Url.PROTOCOL_STATE_HTTPS,Url.REQUEST_STATE_GET);
    }




    //------------
    public Class<? extends RequestConfig> getRequestConfigClass() {
        if (StringUtil.isEmpty(requestConfigClassName)) return null;
        try {
            return (Class<? extends RequestConfig>) Class.forName(requestConfigClassName);
        } catch (ClassNotFoundException e) {
            logger.error(e.toString());
        }
        return null;
    }

    public void setRequestConfigClass(Class<? extends RequestConfig> requestConfigClass) {
        if (requestConfigClass != null)
            this.requestConfigClassName = requestConfigClass.getName();
    }

    public int getRequestState() {
        return requestState;
    }

    public void setRequestState(int requestState) {
        this.requestState = requestState;
    }

    public int getProtocolState() {
        return protocolState;
    }

    public void setProtocolState(int protocolState) {
        this.protocolState = protocolState;
    }

    public String getProtocolScheme() {
        if (protocolState == 1) return "https";
        if (protocolState == 0) return "http";
        throw new RuntimeException("协议类型错误");
    }


    public int getParserFailureTime() {
        return parserFailureTime;
    }

    public void setParserFailureTime(int parserFailureTime) {
        this.parserFailureTime = parserFailureTime;
    }

    public Long getId() {
        return id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Class<? extends IParser> getParserClass() {
        if (StringUtil.isEmpty(parserClassName)) return null;
        try {
            return (Class<? extends IParser>) Class.forName(parserClassName);
        } catch (ClassNotFoundException e) {
            logger.error(e.toString());
        }
        return null;
    }

    public void setParserClass(Class<? extends IParser> clazz) {
        if (clazz != null)
            this.parserClassName = clazz.getName();
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Url{" +
                ", url='" + url + '\'' +
                ", state=" + state +
                ", parserClassName='" + parserClassName + '\'' +
                ", priority=" + priority +
                '}';
    }

    /**
     * 不会存入数据库
     *
     * @return
     */
    public Bundle getBundle() {
        if (bundle == null)
            bundle = Bundle.newInstance();
        return bundle;
    }


    @Override
    public boolean equals(Object obj) {
        Url u = (Url) obj;
        return StringUtil.equals(this.getUrl(), u.getUrl());
    }
}
