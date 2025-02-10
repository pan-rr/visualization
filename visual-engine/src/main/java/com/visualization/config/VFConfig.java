package com.visualization.config;

import com.visualization.constant.OSSConstant;
import com.visualization.runtime.VFunctions;
import com.visualization.stage.VLogger;
import com.visualization.stage.VisualPathRewriter;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.FileInputStream;

@Slf4j
@Configuration
public class VFConfig {

    @Resource
    private VLogger vLogger;

    @Resource(name = "visualMinioClient")
    private MinioClient minioClient;

    @Resource
    private VisualPathRewriter visualPathRewriter;


    @PostConstruct
    public void init() {
        log();
        file();
        path();
    }

    private void log() {
        VFunctions.LOG.recoverFunction((vlog -> {
            vLogger.accept(vlog);
        }));
    }

    private void file() {
        VFunctions.FILE.recoverFunction((vFile -> {
            try {
                if (vFile.mode) {
                    try {
                        GetObjectArgs args = GetObjectArgs.builder().bucket(OSSConstant.BUCKET_NAME).object(vFile.path).build();
                        vFile.is = minioClient.getObject(args);
                    }catch (Exception e){
                        vFile.is = null;
                    }
                } else {
                    FileInputStream is = new FileInputStream(vFile.target);
                    PutObjectArgs args = PutObjectArgs.builder()
                            .bucket(OSSConstant.BUCKET_NAME)
                            .object(vFile.path)
                            .stream(is, is.available(), -1)
                            .build();
                    minioClient.putObject(args);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    private void path(){
        VFunctions.PATH.recoverFunction(visualPathRewriter::rewrite);
    }

}
