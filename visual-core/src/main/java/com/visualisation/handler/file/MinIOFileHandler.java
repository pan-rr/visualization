package com.visualisation.handler.file;

import com.visualisation.constant.MinIOConstant;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Component(value = "minioFileHandler")
@ConditionalOnProperty(name = "visual.storage.minio.enable", havingValue = "true")
public class MinIOFileHandler implements FileHandler {

    @Resource(name = "visualMinioClient")
    private MinioClient minioClient;

    private static final String VISUAL_STORAGE_PREFIX = MinIOConstant.VISUAL_STORAGE_PREFIX;

    private static final String BUCKET_NAME = MinIOConstant.BUCKET_NAME;

    @Override
    public int getPriority() {
        return 1;
    }

    private String getObjectName(String path) {
        return VISUAL_STORAGE_PREFIX + path;
    }

    @Override
    public File download(String sourcePath, Map<?, ?> uploadParam) {
        GetObjectArgs args = GetObjectArgs.builder().bucket(BUCKET_NAME).object(getObjectName(sourcePath)).build();
        File file;
        try {
            GetObjectResponse is = minioClient.getObject(args);
            String filePath = generateFilePath(sourcePath);
            file = new File(filePath);
            makeSureDirectoryExist(file);
            byte[] buffer = new byte[1024];
            int len;
            BufferedOutputStream os = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            while ((len = is.read(buffer)) > -1) {
                os.write(buffer, 0, len);
            }
            os.close();
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | XmlParserException | ServerException |
                 NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    @Override
    public void upload(File file, String targetPath, Map<?, ?> uploadParam) {
        try {
            FileInputStream is = new FileInputStream(file);
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(getObjectName(targetPath))
                    .stream(is, is.available(), -1)
                    .build();
            minioClient.putObject(args);
            Files.deleteIfExists(file.toPath());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | XmlParserException | ServerException |
                 NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
