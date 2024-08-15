package com.visualisation.constant;

public class ViewConstant {
    //jdbc、csv、excel、console
    public static final String VIEW_TYPE = "viewType";
    //导入engine后所使用的表名
    public static final String TABLE_NAME = "tableName";
    //操作脚本，csv视图时无意义，jdbc视图时存放取数sql
    public static final String SCRIPT = "script";
    // 文件路径，例如csv视图下，文件的存放路径
    public static final String FILE_PATH = "filePath";
    // 文件处理器。默认minio
    public static final String FILE_HANDLER_ID = "fileHandlerId";
    // 取数的配置参数
    public static final String PARAM = "param";
}
