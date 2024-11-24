### Visualization分布式数据分析平台

一个类似Kettle的数据分析平台，目标是数据分析，可承担一点ETL任务。
Visualization使用低代码(类工作流)方式编排DAG任务，集群自动执行相应的任务。
如果喜欢或者对您有帮助，请点个star吧！：）

[点击到达GitHub](https://github.com/pan-rr/visualization) | [点击到达码云](https://gitee.com/pan-rr/visualization)

#### 架构图

![架构图](https://gitee.com/pan-rr/visualization/raw/master/pic/visual.svg)

#### 特性

+ 流程配置化。提供前端拖拉拽形式配置流程，数据操作使用表单配置，少量代码即可完成相应的配置；
+ 基于Spring Cloud的分布式集群计算，外部组件依赖较少，容器集群部署相对方便；
+ 基于`SQL-SELECT`方式提取数据，上手成本相对低；
+ DAG任务按优先级调度，节点任务由集群自动执行；
+ 支持多数据源，目前支持CSV、JDBC(MySQL驱动、PostgreSQL)，后续会增加其他类型的数据源；
+ 文件分片上传下、下载；
+ 支持流程实例任务执行情况查询，以任务时间线形式展示；
+ 支持多租户权限管理，允许父子租户
+ 支持前端页面路由按权限动态生成，后端按资源鉴权；
+ 权限树使用G6绘制
+ 部分组件支持语法提示和高亮
+ 支持Visual、SQL、HTTP任务

#### 使用场景

+ excel（异步）导出
  - 某些国企项目有excel导出需求，visual可以异步导出excel,减少代码开发量
+ 单节点可承接的数据分析。
  - 本工具目前不支持数据分片，大数据分析需自行在任务节点手动分片或使用其他OLAP工具或加大内存；
  - 一般个位数GB的JVM可以处理百万级数据
+ 有数据分析需求，但不想搭建大数据那套集群，可使用本工具；
+ 有配置需求的场景，例如定时生成分析报表的需求。
  - 这个目前需外部定时任务组件新建流程实例，目前市面上已经有很多定时任务组件可以搭配使用（后续考虑增加定时组件）；
+ 异构数据源连接操作，例如某张表在PolarDB、CSV，另一张表在MySQL的连接操作；
+ 取数逻辑频繁修改，例如某个指标的获取方式。本工具只需修改配置即可实现修改逻辑；
+ 数据大屏的后端，类似于datav、mapv的画布工具，使用该工具可以快速构建大屏后端；

#### 部分截图

- 非开源版

![展示s](https://gitee.com/pan-rr/visualization/raw/master/pic/gif/pro.gif)

![画布s](https://gitee.com/pan-rr/visualization/raw/master/pic/s/画布.png)

![上下文配置s](https://gitee.com/pan-rr/visualization/raw/master/pic/s/上下文配置.png)

![上下文查看s](https://gitee.com/pan-rr/visualization/raw/master/pic/s/上下文查看.png)

+ 开源版

![展示ce](https://gitee.com/pan-rr/visualization/raw/master/pic/gif/ce.gif)

![画布](https://gitee.com/pan-rr/visualization/raw/master/pic/ce/画布.png)

![权限树](https://gitee.com/pan-rr/visualization/raw/master/pic/ce/权限树.png)

![语法提示](https://gitee.com/pan-rr/visualization/raw/master/pic/ce/语法提示.png)

![语法高亮](https://gitee.com/pan-rr/visualization/raw/master/pic/ce/语法高亮.png)

![表单配置1](https://gitee.com/pan-rr/visualization/raw/master/pic/ce/任务节点表单配置.png)

![流程配置](https://gitee.com/pan-rr/visualization/raw/master/pic/ce/流程配置.png)

![流程模版查看](https://gitee.com/pan-rr/visualization/raw/master/pic/ce/流程模版查看.png)

![表单配置2](https://gitee.com/pan-rr/visualization/raw/master/pic/ce/表单配置.png)

![文件管理](https://gitee.com/pan-rr/visualization/raw/master/pic/ce/文件管理.png)

![赋权管理](https://gitee.com/pan-rr/visualization/raw/master/pic/ce/赋权管理.png)

![赋权人员管理](https://gitee.com/pan-rr/visualization/raw/master/pic/ce/赋权人员管理.png)

![执行日志](https://gitee.com/pan-rr/visualization/raw/master/pic/ce/执行日志.png)

![数据源配置](https://gitee.com/pan-rr/visualization/raw/master/pic/ce/数据源配置.png)

![流程列表](https://gitee.com/pan-rr/visualization/raw/master/pic/ce/流程列表.png)

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

1. 启动MySQL、Redis、MinIO、InfluxDB
2. 启动visual-discovery (SpringBoot的启动方式)
3. 启动visual-gateway (SpringBoot的启动方式)
4. 启动visual-auth (SpringBoot的启动方式)
5. 启动visual-engine (SpringBoot的启动方式)
6. 启动visual-portal (yarn test，其他环境指令目前就不配置了)

#### 主要功能点说明

+ 流程模版配置、优先级(失败重试)配置;
+ 流程实例执行、查看、终止、日志；
+ 文件上传下载；
+ 权限资源配置、权限分配、赋权、网关鉴权、前端路由渲染、权限树；

`非开源版增加了上下文任务，任务可以获取上下文的变量，以及修改了部分样式`

#### 任务类型说明

+ Visual

  多输入项和一个输出项，输入项定义数据获取信息，输出项定义数据连接和输出信息；使用`SQL-SELECT`方式提取、转换数据，上手成本相对低；

+ SQL
  
  用于执行建表、删除表等操作；

+ Context(非开源版)
  
  用于上下文变量注入或覆盖，其他 任务可以使用表达式获取上下文变量；

+ HTTP
  
  主要用于外部接口调用。通知外部系统、获取token等用途；


#### 任务流程配置步骤说明

1. 在流程管理-流程定义新建流程模版。拖拽左侧“任务节点”到画布，点击节点进行配置。配置完成后点击发布流程
2. 在已定义流程列表点击运行实例
3. 等待任务完成，在“流程管理-流程实例列表-查看执行日志”里查看任务的执行情况

#### 租户说明

+ 注册时，可选择租户用户和普通用户；
+ 普通用户在赋权后才与对应的租户关联；
+ 右上角的“企业/组织/团队”选择当前进入的租户空间，所有用户都默认和public租户关联；

#### 存储空间说明

+ 存储空间可选个人和当前租户；
+ 所有配置项都存储在选择的存储空间；

#### 流程实例涉及文件说明

流程涉及到的文件有以下两种路径

+ 路径前缀是spaceShare，则文件在spaceShare文件夹下；

+ 其余情况的路径前缀是“流程模版Id/流程实例Id/文件配置的路径”。这么设置是为了规避多流程实例出现文件覆盖。

#### 流程模版文件上传下载

+ 在文件管理页找到对应路径上传下载。

+ 支持分片上传、秒传、下载

+ `注意:`流程模版共享的文件需在spaceShare文件夹下，否则无法识别。例如存储空间为public时，对应空间路径是"/public/spaceShare"

#### 后记

更多功能需日后完善。

尊重劳动成果，未经允许禁止商业闭源修改使用。

二次开发必须注明出处`https://github.com/pan-rr/visualization` 或者 `https://gitee.com/pan-rr/visualization`

禁止抹掉原作者信息

有什么问题或建议可提issue或者邮件vision_netmail@163.com联系