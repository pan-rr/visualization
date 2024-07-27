package com.visualisation.conf;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "visual.storage.minio.enable", havingValue = "true")
public class MinIOConf {

    @Value("${visual.storage.minio.endpoint}")
    private String endpoint;

    @Value("${visual.storage.minio.accessKey}")
    private String accessKey;

    @Value("${visual.storage.minio.secretKey}")
    private String secretKey;


    @Bean(name = "visualMinioClient")
    public MinioClient visualMinioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
