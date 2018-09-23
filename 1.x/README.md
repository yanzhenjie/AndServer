# 概述

![Logo](./images/logo.svg)

AndServer是一个Android平台的WebServer服务器和WebServer开发框架，作为Web服务器它支持了IP、端口的绑定，以及SSL自定义证书。作为Web开发框架它支持静态网站部署、动态Http接口、文件的上传下载。

作为一个平台框架，它的最核心的功能应该是对Web开发的支持，它的这些特性可能是你喜欢的：
* Request拦截器
* RequestHandler过滤器
* 全局异常处理者

比如Http缓存协议的实现就是基于RequestHandler过滤器的，里面还有一些好玩的小玩意。

如果你有什么疑问，或者你想为AndServer贡献一些代码:  
[https://github.com/yanzhenjie/AndServer](https://github.com/yanzhenjie/AndServer)

对于一些没有做过WebServer开发的开发者来说，一下子可能难易理解WebServer端某些的概念，推荐这些开发者朋友下载Demo源码查看或者通读整个文档，这可能对理解WebServer开发有所帮助。