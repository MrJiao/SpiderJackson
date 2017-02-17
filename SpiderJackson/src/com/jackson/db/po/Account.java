package com.jackson.db.po;

/**
 * Created by Jackson on 2016/11/14.
 */
public class Account {

    public static int STATE_FREE = 0;//为被取出
    public static int STATE_TAKEN_OUT = 1;//已取出


    private String userName;
    private String passWord;
    private String cookie;
    private long lastLoginTime;
    private int state;
    private long unLockedTime;//解锁时间

    public long getUnLockedTime() {
        return unLockedTime;
    }

    public void setUnLockedTime(long unLockedTime) {
        this.unLockedTime = unLockedTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
