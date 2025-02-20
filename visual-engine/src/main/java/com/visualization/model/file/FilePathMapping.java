package com.visualization.model.file;

import com.visualization.utils.FilePathUtil;
import com.visualization.utils.ShortLinkUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_file_path_mapping")
public class FilePathMapping {

    @Id
    private Integer filePathHash;

    @Column(length = 500)
    private String sourcePath;

    @Column(length = 200)
    private String fileName;

    @Column(name = "file_prefix_hash")
    private Integer filePrefixHash;

    public static FilePathMapping buildExampleByFolder(String folder) {
        if (folder.endsWith("/")) {
            folder = folder.substring(0, folder.length() - 1);
        }
        FilePathMapping mapping = new FilePathMapping();
        mapping.filePrefixHash = ShortLinkUtil.zipToInt(folder);
        return mapping;
    }

    public static FileDetail covert(FilePathMapping mapping) {
        return FileDetail.builder()
                .isFolder(false)
                .name(mapping.fileName)
                .build();
    }

    public static List<FileDetail> covert(List<FilePathMapping> list) {
        return list.stream().map(FilePathMapping::covert).collect(Collectors.toList());
    }

    public static FilePathMapping buildMapping(FileChunkParam param, FileChunkRecord record) {
        String targetOSSKey = param.computeOSSKey();
        File file = new File(targetOSSKey);
        String parent = file.getParent();
        return FilePathMapping.builder()
                .fileName(param.getFileName())
                .sourcePath(record.getOssKey())
                .filePathHash(ShortLinkUtil.zipToInt(targetOSSKey))
                .filePrefixHash(ShortLinkUtil.zipToInt(parent))
                .build();
    }

    public static FilePathMapping buildMapping(FileLaunchRecord launchRecord, FileChunkRecord record) {
        String targetOSSKey = FilePathUtil.getNormalPath(launchRecord.getFolder(), launchRecord.getFileName());
        File file = new File(targetOSSKey);
        String parent = file.getParent();
        return FilePathMapping.builder()
                .fileName(launchRecord.getFileName())
                .sourcePath(record.getOssKey())
                .filePathHash(ShortLinkUtil.zipToInt(targetOSSKey))
                .filePrefixHash(ShortLinkUtil.zipToInt(parent))
                .build();
    }

}
