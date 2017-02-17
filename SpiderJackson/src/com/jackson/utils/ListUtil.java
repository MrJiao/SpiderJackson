package com.jackson.utils;

import java.lang.reflect.Field;
import java.util.List;

import static javax.swing.UIManager.getInt;

/**
 * Created by Jackson on 2016/11/4.
 */
public class ListUtil {

    public static <T>T getMaxValue(List<T> list,String filedName){
        T obj = list.get(0);
        Field declaredField =null;

        int position=-1;
        int maxValue=-1;
        try {
             declaredField = obj.getClass().getDeclaredField(filedName);
            declaredField.setAccessible(true);
            for(int i=0;i<list.size();i++){
                int value = declaredField.getInt(list.get(i));
                if(i==0){
                    maxValue = value;
                    position = i;
                    continue;
                }
                if(maxValue<value){
                    maxValue = value;
                    position = i;
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return list.get(position);
    }


    /**
     *
     * @param list
     * @param filedName 这个字段必须为int型才可以用
     * @param <T>
     * @return
     */
    public static <T>T getMinValue(List<T> list,String filedName){
        T obj = list.get(0);
        Field declaredField =null;

        int position = 0;
        int maxValue=-1;
        try {
            declaredField = obj.getClass().getDeclaredField(filedName);
            declaredField.setAccessible(true);
            for(int i=0;i<list.size();i++){
                int value = declaredField.getInt(list.get(i));
                if(i==0){
                    maxValue = value;
                    position = i;
                    continue;
                }
                if(maxValue>value){
                    maxValue = value;
                    position = i;
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return list.get(position);
    }

    public static boolean isEmpty(List list){
        if(list==null)return true;
        if(list.size()==0)return true;
        return false;
    }


}
