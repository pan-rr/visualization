package com.visualisation.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.visualisation.constant.OSSConstant;
import com.visualisation.model.file.FileDetail;
import com.visualisation.service.FileManageService;
import com.visualisation.utils.FilePathUtil;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class FileManageServiceImpl implements FileManageService {

    @Resource(name = "visualS3Client")
    private AmazonS3 amazonS3;

    @Resource(name = "visualMinioClient")
    private MinioClient minioClient;

    private String rewriteFolderPath(String dirPath){
        return FilePathUtil.getPortalFilePath(dirPath);
    }


    private boolean checkPathExist(String path){
        try {
            amazonS3.getObjectMetadata(OSSConstant.BUCKET_NAME, path);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public List<FileDetail> listFolder(String folderPath) {
        folderPath = rewriteFolderPath(folderPath);
        if (!checkPathExist(folderPath)) {
            mkdir(folderPath,false);
        }
        ListObjectsV2Result result = amazonS3.listObjectsV2(OSSConstant.BUCKET_NAME, folderPath);
        List<S3ObjectSummary> summaries = result.getObjectSummaries();
        return FileDetail.covertFileDetail(folderPath,summaries);
    }

    @SneakyThrows
    @Override
    public void mkdir(String folderPath, boolean needRewrite) {
        if (needRewrite){
           folderPath = rewriteFolderPath(folderPath);
        }
        if (!checkPathExist(folderPath)){
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(OSSConstant.BUCKET_NAME)
                    .object(folderPath)
                    .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                    .build();
            minioClient.putObject(args);
        }
    }

    @Override
    public void download(String path, HttpServletResponse response) {

    }
}
