package com.visualization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortParam {
    private String field;
    // 正数升序，其他降序
    private Integer direction;
}
