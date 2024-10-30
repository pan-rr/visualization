package com.visualization.model.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_file_launch")
public class FileLaunchRecord {
    @Id
    private String md5;

    private String folder;

    private String fileName;

    private Date createTime;


    public static FileLaunchRecord getLaunchRecord(FileChunkParam param){
        return FileLaunchRecord.builder().md5(param.getMd5())
                .fileName(param.getFileName())
                .folder(param.getFolder())
                .createTime(new Date())
                .build();
    }
}
