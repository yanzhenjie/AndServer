# 基本Api

对于一些基本概念的说明和演示一些基本的Api使用。

## 站点概念
从AndServer1.1.0版本开始，有了站点这个概念。每一个Android机器看作一台服务器，一台服务器可以搭建多个站点，也就是下文中要提到的`Server`。

```java
Server mServer;

...

mServer = AndServer.serverBuilder()
	// 服务器设置部分：
	.sslContext()                   // 设置SSLConext，加载SSL证书。
	.sslSocketInitializer()         // 对SSLServerSocket进行一些初始化设置。
	.inetAddress()                  // 设置服务器要监听的网络地址。
	.port()                         // 设置服务器要监听的端口。
	.timeout()                      // Socket的超时时间。
	.listener()                     // 服务器监听。
	// Web框架设置部分：
	.interceptor()                  // Request/Response对的拦截器。
	.website()                      // 设置网站。
	.registerHandler()              // 注册一个Http Api路径和对应的处理器。
	.filter()                       // RequestHandler的过滤器。
	.exceptionResolver()            // 异常解决者。
	.build();

...

mServer.start();                                // 启动服务器。
mServer.shutdown();                             // 关闭服务器。
boolean isRunnging = mServer.isRunning();       // 服务器是否在运行。
InetAddress address = mServer.getInetAddress(); // 拿到服务器监听的网络地址。
```

上述除了端口必须要指定外，几乎所有的Api都不是必须的，开发者选择自己需要的进行设置即可。一般情况下，我们至少会注册一个Http接口的`路径和对应的处理器`。  

如果不绑定IP地址，从`mServer.getInetAddress()`拿到的IP地址可能是`0.0.0.0`，这种情况下，服务器默认监听了本机当前局域网IP，例如`192.168.1.11`。

## 示例
下面是一个简单的例子：
```java
InetAddress address = NetUtils.getLocalIPAddress();

Server server = AndServer.serverBuilder()
	.inetAddress()
	.port(8080)
	.registerHandler("/register", new RegisterRequestHandler())
	.registerHandler("/login", new LoginRequestHandler())
	.build();

server.start();
```

假设我们本机的IP地址是`192.168.1.11`，那么上面的代码结果是在`192.168.1.11`网关下监听了`8080`端口，服务器上将会生成两个Http接口：

* `http://192.168.1.11:8080/register`，请求会分发到`RegisterRequestHandler`类。
* `http://192.168.1.11:8080/login`，请求会分发到`LoginRequestHandler`类。

> **注意**：AndServer从1.1.0版本开始，注册`path`和对应的`RequestHandler`时，`path`必须严格遵守`path`的书写规范，例如：`user`、`user/admin`都是违法的，`/user`、`/user/admin`都是合法的。