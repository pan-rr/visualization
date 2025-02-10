package com.visualization.stage;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;


public class Visual {

    public static String time(String pattern){
        return DateFormatUtils.format(new Date(), pattern);
    }

    public static long timestamp(){
        return System.currentTimeMillis();
    }

}
