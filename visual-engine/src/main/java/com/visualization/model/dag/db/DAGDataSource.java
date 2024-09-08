package com.visualization.model.dag.db;

import com.google.gson.Gson;
import com.visualization.model.portal.PortalDataSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_dag_datasource", indexes = {
        @Index(name = "idx_hash", columnList = "hash")
})
public class DAGDataSource {
    @Id
    private Long dataSourceId;
    private String space;
    private String name;
    private String config;
    private Integer hash;


    public PortalDataSource convert() {
        Gson gson = new Gson();
        Map map = gson.fromJson(config, Map.class);
        return PortalDataSource.builder()
                .config(map)
                .name(name)
                .space(space)
                .id(dataSourceId.toString())
                .build();
    }

    public static List<PortalDataSource> convert(List<DAGDataSource> list) {
        return list.stream().map(DAGDataSource::convert).collect(Collectors.toList());
    }

    public static Map<Long, Map<?, ?>> convertMap(List<DAGDataSource> list) {
        Map<Long, Map<?, ?>> res = new HashMap<>();
        list.forEach(i -> {
            String str = i.getConfig();
            Gson gson = new Gson();
            Map map = gson.fromJson(str, Map.class);
            res.put(i.getDataSourceId(), map);
        });
        return res;
    }
}
