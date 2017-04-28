package com.capton.purecomic.Utils;

/**
 * Created by capton on 2017/4/20.
 */

public class VolleyParamUtil {
    public static String transformToNoSpaceString(String before){
        String after="";
        after=before.replace(" ","%20");
        return after;
    }
}
