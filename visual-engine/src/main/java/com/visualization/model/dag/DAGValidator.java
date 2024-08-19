package com.visualization.model.dag;

import org.springframework.util.CollectionUtils;

import java.util.*;

public class DAGValidator<ID> {

    // ID[0] : from ID[1] : to
    private List<ID[]> edges;

    private Map<ID, List<ID>> map;

    private Boolean flag;

    public DAGValidator(List<ID[]> edges) {
        this.edges = CollectionUtils.isEmpty(edges) ? new ArrayList<>() : edges;
    }

    public boolean validate() {
        if (flag != null) return flag;
        this.map = new HashMap<>();
        Set<ID> nodes = new HashSet<>();
        for (ID[] e : edges) {
            nodes.add(e[0]);
            nodes.add(e[1]);
            this.map.compute(e[0], (k, v) -> {
                if (v == null) v = new ArrayList<>();
                v.add(e[1]);
                return v;
            });
        }
        Set<ID> set = new HashSet<>();
        for (ID node : nodes) {
            function(node,set);
            set.remove(node);
        }
        flag = !Boolean.FALSE.equals(flag);
        return flag;
    }

    private void function(ID start , Set<ID> set) {
        if (flag != null) return;
        if(!set.add(start)){
            flag = false;
            return;
        }
        List<ID> ids = map.get(start);
        if (!CollectionUtils.isEmpty(ids)){
            for (ID id : ids) {
                function(id,set);
                set.remove(id);
            }
        }
    }

//    public static void main(String[] args) {
//        List<Integer[]> edges = new ArrayList<>();
//        edges.add(new Integer[]{4, 3});
//        edges.add(new Integer[]{4, 6});
//        edges.add(new Integer[]{6, 1});
//        edges.add(new Integer[]{6, 5});
//        edges.add(new Integer[]{3, 2});
//        edges.add(new Integer[]{3, 6});
//        edges.add(new Integer[]{1, 2});
//        DAGValidator<Integer> validator = new DAGValidator<>(edges);
//        boolean validate = validator.validate();
//        System.err.println(validate);
//    }
}
