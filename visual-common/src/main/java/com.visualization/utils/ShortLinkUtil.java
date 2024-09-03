package com.visualization.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class ShortLinkUtil {
    public static String zip(String str) {
        long l = zipToInt(str);
        return Base62Util.zip(l);
    }

    public static int zipToInt(String str) {
        return Hashing.murmur3_32().hashString(str, StandardCharsets.UTF_8).asInt();
    }

}
