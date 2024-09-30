package com.visualization.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageParam {
    private Integer page = 0;
    private Integer size = 10;
    private Map<String, Object> conditions;
    private List<SortParam> sort;
}
