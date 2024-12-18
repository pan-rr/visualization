package com.visualization.model.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.visualization.constant.OSSConstant;
import com.visualization.utils.FilePathUtil;
import com.visualization.utils.SnowIdUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileChunkParam {

    /**
     * 文件唯一标识(MD5)
     */
    @NotBlank(message = "文件标识不能为空")
    private String md5;

    /**
     * 文件大小（byte）
     */
    @NotNull(message = "文件大小不能为空")
    private Long totalSize;

    /**
     * 分片大小（byte）
     */
    @NotNull(message = "分片大小不能为空")
    private Long chunkSize;

    /**
     * 文件名称
     */
    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    private String uploadId;

    private String ossKey;

    /**
     * 文件夹
     */
    @NotBlank(message = "文件夹不能为空")
    private String folder;

    private void initUploadRequest(AmazonS3 amazonS3) {
        String bucketName = OSSConstant.BUCKET_NAME;
        computeOSSKey();
        String contentType = MediaTypeFactory.getMediaType(ossKey).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        InitiateMultipartUploadResult initiateMultipartUploadResult = amazonS3
                .initiateMultipartUpload(new InitiateMultipartUploadRequest(bucketName, ossKey).withObjectMetadata(objectMetadata));
        this.uploadId = initiateMultipartUploadResult.getUploadId();

    }

    public String computeOSSKey() {
        if (StringUtils.isEmpty(ossKey)) {
            ossKey = FilePathUtil.getNormalPath(folder, fileName);
        }
        return ossKey;
    }

    public String computeUploadLockKey() {
        return "visual_upload_" + md5;
    }


    public FileChunkRecord buildDetailByParam(AmazonS3 amazonS3) {
        changePath();
        initUploadRequest(amazonS3);
        int chunkNum = (int) Math.ceil(totalSize * 1.0 / chunkSize);
        return FileChunkRecord.builder()
                .bucketName(OSSConstant.BUCKET_NAME)
                .chunkNum(chunkNum)
                .chunkSize(chunkSize)
                .totalSize(totalSize)
                .fileName(fileName)
                .md5(md5)
                .ossKey(ossKey)
                .uploadId(uploadId)
                .id(SnowIdUtil.nextId())
                .finished(false)
                .build();
    }

    private void changePath() {
        folder = "filePool/" + DateFormatUtils.format(new Date(), "yyyyMMdd/HH/");
        fileName = UUID.randomUUID().toString();
    }
}
