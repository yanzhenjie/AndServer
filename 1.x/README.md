# 概述

![Logo](./images/logo.svg)

AndServer 是一个 Android 平台的 WebServer 服务器和 WebServer 开发框架，作为 Web 服务器它支持了 IP、端口的绑定，以及 SSL 自定义证书。作为 Web 开发框架它支持静态网站部署、动态 HTTP 接口、文件的上传下载。

作为一个平台框架，它的最核心的功能应该是对 Web 开发的支持，它的这些特性可能是你喜欢的：

- Request 拦截器
- RequestHandler 过滤器
- 全局异常处理者

比如 HTTP 缓存协议的实现就是基于 RequestHandler 过滤器的，里面还有一些好玩的小玩意。

如果你有什么疑问，或者你想为 AndServer 贡献一些代码:  
[https://github.com/yanzhenjie/AndServer](https://github.com/yanzhenjie/AndServer)

对于一些没有做过 WebServer 开发的开发者来说，一下子可能难易理解 WebServer 端某些的概念，推荐这些开发者朋友下载 Demo 源码查看或者通读整个文档，这可能对理解 WebServer 开发有所帮助。
