package com.visualization.service;

import com.google.common.io.Files;
import com.visualization.constant.OSSConstant;
import com.visualization.model.dag.db.DAGTemplate;
import com.visualization.utils.FilePathUtil;
import io.minio.*;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class MinIOService {

    @Resource(name = "visualMinioClient")
    private MinioClient minioClient;

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    @SneakyThrows
    public Map<String,String> getTemplateStrBySpace(String space , Set<String> includeIds) {
        String pattern = "visual/{0}/template/";
        String prefix = MessageFormat.format(pattern, space);
        ListObjectsArgs args = ListObjectsArgs.builder()
                .bucket(OSSConstant.BUCKET_NAME)
                .prefix(prefix)
                .build();
        Iterable<Result<Item>> results = minioClient.listObjects(args);
        Map<String,String> map = new HashMap<>();
        for (Result<Item> result : results) {
            Item item = result.get();
            if (!item.isDir()) {
                String id = Files.getNameWithoutExtension(item.objectName());
                if (!includeIds.contains(id))continue;
                String templateStr = getTemplateStr(id, item.objectName());
                map.put(id,templateStr);
            }
        }
        return map;
    }

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

    private String key(String templateId) {
        return "visual:template:" + templateId;
    }

    @SneakyThrows
    public String getTemplateStr(String templateId, String path) {
        BoundValueOperations<String, String> ops = redisTemplate.boundValueOps(key(templateId));
        String string = ops.get();
        if (StringUtils.isNotBlank(string)) {
            return string;
        }
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
        String res = os.toString();
        os.close();
        ops.setIfAbsent(res, 1, TimeUnit.HOURS);
        return res;
    }

    @SneakyThrows
    public void getTemplateStr(DAGTemplate template) {
        String path = getTemplatePath(template);
        template.setJson(getTemplateStr(template.getTemplateId().toString(), path));
    }
}
