package com.visualization.stage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.CollectionUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VisualStageExpressionRewriter {

    public static String rewriteExpression(String expression, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        List<Tuple2<String, String>> list = new LinkedList<>();
        params.forEach((k, v) -> {
            String s = v.toString();
            if (StringUtils.startsWith(s, "#")) {
                list.add(Tuples.of(k, s));
            }else {
                map.put(k,v);
            }
        });
        ExpressionParser parser = new SpelExpressionParser();
        if (!CollectionUtils.isEmpty(list)) {
            StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            for (Method method : Visual.class.getDeclaredMethods()) {
                evaluationContext.registerFunction(method.getName(), method);
            }
            for (Tuple2<String, String> t : list) {
                map.put(t.getT1(), parser.parseExpression(t.getT2()).getValue(evaluationContext, String.class));
            }
        }
        TemplateParserContext parserContext = new TemplateParserContext();
        return parser.parseExpression(expression, parserContext).getValue(map, String.class);
    }
}
