package com.visualization.utils;

import com.visualization.model.Condition;
import com.visualization.model.PageParam;
import com.visualization.model.SortParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

public class PageUtil {

    public static <T> Pair<Specification<T>, PageRequest> buidlRequestPair(PageParam param, Class<T> clazz) {
        PageRequest request;
        List<SortParam> sort = param.getSort();
        if (!CollectionUtils.isEmpty(sort)) {
            Sort orders = sort.stream().map(i -> {
                Sort by = Sort.by(i.getField());
                by = i.getDirection() > 0 ? by.ascending() : by.descending();
                return by;
            }).reduce(Sort::and).get();
            request = PageRequest.of(param.getPage() - 1, param.getSize(), orders);
        } else {
            request = PageRequest.of(param.getPage() - 1, param.getSize());
        }
        Map<String, Condition> conditions = param.getConditions();
        if (!CollectionUtils.isEmpty(conditions)) {
            Specification<T> sp = SpecificationUtil.build(conditions, clazz);
            return Pair.of(sp, request);
        }
        return Pair.of(null, request);
    }
}
