package com.visualisation.constant;

public class MinIOConstant {

    public static final String BUCKET_NAME = "visual";
    public static final String PUBLIC = "public";
    public static final String VISUAL_BASE = "/visual/";
    public static final String VISUAL_STORAGE_PREFIX = VISUAL_BASE + "storage/";
    public static final String VISUAL_CONFIG_PREFIX = VISUAL_BASE + "config/";

    public static String getConfigPath(String suffix) {
        return VISUAL_CONFIG_PREFIX + suffix;
    }

    public static String getStoragePath(String suffix) {
        return VISUAL_STORAGE_PREFIX + suffix;
    }
}
