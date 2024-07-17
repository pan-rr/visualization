### Visualizetion联邦查询工具

#### 使用场景

1. 单机可承接的数据分析。本工具旨在单机可承受数据量的联邦查询，大数据分析请使用其他OLAP工具；
2. 有数据分析需求，但不想搭建大数据那套集群，可使用本工具；
3. 充当Mock接口，使用配置，快速提供接口数据；
4. 跨数据源连接操作，例如某张表在PolarDB，另一张表在MySQL的连接操作；
5. 部分数据库不支持多线程自查询，本工具使用多线程查询扇入数据，可能有一定加速效果；
6. 有配置需求的场景，例如定时生成分析报表的需求，可使用本工具，只需修改配置即可实现修改逻辑；
7. 数据大屏的后端，类似于datav、mapv的画布工具，使用该工具可以快速构建大屏后端；

#### 特点

1. 流程配置化，使用json配置数据流程，少量代码即可完成配置；
2. 基于H2内存模式进行查询；
3. 部分SQL会进行替换，导入h2的建表语句无需使用者提供
4. 支持多数据源，目前支持CSV、JDBC(需要自行添加驱动)，后续会增加其他数据源；

#### 概念

json配置分为多个扇入视图和一个扇出视图；扇入负责提取数据，扇出提供结果；

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
      "script" : "select no , name ,sex from student",
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

##### 数据流程

根据input加载数据进h2，根据output.script进行查询，结果根据output.viewType存放


#### 使用方式

1. spring容器获取com.visualisation.Commander的bean；
2. 调用Commander的方法，传入配置好的json即可；参考com.visualisation.H2Test.test调用；
3. 配置json样例在json文件夹下；
4. 数据样例在csv文件夹下，sql样例需自行把csv导入数据库

#### 性能测试

+ 测试参数

`硬件参数：M系列芯片`

`jvm参数：JDK1.8; -XX=+UseG1GC;-Xms=1g;-Xmx=8g`

`数据库：MySQL8.1`

>测试结果

图片无法加载的话，请去pic文件夹下看，单位均为ms

**两张数据量均为10w的表inner join后，结果写CSV耗时1.3s**

**两张数据量均为3w的表inner join后，结果插入MySQL，耗时4.5s**

如果目标数据库支持文件导入方式的话，建议导出csv后再把csv导入数据库，这样可以节省时间

![1](https://github.com/pan-rr/visualization/master/pic/3w*3w.png)

**person.csv导入MySQL**
person.csv 有百万行数据
![1](https://github.com/pan-rr/visualization/master/pic/csv导入100wpeople.png)

**prov.csv导入MySQL**
prov.csv 有百万行数据
![1](https://github.com/pan-rr/visualization/master/pic/csv导入100w.png)