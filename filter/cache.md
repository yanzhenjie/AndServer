# Http缓存

过滤器是非常有用的，比如Http缓存协议的实现就可以基于过滤器来做。  

在AndServer中，有一个`HttpCacheFilter`类，它实现了Http缓存协议部分，下面是它实现的几个关键头：

* Cache-Control
* Last-Modified
* If-Modified-Since
* If-Unmodified-Since
* ETag
* If-None-Match

## 用法
比如我们100%建议开启`Website`的缓存，它能提高用来提高服务器性能，尤其像`AndServer`这种运行在手机端的服务器。  

```
Server server = AndServer.serverBuilder()
	...
	.filter(new HttpCacheFilter())
	.build();

...
```

如上述代码即可打开AndServer自带的几个`Website`的缓存：
* AssetsWebsite
* StorageWebsite

## Http缓存协议
我们先来认识AndServer中的两个Interface：
1. LastModified，用来支持响应头的`Last-Modified`和请求头的`If-Modified-Since`
2. ETag，用来支持响应头的`ETag`和请求头的`If-None-Match`

> 关于上述两个Http头的概念，如果大家还有疑问请Google搜索相关资料了解。

每个接口的数据肯定是又**唯一**属性的，所以要保证每一个接口的数据都有一个唯一ID。`LastModified`接口是返回当前接口指向资源的修改时间的，`ETag`接口是返回当前接口指向资源的唯一值的。**唯一值**这个概念可能比较难理解，因为Http没有规定`ETag`的值是资源的什么属性，比如`HashCode`、`MD5`等等都可以作为一个唯一属性。  

### LastModified
此接口返回当前资源的修改时间（`Last-Modified`响应头），客户端检测到这个响应头时会缓存当前接口的响应数据。客户端再次请求当前资源时，会带上上次请求此接口返回的修改时间（`If-Modified-Since`相应头），服务器会用`If-Modified-Since`和服务器上资源的修改时间做对比。如果`If-Modified-Since`大于等于服务器资源的修改时间，那么返回304响应码，没有任何响应包体，客户端将会使用上次缓存的数据；如果`If-Modified-Since`小于服务器资源的修改时间，说明自上次请求之后，此资源已经被修改过了，也就是服务器的资源比客户端的资源新，服务器此时返回响应码200，返回新的资源作为响应包体。

### ETag
一般情况下我们只要开启`LastModified`即可，那么ETag又是做什么的呢？因为Unix时间戳是按照秒计算的，所以Http中的时间都是秒，或者说表示毫秒的三位都是0。由此可见，如果当前接口指向的资源在1秒内被修改，那么客户端将拿不到最新的资源。所以介于此，Http添加了ETag来兼容`Last-Modified`，Http协议固定`ETag`的值应该是粒度的值，也就是说它比`Last-Modified`更加细化。

## RequestHandler如何使用缓存
如果开发者设置了`HttpCacheFilter`为AndServer的过滤器，那么为`RequestHandler`开启缓存功能将会很简单，不用自己在`Response`中设置任何头。

```java
public class MyHandler implement RequestHandler, LastModified {

	public void handle(HttpRequest request, HttpResponse response, HttpContext context) ... {
		// 比如当前资源是个File。
		String path = HttpRequestParser.getRequestPath(request);
		... // 一些判断此资源是个文件。

		File file = new File(path);
		response.setStatusCode(200);
		response.setEntity(new FileEntity(file));
	}

	@Ovrride
	public long getLastModified(HttpRequest request) ... {
		// 比如当前资源是个File。
		String path = HttpRequestParser.getRequestPath(request);
		... // 一些判断此资源是个文件。

		File file = new File(path);
		return file.lastModified();
	}
}
```