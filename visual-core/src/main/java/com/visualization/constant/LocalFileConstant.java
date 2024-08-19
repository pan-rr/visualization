package com.visualization.constant;

import java.util.UUID;

public class LocalFileConstant {

    public static final String BASE_LOCAL_PREFIX = System.getProperty("user.home") + "/visual/";

    public static final String BASE_TEMP_PREFIX = BASE_LOCAL_PREFIX + "temp/";

    public static final String BASE_TEMP_HTTP_PREFIX = BASE_TEMP_PREFIX + "http/";


    public static String getRandomHttpTempFilePath(String suffix) {
        return BASE_TEMP_HTTP_PREFIX + UUID.randomUUID() + '/' + suffix;
    }

    public static String getRandomTempFilePath(String suffix) {
        return BASE_TEMP_PREFIX + UUID.randomUUID() + '/' + suffix;
    }

    public static String getTempFilePath(String suffix) {
        return BASE_TEMP_PREFIX + suffix;
    }

    public static String getRandomTempFilePathPrefix() {
        return BASE_TEMP_PREFIX + UUID.randomUUID() + '/';
    }
}
