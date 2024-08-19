package com.visualization.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.visualization.constant.OSSConstant;
import com.visualization.model.file.FileDetail;
import com.visualization.service.FileManageService;
import com.visualization.utils.FilePathUtil;
import io.minio.*;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Service
public class FileManageServiceImpl implements FileManageService {

    @Resource(name = "visualS3Client")
    private AmazonS3 amazonS3;

    @Resource(name = "visualMinioClient")
    private MinioClient minioClient;

    private String rewriteFolderPath(String dirPath) {
        return FilePathUtil.getPortalFilePath(dirPath);
    }


    private boolean checkPathExist(String path) {
        try {
            amazonS3.getObjectMetadata(OSSConstant.BUCKET_NAME, path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<FileDetail> listFolder(String folderPath) {
        folderPath = rewriteFolderPath(folderPath);
        if (!checkPathExist(folderPath)) {
            mkdir(folderPath, false);
        }
        ListObjectsV2Result result = amazonS3.listObjectsV2(OSSConstant.BUCKET_NAME, folderPath);
        List<S3ObjectSummary> summaries = result.getObjectSummaries();
        return FileDetail.covertFileDetail(folderPath, summaries);
    }

    @SneakyThrows
    @Override
    public void mkdir(String folderPath, boolean needRewrite) {
        if (needRewrite) {
            folderPath = rewriteFolderPath(folderPath);
        }
        if (!checkPathExist(folderPath)) {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(OSSConstant.BUCKET_NAME)
                    .object(folderPath)
                    .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                    .build();
            minioClient.putObject(args);
        }
    }

    @SneakyThrows
    @Override
    public void download(String path, HttpServletResponse response) {
        String key = FilePathUtil.getPortalFilePath(path);
        String[] split = key.split("/");
        String fileName = split[split.length - 1];
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(OSSConstant.BUCKET_NAME)
                .object(key)
                .build();
        GetObjectResponse is = minioClient.getObject(args);
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentLength(Integer.parseInt(Objects.requireNonNull(is.headers().get("Content-Length"))));
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        try (BufferedInputStream bis = new BufferedInputStream(is)) {
            byte[] buff = new byte[1024];
            ServletOutputStream os = response.getOutputStream();
            int i;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
            }
        }
    }
}
