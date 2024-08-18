### Visualization分布式数据分析平台

如果喜欢或者对您有帮助，请点个star吧！部分帅哥美女已经点了star，您还在等什么？：）

#### 特性

+ 流程配置化，提供前端拖拉拽形式配置，数据流程使用json配置，少量代码即可完成取数逻辑的配置；
+ 基于Spring Cloud的分布式集群计算，外部依赖较少，容器集群部署相对方便；
+ DAG任务调度，节点任务由集群自动执行；
+ 支持多数据源，目前支持CSV、JDBC(MySQL驱动)，后续会增加其他数据源；
+ 文件分片上传下、下载；
+ 支持流程实例任务执行时间线查询

#### 部分截图

![文件管理](https://gitee.com/pan-rr/visualization/raw/master/pic/文件管理.png)

![时间线](https://gitee.com/pan-rr/visualization/raw/master/pic/时间线.png)

![流程列表](https://gitee.com/pan-rr/visualization/raw/master/pic/流程列表.png)

![流程配置](https://gitee.com/pan-rr/visualization/raw/master/pic/流程配置.png)

#### 使用场景

+ excel（异步）导出
  - 某些国企项目有excel导出需求，visual可以异步导出excel,减少代码开发量
+ 单节点可承接的数据分析。
  - 本工具目前不支持数据分片，大数据分析需自行在任务节点手动分片或使用其他OLAP工具或加大内存；
  - 一般个位数GB的JVM可以处理百万级数据
+ 有数据分析需求，但不想搭建大数据那套集群，可使用本工具；
+ 有配置需求的场景，例如定时生成分析报表的需求。
  - 这个目前需外部定时任务组件新建流程实例，目前市面上已经有很多定时任务组件可以搭配使用（后续考虑增加定时组件）；
+ 跨数据源连接操作，例如某张表在PolarDB、CSV，另一张表在MySQL的连接操作；
+ 取数逻辑频繁修改，例如某个指标。本工具只需修改json配置即可实现修改逻辑； 
+ 数据大屏的后端，类似于datav、mapv的画布工具，使用该工具可以快速构建大屏后端；


#### 部署前置条件

JDK1.8+、MySQL5.7+、Redis6+、NodeJS、MinIO

备注：作者开发环境是Mac

#### 工程结构

1. visual-discovery：注册中心eureka
2. visual-engine：计算节点，分布式集群请启动多实例
3. visual-core：配置化的基础实现
4. visual-portal：前端项目

#### 部署步骤

1. 启动MySQL、Redis、Minio
2. 启动visual-discovery
3. 启动visual-engine
4. 启动visual-portal

#### 样例流程

1. 在流程管理-流程定义新建流程模版。拖拽左侧“任务节点”到画布，点击节点进行配置。配置完成后点击发布流程
2. 在已定义流程列表点击运行实例
3. 等待任务完成

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
      "viewType": "jdbc",
      "tableName": "student",
      "script": "select no , name ,sex from student",
      "param": {
        "url": "jdbc:mysql://localhost:3306/visualization?useUnicode=true&characterEncoding=utf8",
        "username": "test",
        "password": "test"
      }
    },
    {
      "viewType": "csv",
      "filePath": "classpath:csv/hobby.csv",
      "tableName": "hobby",
      "param": {
        "charset": "utf-8",
        "writeColumnHeader": "true"
      }
    }
  ],
  "output": {
    "viewType": "jdbc",
    "tableName": "test_jdbc",
    "script": "select a.*,b.type from student a inner join hobby b on a.no = b.no",
    "param": {
      "url": "jdbc:mysql://localhost:3306/visualization?useUnicode=true&characterEncoding=utf8",
      "username": "test",
      "password": "test"
    }
  }
}
```
#### sql节点任务

+ 属性

> sql: 用来做前置后置的建表等操作
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

#### 流程模版文件上传下载

![文件管理](https://gitee.com/pan-rr/visualization/raw/master/pic/文件管理.png)

+ 在文件管理页找到对应路径上传下载。

+ `注意:`流程模版共享的文件需在spaceShare文件夹下，否则无法识别。例如存储空间为public时，对应空间路径是"/public/spaceShare"

#### 后记

更多功能需日后完善