Corgi
=======

**一个适用于中小互联网企业的迷你版本发布系统**  
更详细介绍请移步 贝聊 掘金专栏：https://juejin.im/post/5992b3386fb9a03c5c701fe6

# 前言
说到系统部署工具，大的互联网公司基本上是自己研发，一键自动部署到数百台到数千台服务器,比如twitter开源的Murder。
小的公司可能使用一些开源工具，比如Jenkins、Puppet、Ansible结合一些脚本进行。
开源的部署工具有Capistrano、瓦力等，在线部署的有dploy.io/。其中瓦力是国人开发的一个系统部署工具。
之前我们尝试使用walle来部署web服务和dubbo服务，在使用过程遇到一些问题：  

> 1.对运维规范支持不够  
> 2.不能在界面显示部署失败的原因  
> 3.读取tags列表非常慢，经常要多次刷新，导致部署过程很长  
> 4.如果项目下有N个模块，每次部署此项目下所有的模块，要重复编译此项目N次（Java项目），很耗时间  
> 5.业务系统多的时候项目列表眼花缭乱  
> 6.不能只部署一部分服务器  

所以针对这些问题，我们决定自研一个部署工具，经过接近一年的版本迭代和改进，该系统已经逐步完善，已经经过数万次的版本发布的考验，所以我们决定对外开源这个系统。

# 功能

- 能够查看java项目编译过程的完整日志  
- 快速读取svn/git 的所有分支，包括tags branches  
- 在一个项目部署多台服务器的场景，能够做到只需要编译一次  
- 支持服务器灰度发布
- 发布过程每个环节尽可能清晰，能知道发布进行到哪个环节，每个环节耗时
- 每个环节出现异常，能够捕捉到具体的异常信息，方便定位问题
- 监控系统的异常，比如进程是否正常启动
- 发布多台机器情况下，能够配置相关发布策略来匹配相关业务
- 支持多个环境  
- 支持在线修改JVM参数  
- 支持在线回滚、重启、停止

# 架构描述
搭建本项目需要2台服务器，一台用来部署发布系统的，一台用来编译所有的项目，不支持2台机器合并成一台机器。  
本发布系统设计思路：
> 1.从编译服务器打包好，然后将包回传到发布系统  
> 2.将打好的包从发布系统开始分发到各个业务服务器  
> 3.启动一个agent(web服务)，用于接收从编译服务器的编译过程日志以及业务系统发布过程产生的日志(不含业务日志)   

![架构图](http://7xrmyq.com1.z0.glb.clouddn.com/deploy2.png)

# 限制
**1.只支持resin服务器**  
由于贝聊一直采用resin web服务器进行部署，所以我们开发发布系统的时候，针对resin做了定制化的开发，包括配置resin watch-dog。resin的一个好处是一台机器部署多个resin web
项目，只需要安装一个resin服务器，而且重启的时候可以只针对一个项目进行重启，其他项目不受影响。  
**2.tomcat支持情况**  
如果项目采用的是spring boot内嵌tomcat的方式，我们支持，如果不是，暂时不支持，我们也计划在下一个版本的开发上支持。  
**3.其他语言**  
像前端、PHP、python等不需要经过编译，直接从git/svn下载然后打包项目发送到业务服务器的，我们都支持。

所以，总的来说，如果你的java项目是通过resin/内嵌tomcat 的方式进行部署，都能够被本系统完美支持。

# 相关文档
部署过程参考：[从零开始部署发布系统](/doc/从零开始部署发布系统.md)  
初始化文档：[系统初始化文档](/doc/init.md)  
功能介绍：[一个发布系统至少应该包含的功能](/doc/a-deployment-system.md)    
最佳实践：[贝聊在系统上的使用总结](/doc/best-practice-in-BL.md)  


# Author
贝聊研发部平台开发组：林毅、梁广龙、李东耀、李海文  
