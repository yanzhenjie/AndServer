# 服务器

一个WebServer必须要涉及到的点是启动、停止、网络地址与端口监听、连接超时配置、SSL、状态监听、Socket的一些优化配置等，AndServer也提供了这些能力。

在AndServer中，只需要启动服务器即可，其它组件AndServer会自动加载。AndServer的服务器的启动是在子线程中进行的，因此服务器的启动成功与否，必须使用监听器。

下面是针对AndServer的服务器管理示例代码：
```java
public class ServerManager {

    private Server mServer;

    /**
     * Create server.
     */
    public ServerManager() {
        InetAddress inetAddress = ...;

        mServer = AndServer.serverBuilder()
            .inetAddress(inetAddress)
            .port(8080)
            .timeout(10, TimeUnit.SECONDS)
            .listener(new Server.ServerListener() {
                @Override
                public void onStarted() {
                    // TODO The server started successfully.
                }

                @Override
                public void onStopped() {
                    // TODO The server has stopped.
                }

                @Override
                public void onException(Exception e) {
                    // TODO An exception occurred while the server was starting.
                }
            })
            .build();
    }

    /**
     * Start server.
     */
    public void startServer() {
        if (mServer.isRunning()) {
            // TODO The server is already up.
        } else {
            mServer.startup();
        }
    }

    /**
     * Stop server.
     */
    public void stopServer() {
        if (mServer.isRunning) {
            mServer.shutdown();
        } else {
            Log.w("AndServer", "The server has not started yet.");
        }
    }
}
```

上文中有一段伪代码，作用是生成一个网络地址，一般我们都绑定本机在局域网中的IP地址。当然，如果你的设备有一个外网IP，你可以用外网IP来生成一个网络地址。
```java
InetAddress inetAddress = ...;
```

例如，使用某个IP生成网络地址：
```java
InetAddress inetAddress = InetAddress.getByName("192.168.1.11");
```

例如，使用某个域名生成网络地址：
```java
InetAddress inetAddress = InetAddress.getByName("www.yanzhenjie.com");
```