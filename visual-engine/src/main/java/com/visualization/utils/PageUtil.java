package com.visualization.utils;

import com.visualization.model.PageParam;
import com.visualization.model.SortParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PageUtil {

    public static <T> Pair<Specification<T>, PageRequest> convertNormalRequest(PageParam param, Class<T> clazz, Sort defaultSort) {
        PageRequest request = PageRequest.of(param.getPage() - 1, param.getSize());
        List<SortParam> sort = param.getSort();
        if (CollectionUtils.isEmpty(sort) && Objects.nonNull(defaultSort)){
            request.withSort(defaultSort);
        } else{
            List<Sort.Order> collect = sort.stream().map(i -> i.getDirection() > 0 ? Sort.Order.asc(i.getField()) : Sort.Order.desc(i.getField())).collect(Collectors.toList());
            request.withSort(Sort.by(collect));
        }
        Map<String, Object> conditions = param.getConditions();
        if (!CollectionUtils.isEmpty(conditions)) {
            Specification<T> sp = (root, query, builder) -> {
                List<Predicate> list = new ArrayList<>();
                list.add(builder.equal(root.get("space"), conditions.get("space")));
                List<Integer> status = (List<Integer>) conditions.get("status");
                if (!CollectionUtils.isEmpty(status)) {
                    CriteriaBuilder.In<Object> statusIn = builder.in(root.get("status"));
                    for (Integer i : status) {
                        statusIn.value(i);
                    }
                    list.add(statusIn);
                }
                return builder.and(list.toArray(new Predicate[0]));
            };
            return Pair.of(sp, request);
        }
        return Pair.of(null, request);
    }
}
