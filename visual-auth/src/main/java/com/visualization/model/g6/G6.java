package com.visualization.model.g6;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class G6 {

    public static final String ID = "id";

    private List<Map<String, Object>> nodes;

    private List<Edge> edges;

    public static G6 getInstance(String pre, List<Map<String, Object>> list) {
        G6 g6 = new G6();
        g6.edges = list.stream().map(m -> Edge.builder().source(pre).target(String.valueOf(m.get(ID))).build()).collect(Collectors.toList());
        g6.nodes = list;
        g6.nodes.forEach(m -> m.compute(ID, (k, v) -> String.valueOf(v)));
        return g6;
    }
}
