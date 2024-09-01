## visual_task说明文件

目前visual task 只支持json配置，后续会增加表单配置

### 配置说明

如图所示即流程配置页的概念

![配置说明](https://gitee.com/pan-rr/visualization/raw/master/pic/配置说明.png)

### 配置JSON的说明

配置JSON和鸭子模型类似，只会在任务运行时才会检测配置的合法性，任务配置提交时不会进行校验。以下是JSON配置的选项说明。


#### viewType类型

> viewType:jdbc

支持jdbc的输入和输出类型，需在viewType指定

> viewType:csv

支持csv的输入和输出类型，需在viewType指定

> viewType:console

支持console的输出类型，需在viewType指定。一般用在本地环境

> viewType:excel

支持excel的输出类型，需在viewType指定。param.headers中指定表头，目前不支持复杂表头。备注：excel目前只支持输出类型

headers是一个二维数组，每一个数组对应每一列的表头，详情看easyexcel
```json
{"headers":[["人员属性","ID"],["人员属性","名称"],["人员属性","性别"],["籍贯信息","籍贯"]]}
```
#### 节点任务 json 属性介绍

```java
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
```

#### visual节点任务

多个输入、一个输出的任务

+ intput属性

> viewType: jdbc、csv

> tableName: 导入engine后所使用的表名，对应output.script里面使用的表名

> indexOn: 数据导入后是否建立索引，字段用逗号隔开，例如“no,sex“

> script: 取数脚本，csv视图时无意义，jdbc视图时存放取数sql

> param: viewType为jdbc时存放jdbc连接参数

+ output属性

> viewType: jdbc、console

> tableName: viewType为jdbc时，目标数据库的表名

> script: 取数SQL，使用h2数据库的语法

> param: jdbc视图时存放jdbc连接参数

+ 样例

```json
{
  "input": [
    {
      "viewType": "csv", //csv类型
      "filePath": "spaceShare/csv/student.csv", // 存储空间下的文件路径
      "tableName": "student" //进入engine使用的表名
    },
    {
      "viewType": "csv",
      "filePath": "spaceShare/csv/hobby.csv",
      "tableName": "hobby"
    }
  ],
  "output": {
    "viewType": "console",
    "script": "select a.*,b.type from student a inner join hobby b on a.no = b.no"
  }
}
```
#### sql节点任务

+ 属性

> sql: 用来做前置后置的建表、删表、替换表名等操作
> 剩下的就是jdbc连接参数

+ 样例

```json
{
  "sql": {
    "script": "create table t_test( id varchar(10) )",
    "url": "jdbc:mysql://localhost:3306/visualization?useUnicode=true&characterEncoding=utf8",
    "username": "test",
    "password": "test"
  }
}
```

#### Excel输出类型例子

```json
{
  "input": [
    {
      "viewType": "jdbc",
      "tableName": "person",
      "indexOn": "no,sex",
      "script": "select * from person limit 100",
      "param": {
        "url": "jdbc:mysql://localhost:3306/visualization?useUnicode=true&characterEncoding=utf8",
        "username": "test",
        "password": "test"
      }
    },
    {
      "viewType": "jdbc",
      "tableName": "prov",
      "indexOn": "no",
      "script": "select * from prov limit 100",
      "param": {
        "url": "jdbc:mysql://localhost:3306/visualization?useUnicode=true&characterEncoding=utf8",
        "username": "test",
        "password": "test"
      }
    }
  ],
  "output": {
    "viewType": "excel",
    "filePath": "csv/test_excel.xlsx",
    "param": {
      "headers": [
        [
          "人员属性",
          "ID"
        ],
        [
          "人员属性",
          "名称"
        ],
        [
          "人员属性",
          "性别"
        ],
        [
          "籍贯信息",
          "籍贯"
        ]
      ]
    },
    "script": "select a.*,b.ancetral_place from person a inner join prov b on a.no = b.no"
  }
}
```
