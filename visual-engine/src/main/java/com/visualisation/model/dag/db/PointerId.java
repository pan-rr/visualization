package com.visualisation.model.dag.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointerId implements Serializable {

    private Long instanceId;

    private Long taskId;
}
