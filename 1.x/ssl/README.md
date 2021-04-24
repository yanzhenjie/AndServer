# HTTPs

部分开发者对产品的安全性要求比较高，使用 Https 进行通信这将是一个不错的选择。如[基本 Api](../base/api.md)章节所示，开启 Https 我们仅仅需要设置`SSLConext`即可，`SSLContext`对象可以进行证书加载等操作，当然我们也可以对`SSLServerSocket`进行一些设置。

```java
Server server = AndServer.serverBuilder()
	.sslContext()                   // 设置SSLConext，加载SSL证书。
	.sslSocketInitializer()         // 对SSLServerSocket进行一些初始化设置。
	...
	.build();
```

如果要使用 Https，一般开发者只需要设置`SSLContext`即可，少数开发者在了解`SSLServerSocket`的情况下可以进行一个个性化要求的设置。

```java
public class SSLInitializer implements SSLSocketInitializer {
    @Override
    public void onCreated(SSLServerSocket socket) throws SSLException {
		socket.setEnabledCipherSuites();
        socket.setNeedClientAuth();
        ...
    }
}
```

> 关于 SSLSocket 的文档在网络上有很多博文在讲解，本文不再赘述。
