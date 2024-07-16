package com.visualisation.model.dag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EdgeId implements Serializable {
    private Long instanceId;

    private Long fromTaskId;

    private Long toTaskId;
}
