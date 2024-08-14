package com.visualisation.service;

import com.visualisation.model.file.FileChunkParam;
import com.visualisation.model.file.FileChunkRecord;
import com.visualisation.model.file.FileChunkTask;

import java.util.Map;

public interface FileChunkService {

    FileChunkRecord getFileChunkRecordByMD5(String md5);

    FileChunkTask initFileChunkTask(FileChunkParam param);


    FileChunkTask checkFileChunkTask(String md5);

    String generatePreSignedUrl(String ossKey, Map<String, String> params);

    void merge(String md5);
}
