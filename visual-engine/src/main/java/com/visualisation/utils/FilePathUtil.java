package com.visualisation.utils;

import com.visualisation.constant.VisualConstant;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Map;

public class FilePathUtil {

    public static final String PUBLIC_SPACE = VisualConstant.DEFAULT_SPACE;
    public static final String SPACE_SHARE = "spaceShare/";


    private static final String VISUAL_TEMPLATE_PATTERN = "visual/#{[space]}/template/#{[templateId]}.json";

    private static final String VISUAL_STORAGE_PATTERN = "visual/#{[space]}/storage/#{[templateId]}/#{[instanceId]}/#{[filePath]}";

    private static final String VISUAL_SPACE_SHARE_STORAGE_PATTERN = "visual/#{[space]}/storage/#{[filePath]}";


    public static String getTemplatePath(Map<String, Object> params) {
        ExpressionParser parser = new SpelExpressionParser();
        TemplateParserContext parserContext = new TemplateParserContext();
        return parser.parseExpression(VISUAL_TEMPLATE_PATTERN, parserContext).getValue(params, String.class);
    }

    public static String getStoragePath(Map<String, Object> params) {
        ExpressionParser parser = new SpelExpressionParser();
        TemplateParserContext parserContext = new TemplateParserContext();
        String pattern = Boolean.TRUE.equals(params.get(SPACE_SHARE)) ? VISUAL_SPACE_SHARE_STORAGE_PATTERN : VISUAL_STORAGE_PATTERN;
        return parser.parseExpression(pattern, parserContext).getValue(params, String.class);
    }

}