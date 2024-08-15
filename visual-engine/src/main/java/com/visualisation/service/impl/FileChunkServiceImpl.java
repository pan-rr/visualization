package com.visualisation.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.visualisation.constant.OSSConstant;
import com.visualisation.model.file.FileChunkParam;
import com.visualisation.model.file.FileChunkRecord;
import com.visualisation.model.file.FileChunkTask;
import com.visualisation.repository.file.FileChunkDetailRepository;
import com.visualisation.service.FileChunkService;
import io.minio.MinioClient;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FileChunkServiceImpl implements FileChunkService {

    @Resource(name = "visualS3Client")
    private AmazonS3 amazonS3;

    @Resource(name = "visualMinioClient")
    private MinioClient minioClient;

    @Value("${visual.storage.minio.endpoint}")
    private String endpoint;

    @Resource
    private FileChunkDetailRepository fileChunkDetailRepository;

    private String getPath(String ossKey) {
        return MessageFormat.format("{0}/{1}/{2}", endpoint, OSSConstant.BUCKET_NAME, ossKey);
    }

    private FileChunkTask getTaskByRecord(FileChunkRecord record) {
        return FileChunkTask.builder()
                .finished(false)
                .path(getPath(record.getOssKey()))
                .fileChunkRecord(record)
                .build();
    }


    @Override
    public FileChunkRecord getFileChunkRecordByMD5(String md5) {
        return fileChunkDetailRepository.findByMD5(md5);
//        FileChunkRecord record = fileChunkDetailRepository.findByMD5(md5);
//        if (Objects.isNull(record)) {
//            throw new RuntimeException("分片任务不存在！");
//        }
//        return record;
    }

    @Override
    public FileChunkTask initFileChunkTask(FileChunkParam param) {
        FileChunkRecord fileChunkRecord = param.buildDetailByParam(amazonS3);
        fileChunkDetailRepository.save(fileChunkRecord);
        return getTaskByRecord(fileChunkRecord);
    }

    @Override
    public FileChunkTask checkFileChunkTask(String md5) {
        FileChunkRecord record = getFileChunkRecordByMD5(md5);
        if (Objects.isNull(record)) {
            return null;
        }
        FileChunkTask task = getTaskByRecord(record);
        boolean doesObjectExist = amazonS3.doesObjectExist(OSSConstant.BUCKET_NAME, task.getPath());
        task.setFinished(doesObjectExist);
        if (!doesObjectExist) {
            // 未上传完，返回已上传的分片
            ListPartsRequest listPartsRequest = new ListPartsRequest(OSSConstant.BUCKET_NAME, record.getOssKey(), record.getUploadId());
            PartListing partListing = amazonS3.listParts(listPartsRequest);
            task.setFinished(false);
            record.setExitPartList(partListing.getParts());
        }
        return task;
    }

    @Override
    public String generatePreSignedUrl(String ossKey, Map<String, String> params) {
        Date now = new Date();
        // 必须在1小时内上传完
        Date expireDate = DateUtils.addHours(now, 1);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(OSSConstant.BUCKET_NAME, ossKey);
        request.withExpiration(expireDate).withMethod(HttpMethod.PUT);
        if (params != null) {
            request.getRequestParameters().putAll(params);
        }
        URL preSignedUrl = amazonS3.generatePresignedUrl(request);
        return preSignedUrl.toString();
    }


    @Override
    public void merge(String md5) {
        FileChunkRecord record = getFileChunkRecordByMD5(md5);
        ListPartsRequest listPartsRequest = new ListPartsRequest(OSSConstant.BUCKET_NAME, record.getOssKey(), record.getUploadId());
        PartListing partListing = amazonS3.listParts(listPartsRequest);
        List<PartSummary> parts = partListing.getParts();
        if (!record.getChunkNum().equals(parts.size())) {
            // 已上传分块数量与记录中的数量不对应，不能合并分块
            throw new RuntimeException("分片缺失，请重新上传");
        }
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest()
                .withUploadId(record.getUploadId())
                .withKey(record.getOssKey())
                .withBucketName(record.getBucketName())
                .withPartETags(parts.stream().map(partSummary -> new PartETag(partSummary.getPartNumber(), partSummary.getETag())).collect(Collectors.toList()));
        amazonS3.completeMultipartUpload(completeMultipartUploadRequest);
    }
}
