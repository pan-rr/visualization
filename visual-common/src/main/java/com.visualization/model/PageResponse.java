package com.visualization.model;

import com.visualization.enums.ResponseEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse {

    private List<?> result;
    private Integer total;
    private Integer code;
    private String message;

    public static PageResponse success(List<?> result , int total){
        return PageResponse.builder()
                .result(result)
                .total(total)
                .code(ResponseEnum.SUCCESS.getCode())
                .message(ResponseEnum.SUCCESS.getMessage())
                .build();
    }

}
