package com.visualisation.service;

import com.visualisation.constant.OSSConstant;
import com.visualisation.model.dag.DAGTemplate;
import com.visualisation.utils.FilePathUtil;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class MinIOService {

    @Resource(name = "visualMinioClient")
    private MinioClient minioClient;

    private String getTemplatePath(DAGTemplate template) {
        Map<String, Object> map = new HashMap<>();
        map.put("templateId", template.getTemplateId());
        map.put("space", template.getSpace());
        return FilePathUtil.getTemplatePath(map);
    }

    @SneakyThrows
    public void uploadTemplateStr(DAGTemplate template) {
        byte[] bytes = template.getJson().getBytes();
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        String path = getTemplatePath(template);
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(OSSConstant.BUCKET_NAME)
                .object(path)
                .stream(is, is.available(), -1)
                .build();
        minioClient.putObject(args);
    }

    @SneakyThrows
    public void getTemplateStr(DAGTemplate template) {
        String path = getTemplatePath(template);
        GetObjectArgs args = GetObjectArgs
                .builder()
                .bucket(OSSConstant.BUCKET_NAME)
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
