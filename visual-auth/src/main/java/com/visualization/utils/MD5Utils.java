package com.visualization.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {

    public static String md5Encode(String rawString){
        return DigestUtils.md5Hex(rawString);
    }

    public static void main(String[] args) {
        System.err.println(MD5Utils.md5Encode("1277751434263134208"));
        System.err.println(Long.toHexString(1277751434263134208L));
        System.err.println(1277751434263134208L);
    }
}
