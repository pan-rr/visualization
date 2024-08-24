package com.visualization.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {

    public static String md5Encode(String rawString){
        return DigestUtils.md5Hex(rawString);
    }
}
