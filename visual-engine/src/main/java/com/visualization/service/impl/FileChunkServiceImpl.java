package com.visualization.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.visualization.constant.OSSConstant;
import com.visualization.exception.UploadException;
import com.visualization.model.file.*;
import com.visualization.repository.file.FileChunkRecordRepository;
import com.visualization.repository.file.FileLaunchRecordRepository;
import com.visualization.repository.file.FilePathMappingRepository;
import com.visualization.service.FileChunkService;
import com.visualization.service.MinIOService;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class FileChunkServiceImpl implements FileChunkService {

    @Resource(name = "visualS3Client")
    private AmazonS3 amazonS3;

    @Resource
    private MinIOService minIOService;

    @Value("${visual.storage.minio.endpoint}")
    private String endpoint;

    @Resource(name = "dagTransactionTemplate")
    private TransactionTemplate transactionTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private FileChunkRecordRepository fileChunkRecordRepository;

    @Resource
    private FilePathMappingRepository filePathMappingRepository;

    @Resource
    private FileLaunchRecordRepository fileLaunchRecordRepository;

    private String getPath(String ossKey) {
        return MessageFormat.format("{0}/{1}/{2}", endpoint, OSSConstant.BUCKET_NAME, ossKey);
    }

    private FileChunkTask getTaskByRecord(FileChunkRecord record) {
        return FileChunkTask.builder()
                .finished(record.getFinished())
                .path(getPath(record.getOssKey()))
                .fileChunkRecord(record)
                .build();
    }


    @Override
    public FileChunkRecord getFileChunkRecordByMD5(String md5) {
        return fileChunkRecordRepository.findByMD5(md5);
    }

    @Override
    public FileChunkTask initFileChunkTask(FileChunkParam param) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String uploadLockKey = param.computeUploadLockKey();
        Boolean flag = ops.setIfAbsent(uploadLockKey, param.getFileName(), 1, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(flag)) {
            throw new UploadException("系统正在上传一个高相似度的文件，请稍后再上传" + param.getFileName());
        }
        FileLaunchRecord launchRecord = FileLaunchRecord.getLaunchRecord(param);
        FileChunkRecord fileChunkRecord = param.buildDetailByParam(amazonS3);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                fileChunkRecordRepository.save(fileChunkRecord);
                fileLaunchRecordRepository.save(launchRecord);
            }
        });
        redisTemplate.delete(uploadLockKey);
        return getTaskByRecord(fileChunkRecord);
    }

    @Override
    public boolean tryQuickUpload(FileChunkParam param) {
        FileChunkRecord record = fileChunkRecordRepository.findByMD5(param.getMd5());
        if (Objects.isNull(record)) {
            return false;
        }
        String targetOSSKey = param.computeOSSKey();
        String sourceOSSKey = record.getOssKey();
        if (StringUtils.equals(sourceOSSKey, targetOSSKey)) {
            return true;
        }
        if (Boolean.TRUE.equals(record.getFinished())) {
            FilePathMapping mapping = FilePathMapping.buildMapping(param, record);
            filePathMappingRepository.save(mapping);
            return true;
        }
        return false;
    }

    @Override
    public FileChunkTask checkFileChunkTask(String md5) {
        FileChunkRecord record = getFileChunkRecordByMD5(md5);
        if (Objects.isNull(record)) {
            return null;
        }
        FileChunkTask task = getTaskByRecord(record);
        task.checkFinished(amazonS3);
        return task;
    }

    @Override
    public String generatePreSignedUrl(String ossKey, Map<String, String> params) {
        Date now = new Date();
        // 必须在1小时内处理完
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
        if (Boolean.TRUE.equals(record.getFinished())) return;
        ListPartsRequest listPartsRequest = new ListPartsRequest(OSSConstant.BUCKET_NAME, record.getOssKey(), record.getUploadId());
        PartListing partListing = amazonS3.listParts(listPartsRequest);
        List<PartSummary> parts = partListing.getParts();
        if (!record.getChunkNum().equals(parts.size())) {
            // 已上传分块数量与记录中的数量不对应，不能合并分块
            throw new RuntimeException("分片缺失，请重新上传");
        }
        FileLaunchRecord launchRecord = fileLaunchRecordRepository.getById(md5);
        FilePathMapping filePathMapping = FilePathMapping.buildMapping(launchRecord, record);
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest()
                .withUploadId(record.getUploadId())
                .withKey(record.getOssKey())
                .withBucketName(record.getBucketName())
                .withPartETags(parts.stream().map(partSummary -> new PartETag(partSummary.getPartNumber(), partSummary.getETag())).collect(Collectors.toList()));
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                amazonS3.completeMultipartUpload(completeMultipartUploadRequest);
                record.setFinished(true);
                fileChunkRecordRepository.save(record);
                filePathMappingRepository.save(filePathMapping);
                fileLaunchRecordRepository.deleteById(md5);
            }
        });
    }
}
