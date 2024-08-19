package com.visualization.utils;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Map;

public class SPELUtils {

    public static String parseExpression(String expression , Map<String,Object> params){
        ExpressionParser parser = new SpelExpressionParser();
        TemplateParserContext parserContext = new TemplateParserContext();
        return parser.parseExpression(expression, parserContext).getValue(params, String.class);
    }
}
