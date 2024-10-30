package com.visualization.utils;

import com.visualization.constant.VisualConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FilePathUtil {

    public static final String PUBLIC_SPACE = VisualConstant.DEFAULT_SPACE;
    public static final String SPACE_SHARE = "spaceShare/";

    private static final String VISUAL_TEMPLATE_PATTERN = "visual/#{[space]}/template/#{[templateId]}.json";

    private static final String VISUAL_STORAGE_PATTERN = "visual/#{[space]}/storage/#{[templateId]}/#{[instanceId]}/#{[filePath]}";

    private static final String VISUAL_SPACE_SHARE_STORAGE_PATTERN = "visual/#{[space]}/#{[filePath]}";

    private static final String VISUAL_NORMAL_PATTERN = "visual/#{[folder]}#{[fileName]}";

    public static String getNormalPath(String folder, String fileName) {
        Map<String, Object> map = new HashMap<>();
        map.put("folder", folder);
        map.put("fileName", fileName);
        return getNormalPath(map);
    }

    public static String getNormalPath(Map<String, Object> params) {
        ExpressionParser parser = new SpelExpressionParser();
        TemplateParserContext parserContext = new TemplateParserContext();
        return parser.parseExpression(VISUAL_NORMAL_PATTERN, parserContext).getValue(params, String.class);
    }

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

    public static boolean isFolderPath(String path) {
        return !StringUtils.isEmpty(path) && path.endsWith("/");
    }

    public static String getPortalFilePath(String suffix) {
        suffix = "visual/" + suffix;
        return suffix.replaceAll("//", "/");
    }

    public static String getFileName(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }

}
