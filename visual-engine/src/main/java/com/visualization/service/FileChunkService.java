package com.visualization.service;

import com.visualization.model.file.FileChunkParam;
import com.visualization.model.file.FileChunkRecord;
import com.visualization.model.file.FileChunkTask;

import java.util.Map;

public interface FileChunkService {

    FileChunkRecord getFileChunkRecordByMD5(String md5);

    FileChunkTask initFileChunkTask(FileChunkParam param);

    boolean tryQuickUpload(FileChunkParam param);

    FileChunkTask checkFileChunkTask(String md5);

    String generatePreSignedUrl(String ossKey, Map<String, String> params);

    void merge(String md5);
}
