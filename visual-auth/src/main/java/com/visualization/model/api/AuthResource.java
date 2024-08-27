package com.visualization.model.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResource {

    private String authKey;

    private Boolean permitAll;

    private List<String> resourceList;

}
