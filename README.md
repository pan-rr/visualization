### Visualization分布式数据分析平台

一个类似Kettle的数据分析平台，目标是数据分析，可承担一点ETL任务。

如果喜欢或者对您有帮助，请点个star吧！部分帅哥美女已经点了star，您还在等什么？：）

#### 架构图

![架构图](https://gitee.com/pan-rr/visualization/raw/master/pic/visual.svg)


#### 特性

+ 流程配置化，提供前端拖拉拽形式配置，数据流程使用json配置，少量代码即可完成取数逻辑的配置；
+ 基于Spring Cloud的分布式集群计算，外部依赖较少，容器集群部署相对方便；
+ DAG任务调度，节点任务由集群自动执行；
+ 支持多数据源，目前支持CSV、JDBC(MySQL驱动)，后续会增加其他数据源；
+ 文件分片上传下、下载；
+ 支持流程实例任务执行情况查询，以任务时间线形式展示；
+ 支持多租户权限管理，允许父子租户
+ 支持前端页面路由按权限生成；

#### 部分截图

![文件管理](https://gitee.com/pan-rr/visualization/raw/master/pic/文件管理.png)

![权限控制](https://gitee.com/pan-rr/visualization/raw/master/pic/权限控制.png)

![配置说明](https://gitee.com/pan-rr/visualization/raw/master/pic/配置说明.png)

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

JDK1.8+、MySQL5.7+、Redis6+、NodeJS、MinIO、InfluxDB

`备注：`作者开发环境是Mac，部分依赖可能和Windows有区别。作者开发过程就遇到Netty的依赖需要替换

#### 工程结构

+ visual-discovery：注册中心eureka
+ visual-engine：计算节点，分布式集群请启动多实例
+ visual-core：配置化的基础实现
+ visual-portal：前端项目
+ visual-gateway：网关服务
+ visual-auth：权限服务

#### 部署步骤

1. 启动MySQL、Redis、Minio、InfluxDB
2. 启动visual-discovery (SpringBoot的启动方式)
3. 启动visual-gateway (SpringBoot的启动方式)
4. 启动visual-auth (SpringBoot的启动方式)
5. 启动visual-engine (SpringBoot的启动方式)
6. 启动visual-portal (yarn test，其他环境指令目前就不配置了)

#### 任务流程

节点任务配置说明在visual_task.md

1. 在流程管理-流程定义新建流程模版。拖拽左侧“任务节点”到画布，点击节点进行配置。配置完成后点击发布流程
2. 在已定义流程列表点击运行实例
3. 等待任务完成，在“流程管理-流程实例列表-查看执行日志”里查看任务的执行情况


#### 流程模版文件上传下载

![文件管理](https://gitee.com/pan-rr/visualization/raw/master/pic/文件管理.png)

+ 在文件管理页找到对应路径上传下载。

+ 支持分片上传、秒传、下载

+ `注意:`流程模版共享的文件需在spaceShare文件夹下，否则无法识别。例如存储空间为public时，对应空间路径是"/public/spaceShare"

#### 后记

更多功能需日后完善。
尊重劳动成果，未经允许禁止商业闭源修改使用。