package com.visualization.utils;

import java.util.HashMap;
import java.util.Map;

public class CompressNumberStringUtil {

    private static final String MASK62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final char[] ARR = MASK62.toCharArray();

    private static final Map<Character, Integer> MAP = new HashMap<>();

    private static final int LEN = MASK62.length();

    static {
        for (int i = 0; i < ARR.length; i++) {
            MAP.put(ARR[i], i);
        }
    }

    public static String zip(long num) {
        StringBuilder sb = new StringBuilder();
        if (num == 0) {
            sb.append("0");
        }
        while (num > 0) {
            sb.append(ARR[(int) (num % LEN)]);
            num = num / LEN;
        }
        return sb.reverse().toString();
    }

    public static long unzip(String string) {
        int end = string.length() - 1;
        char[] chars = string.toCharArray();
        long k = 1;
        long res = 0;
        while (end > -1){
            res = res + MAP.get(chars[end--]) * k;
            k *= LEN;
        }
        return res;
    }

    public static void main(String[] args) {
        System.err.println(MASK62.length());
    }
}
