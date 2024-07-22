### Visualization分布式数据分析平台

#### 特性

1. 流程配置化，提供前端拖拉拽形式配置，数据流程使用json配置，少量代码即可完成取数逻辑的配置；
2. 基于Spring Cloud的分布式集群计算，外部依赖较少；
3. 基于MySQL实现DAG任务调度；
4. 支持多数据源，目前支持CSV、MySQL，后续会增加其他数据源；

#### 部分截图

![1](https://github.com/pan-rr/visualization/master/pic/流程列表.png)

![1](https://github.com/pan-rr/visualization/master/pic/流程配置.png)

#### 使用场景

1. 单机可承接的数据分析。本工具不支持数据分片，旨在单机可承受数据量的联邦查询，大数据分析请自行在配置进行分片或使用其他OLAP工具或加大内存；
2. 有数据分析需求，但不想搭建大数据那套集群，可使用本工具；
3. 充当Mock接口，使用配置，快速提供接口数据；
4. 跨数据源连接操作，例如某张表在PolarDB，另一张表在MySQL的连接操作；
5. 有配置需求的场景，例如定时生成分析报表的需求；
6. 取数逻辑频繁修改，例如测试环境。本工具只需修改json配置即可实现修改逻辑；
7. 数据大屏的后端，类似于datav、mapv的画布工具，使用该工具可以快速构建大屏后端；

#### 部署前置条件

JDK1.8+、MySQL5.7+、Redis6+、Vue3

#### 工程结构

1. visual-discover：注册中心eureka
2. visual-engine：计算节点。分布式集群请启动多实例。
3. visual-core：配置化的基础实现

#### 部署步骤

1. 启动MySQL、Redis
2. 启动visual-discover
3. 启动visual-engine
4. 启动visual-portal

#### 样例流程

1. 在流程管理-流程定义新建流程模版。拖拽左侧“任务节点”到画布，点击节点进行配置。配置完成后点击发布流程
2. 在已定义流程列表点击运行实例
3. 等待任务完成

#### 节点任务 json 属性介绍

+ intput属性

> viewType: jdbc、csv

> tableName: 导入h2后所使用的表名，对应output.script里面使用的表名

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
      "filePath": "csv/hobby.csv",
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
