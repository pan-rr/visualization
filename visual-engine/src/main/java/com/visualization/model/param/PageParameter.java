package com.visualization.model.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageParameter<T> {
    private Integer page = 0;
    private Integer size = 10;
    private T param;
}
