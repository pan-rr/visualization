package com.visualization.utils;

import com.visualization.enums.ConditionType;
import com.visualization.model.Condition;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpecificationUtil {

    public static <T> Specification<T> build(Map<String, Condition> conditions, Class<T> clazz) {
        return (root, query, builder) -> {
            List<Predicate> list = new ArrayList<>();
            conditions.forEach((f, condition) -> {
                ConditionType type = ConditionType.getByName(condition.getConditionType());
                switch (type) {
                    case eq:
                        list.add(builder.equal(root.get(f), condition.getParam()[0]));
                        break;
                    case in:
                        CriteriaBuilder.In<Object> in = builder.in(root.get(f));
                        Object[] arr = condition.getParam();
                        for (Object o : arr) {
                            in.value(o);
                        }
                        list.add(in);
                        break;
                }
            });
            return query.where(list.toArray(new Predicate[0])).getRestriction();
        };
    }



}
