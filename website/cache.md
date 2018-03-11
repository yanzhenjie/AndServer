# Http缓存

> 理解本章节内容，你可能需要参考[Filter/缓存支持](../filter/cache.md)。

网站开启缓存和`RequestHandler`开启缓存是一样的方法，需要实现`Last-Modified`接口或者`ETag`接口：
```java
public class MyWebsit implement Website, LastModified, ETag {
	public void handle(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		// ...
	}

	@Ovrride
	public long getLastModified(HttpRequest request) throws HttpException, IOException {
		// ...
	}

	@Ovrride
	public String getETag(HttpRequest request) throws HttpException, IOException {
		// ...
	}
}
```

这里具体的实现原理请参考[Filter/缓存支持](../filter/cache.md)。

## AndServer内部已经实现的网站
给AndServer的自带的网站缓存开启缓存，只需要在启动服务器的时候设置`Filter`为`HttpCacheFilter`。  

`HttpCacheFilter`内部已经实现了缓存过滤，所以只要使用`HttpCacheFilter`作为过滤器，可以为AndServer内部已经实现的网站开启缓存，也可以支持`RequestHandler`的缓存，但是需要`RequestHandler`实现`Last-Modified`接口或者`ETag`接口。

```
Server server = AndServer.serverBuilder()
	...
	.filter(new HttpCacheFilter())
	.build();

...
```