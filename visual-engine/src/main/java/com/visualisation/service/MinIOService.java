package com.visualisation.service;

import com.visualisation.constant.MinIOConstant;
import com.visualisation.model.dag.DAGTemplate;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class MinIOService {

    @Resource(name = "visualMinioClient")
    private MinioClient minioClient;

    private String getTemplatePath(Long templateId) {
        return MinIOConstant.VISUAL_CONFIG_PREFIX + templateId + ".json";
    }

    @SneakyThrows
    public void uploadTemplateStr(DAGTemplate template) {
        byte[] bytes = template.getJson().getBytes();
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        String path = getTemplatePath(template.getTemplateId());
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(MinIOConstant.BUCKET_NAME)
                .object(path)
                .stream(is, is.available(), -1)
                .build();
        minioClient.putObject(args);
    }

    @SneakyThrows
    public void getTemplateStr(DAGTemplate template) {
        String path = getTemplatePath(template.getTemplateId());
        GetObjectArgs args = GetObjectArgs
                .builder()
                .bucket(MinIOConstant.BUCKET_NAME)
                .object(path)
                .build();
        GetObjectResponse is = minioClient.getObject(args);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        template.setJson(os.toString());
    }
}
