package com.visualization.model.file;

import com.amazonaws.services.s3.model.PartSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_file_task", indexes = {
        @Index(name = "idx_md5", columnList = "md5", unique = true),
        @Index(name = "idx_upload_id", columnList = "uploadId", unique = true)
})
public class FileChunkRecord implements Serializable {
    @Id
    private Long id;
    //分片上传的uploadId
    private String uploadId;
    //文件唯一标识（md5）
    @Column(length = 500)
    private String md5;
    //文件名
    @Column(length = 500)
    private String fileName;
    //所属桶名
    private String bucketName;
    //文件的key
    @Column(length = 500)
    private String ossKey;
    //文件大小（byte）
    private Long totalSize;
    //每个分片大小（byte）
    private Long chunkSize;
    //分片数量
    private Integer chunkNum;

    private Boolean finished;

    /**
     * 已上传完的分片
     */
    @Transient
    private List<PartSummary> exitPartList;

}
