package com.visualisation.handler.file;

import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * 提供给用户自定义上传下载文件的方式
 * 自定义handler直接在spring容器注册就行
 */
public interface FileHandler {

    /**
     * @param sourcePath  文件源地址
     * @param uploadParam 下载参数
     * @return 文件
     */
    File download(String sourcePath, Map<?, ?> uploadParam);

    /**
     * 上传入口
     *
     * @param file        文件
     * @param targetPath  目标地址
     * @param uploadParam 上传参数
     */
    void upload(File file, String targetPath, Map<?, ?> uploadParam);


    default String generateRandomFilePathPrefix(){
        return new StringBuilder(System.getProperty("user.home"))
                .append( "/visual/temp/")
                .append(UUID.randomUUID())
                .append('/').toString();
    }

    default String generateRandomFilePath(String suffix){
        return new StringBuilder(System.getProperty("user.home"))
                .append( "/visual/temp/")
                .append(UUID.randomUUID())
                .append('/')
                .append(suffix)
                .toString();
    }

    default String generateFilePath(String suffix){
        return new StringBuilder(System.getProperty("user.home"))
                .append( "/visual/temp/")
                .append(suffix)
                .toString();
    }
}
