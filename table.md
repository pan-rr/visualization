## Visual Table简介

Visual Table，下文简称VT。

> VT和Flink Table类似，采用SQL方式定义ETL程序，SQL语句基于SQL-2003解析。
> 
> VT目前支持create table和insert两种语句；
> 
> VT是基于内存的计算工具，部分算子会下推给对应数据库执行，为避免`OOM`，请留意JVM内存容量是否足以支撑数据量；
> 
> VT会尝试翻译`SQL方言`并执行；
> 
> VT支持`跨数据源`查询，目前支持的数据源类型有csv、jdbc、memory。jdbc请自行添加对应数据库驱动，目前有MySQL、PostgreSQL；

## Visual Table SQL

`注意`SQL语句之间需使用半角分号(;)隔开，否则解析会报错！

#### DDL

> DDL模版

```shell
create table <table name> (
  <column declaration>
) select `<properties json>`
  
```

> properties json说明

    visual-type:  
        csv(csv文本，文件需带文本头)
        jdbc(支持jdbc的数据库)
        memory(内存临时表，可用于插入、获取临时变量)
        console(本地测试用)
    url:
        jdbc url
    username:
        jdbc username
    password:
        jdbc password
    time-pattern:
        csv读取日期时转换格式，例：yyyy-MM-dd
    path:
        csv文件路径。
            流程涉及到的文件有以下两种路径：
                1.路径前缀是spaceShare，则文件在spaceShare文件夹下；
                2.其余情况的路径前缀是“流程模版Id/流程实例Id/文件配置的路径”。这么设置是为了规避多流程实例出现文件覆盖。


> DDL例子
```shell
CREATE TABLE t_test_1 (id INT,name VARCHAR(100)) 
as select '{   
    visual-type : "jdbc",
    url : "jdbc:mysql://localhost:3306/test",
    username : "str",   
    password : "str"
}'
```

#### Insert

> Insert 模版

```shell

insert into <table name> select * from <table name>

insert into <table name> values (<column values>)
  
```

> Insert 例子

```shell

insert into t_test(id,name) values (1221,'name')

insert into t_test_1 select * from t_test

```

#### 完整示例

```shell
CREATE TABLE t_test_1 (id INT,name VARCHAR(100)) 
as select '{   
    visual-type : "jdbc",
    url : "jdbc:mysql://localhost:3306/test",
    username : "str",   
    password : "str"
}';
CREATE TABLE t_test (id INT,name VARCHAR(100)) 
as select '{   
    visual-type : "memory"
}';
insert into t_test(id,name) values (1221,'name');
insert into t_test(id,name) values (11,'ss2');
insert into t_test_1 select * from t_test;
```
