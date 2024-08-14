package com.visualisation.model.file;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.visualisation.utils.FilePathUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDetail {

    private String name;

    private Boolean isFolder;


    public static FileDetail covertFileDetail(S3ObjectSummary summary) {
        String key = summary.getKey();
        String[] split = key.split("/");
        return FileDetail.builder()
                .name(split[split.length - 1])
                .isFolder(FilePathUtil.isFolderPath(key))
                .build();
    }

    public static List<FileDetail> covertFileDetail(String folder,List<S3ObjectSummary> list) {
        if (CollectionUtils.isEmpty(list)) return new ArrayList<>();
        return list.stream().filter(i->!folder.equals(i.getKey())).map(FileDetail::covertFileDetail).collect(Collectors.toList());
    }
}
