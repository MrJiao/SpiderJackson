package com.jackson.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
	
	private static String regexIp = "((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|[1-9]):\\d{1,4}";
	private static Pattern pattern;

	/**
	 * 匹配IP + 端口号 eg: 130.213.111.21:8090
	 * @param str
	 * @return
	 */
	public static List<String> matechIp(String str){
		if(pattern==null)
			pattern = Pattern.compile(regexIp);
		return match(pattern,str);
	}
	
	
	public static List<String> match(Pattern pattern,String str){
		ArrayList<String> arr = new ArrayList<String>();
	    Matcher matcher = pattern.matcher(str);
	    while(matcher.find()){
	    	String result = matcher.group();
	    	arr.add(result);
	    }
	    return arr;
	}

	public static String[] splitFirst(String str){
		return  str.split(":", 2);
	}

}
