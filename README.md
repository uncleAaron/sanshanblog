# SanShanBlog

开始时一个博客系统，后来随着点赞功能 搜索 讨论模块的增加 并且提供相对完整的用户支持

所以目标是作一个类似于以博客为导向的社区 
> 演示 [地址][2]

> 这是后台代码 在下文中有技术选型的说明 前端代码在这里 [地址][1]
>
> docker编排文件 [地址][3]

#### 查询

> 您在查询归档 分类 标签 关键字时会响应很快 如果您采用es查询 速度会相对慢一点 但是由于ES的完整支持 您可以搜索到更多的信息

实现了一个次关键字的缓存层 获取博客具体内容时才会向Redis请求取数据
其他时刻通过维持Id唯一的 Date Title Tag 次关键字倒排索引
从而不使用高昂的数据库/Redis查询

> 在目前数量较小时是可以到达速度极快的 暂时试验性的使用 

在内部

- 分别维护6个Map集合  维持为3个倒排索引
- 定时刷新到磁盘上 启动时自动创建并且重新初始化相关数据 建议不要要求可用性 

#### 博客编辑支持俩种风格

> - 一种是Markdown风格的编辑 
> - 另外一种是富文本方式(富文本方式用的是百度的UEditor) 采用七牛云存储相关ueditor上传文件 对文件进行了时间校验以及引用表的维护 如果在12小时-36小时之间未上传引用文件的博客 将会在七牛云中删除

#### 点赞与踩

已对点赞功能进行完整的并发支持 并且在获取点赞相关信息时会进行请求校验  大部分情况下都将在缓存获取 从而避免数据库连接池过多导致崩溃的情况

#### 完整的用户支持

您可以在后台页面看到这些内容 
包括博客的修改删除以及不同编辑器类型的博客的汇总

您可以修改您的个人资料 目前包括 -头像-博客链接

修改之后将会在您的博客个人资料中显示 将会被其他人查看到 也可以被搜索到

您可以在忘记密码时通过注册邮箱进行重置

#### 其他功能

博客的选择更新-可以选择更新标题-标签-内容
还有一些常规功能包括 博客添加与删除  注册 登录 反馈 等

## 技术选型

1. 前端使用[angular4+bootstrap][1] 
2. 后台说明：
 - 后台日志存入MongoDB数据库(以及User，FeedBack信息)
 - 使用Redis作为缓存 mysql作为通用数据库
 - maven作为项目管理工具
 - 基本架构是SSM 不过因为因为Spring4的注解解决方案很成熟 所以基本除了maven的pom.xml之外的xml基本消失了
 > (已经改为Spring Boot) 但是传统的Spring FrameWork方式同样支持 只需调整servlet入口类即可 在Application.java中有说明
 - 采用ElasticSearch 作为搜索支持
 - 日志系统采用的是Log4j+slf4j 存储在mongoDB中
 - REST API 风格的URL 以及事务的完整支持
 - 对实时性不高的任务进行定时处理-目前采用阻塞队列进行添加 
 > 如果后期部署在多台机器上 将会换成MQ或者kafka这种中间件做通信


3. 目前使用的是JWT+Spring security的安全方案

4. 对Tomcat进行了GC参数调整 目前参数为下：
> JAVA_OPTS="-server -Xms700m -Xmx700m -XX:PermSize=64M  -XX:NewRatio=4 -XX:MaxPermSize=128m -Djava.awt.headless=true "

5. 整体项目使用[Docker][3]部署

##  领域模型设计 
主要为 DO DTO VO 三种实体对象
1. DO:数据库表模型,一张表对应一个DO
2. DTO:数据传输载体
3. VO 对应接口返回数据包装.简单情况下DTO可以直接作为VO使用

以及单独为Elastic设计的DO对象




在代码中采用lombok进行缩写代码

[1]: https://github.com/SanShanYouJiu/SanShanBlog-Web
[2]: https://sanshan.xyz/
[3]: https://github.com/SanShanYouJiu/sanshanblog-docker-file