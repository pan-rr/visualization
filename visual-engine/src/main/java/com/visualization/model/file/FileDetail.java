package com.visualization.model.file;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.visualization.utils.FilePathUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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


    public static List<FileDetail> covertFileDetail(String folder, List<S3ObjectSummary> list) {
        if (CollectionUtils.isEmpty(list)) return new ArrayList<>();
        return list.stream()
                .map(i -> i.getKey().replace(folder, ""))
                .map(i -> {
                    if (StringUtils.isEmpty(i)) return null;
                    String[] split = i.split("/");
                    if (split.length == 1 && !i.endsWith("/")) {
                        return FileDetail.builder().name(i).isFolder(false).build();
                    }
                    return FileDetail.builder().name(split[0]).isFolder(true).build();
                }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    }
}
