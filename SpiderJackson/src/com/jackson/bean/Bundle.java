package com.jackson.bean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * Created by Jackson on 2017/1/17.
 */
public class Bundle {

    private static Logger logger = LogManager.getLogger(Bundle.class.getName());
    HashMap<String, Object> mMap = null;


    private Bundle(){
        mMap = new HashMap<>();
    }

    public static Bundle newInstance(){
        return new Bundle();
    }



    public void put(String key,Object obj){
        mMap.put(key, obj);
    }

    public Object get(String key,Object defaultValue){
        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return o;
        } catch (ClassCastException e) {
            logger.warn("key{},Type{},defaultValue{},Exception{}",key,"Object",defaultValue,e);
            return defaultValue;
        }
    }



    public void putBoolean(String key,int value){
        mMap.put(key,value);
    }

    public boolean getBoolean(String key,boolean defaultValue){
        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (boolean) o;
        } catch (ClassCastException e) {
            logger.warn("key{},Type{},defaultValue{},Exception{}",key,"boolean",defaultValue,e);
            return defaultValue;
        }
    }

    public void putInt(String key,int value){
        mMap.put(key,value);
    }

    public int getInt(String key,int defaultValue){
        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (int) o;
        } catch (ClassCastException e) {
            logger.warn("key{},Type{},defaultValue{},Exception{}",key,"int",defaultValue,e);
            return defaultValue;
        }
    }

    public void putString(String key,String value){
        mMap.put(key,value);
    }

    public String getString(String key,String defaultValue){
        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (String) o;
        } catch (ClassCastException e) {
            logger.warn("key{},Type{},defaultValue{},Exception{}",key,"String",defaultValue,e);
            return defaultValue;
        }
    }

    public void putLong(String key,long value){
        mMap.put(key,value);
    }

    public long getLong(String key,long defaultValue){
        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (long) o;
        } catch (ClassCastException e) {
            logger.warn("key{},Type{},defaultValue{},Exception{}",key,"long",defaultValue,e);
            return defaultValue;
        }
    }

}
