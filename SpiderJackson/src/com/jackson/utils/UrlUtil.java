package com.jackson.utils;

import com.jackson.db.po.Url;
import com.jackson.task.parser.IParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jackson on 2016/11/7.
 */
public class UrlUtil {

    public static List<Url> getHttpGetUrls(List<String> urlStrs, int priority, Class<? extends IParser> parserClass){
        ArrayList<Url> arr = new ArrayList();
        for(String s:urlStrs){
            Url url = Url.newHttpGetUrl(s, parserClass);
            url.setPriority(priority);
            arr.add(url);
        }
        return arr;
    }

}
