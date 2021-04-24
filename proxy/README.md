# 反向代理服务器

反向代理服务器不是代理服务器，用 2 句通俗的话来讲：

1. 代理服务器是来代理客户端的；
2. 反向代理服务器是来代理服务端的；

那么什么时候能用到反向代理服务器呢？有时候处于安全考虑，开发者有时候会有这样的需求：

1. 隐藏真实的自己，让客户端访问 A，实际上 A 会把请求转发到 B，对于客户端来说，B 就是隐藏的
2. 分布式部署，客户端访问 A，A 根据访问量的，把请求分配到 B、C、D 等，而 B、C、D 的能力是完全一样的，这样就做到了压力分散

如下述代码所示，客户端请求`www.example.com`时会把请求转发到`http://192.167.1.11:8080`上，以此类推：

```java
public class ServerManager {

    private Server mServer;

    /**
     * Create server.
     */
    public ServerManager(Context context) {
        mServer = AndServer.proxyServer()
            .addProxy("www.example.com", "http://192.167.1.11:8080")
            .addProxy("example2.com", "https://192.167.1.12:9090")
            .addProxy("55.66.11.11", "http://www.google.com")
            .addProxy("192.168.1.11", "https://github.com:6666")
            .port(80)
            .timeout(10, TimeUnit.SECONDS)
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
        if (mServer.isRunning()) {
            mServer.shutdown();
        } else {
            Log.w("AndServer", "The server has not started yet.");
        }
    }
}
```
