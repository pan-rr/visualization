package com.visualization.conf;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.visualization.constant.OSSConstant;
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

    @Bean(name = "visualS3Client")
    public AmazonS3 visualS3Client() {
        //设置连接时的参数
        ClientConfiguration config = new ClientConfiguration();
        //设置连接方式为HTTP，可选参数为HTTP和HTTPS
        config.setProtocol(Protocol.HTTP);
        //设置网络访问超时时间
        config.setConnectionTimeout(5000);
        config.setUseExpectContinue(true);
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        //设置Endpoint
        AwsClientBuilder.EndpointConfiguration end_point = new AwsClientBuilder.EndpointConfiguration(endpoint, Regions.CN_NORTH_1.name());
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withClientConfiguration(config)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(end_point)
                .withPathStyleAccessEnabled(true).build();
        // 检查bucket
        boolean flag = s3.doesBucketExistV2(OSSConstant.BUCKET_NAME);
        if (!Boolean.TRUE.equals(flag)) {
            s3.createBucket(OSSConstant.BUCKET_NAME);
        }
        return s3;
    }
}
