package com.visualization.model.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Option {

    private String label;

    private String value;

    public static ResourceOption convertResourceOption(Option option){
        return ResourceOption.builder().key(option.value).label(option.label).build();
    }

    public static List<ResourceOption> convertResourceOption(List<Option> list){
        return list.stream().map(Option::convertResourceOption).collect(Collectors.toList());
    }
}
