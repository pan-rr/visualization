package com.visualization.model.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListPartsRequest;
import com.amazonaws.services.s3.model.PartListing;
import com.visualization.constant.OSSConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileChunkTask {
    /**
     * 是否完成上传（是否已经合并分片）
     */
    private boolean finished;

    /**
     * 文件地址
     */
    private String path;

    /**
     * 上传记录
     */
    private FileChunkRecord fileChunkRecord;


    public void checkFinished(AmazonS3 amazonS3) {
        if (!Boolean.TRUE.equals(finished)) {
            boolean doesObjectExist = amazonS3.doesObjectExist(OSSConstant.BUCKET_NAME, this.getPath());
            this.setFinished(doesObjectExist);
            if (!doesObjectExist) {
                // 未上传完，返回已上传的分片
                ListPartsRequest listPartsRequest = new ListPartsRequest(OSSConstant.BUCKET_NAME, fileChunkRecord.getOssKey(), fileChunkRecord.getUploadId());
                PartListing partListing = amazonS3.listParts(listPartsRequest);
                this.setFinished(false);
                this.fileChunkRecord.setExitPartList(partListing.getParts());
            }
        }
    }
}