package com.jackson.utils;

import com.sun.istack.internal.Nullable;

/**
 * Created by Jackson on 2016/10/26.
 */
public class StringUtil {

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }


    public static String append(String...sts){
        StringBuilder sb = new StringBuilder();
        for(String s:sts){
            sb.append(s);
        }
        return sb.toString();
    }


    public static boolean isEmpty(@Nullable CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
}
