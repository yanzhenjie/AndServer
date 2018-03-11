# 网站

在[基本Api](../base/README.md)章节中我们演示了如何注册网站，在AndServer中如何注册一个网站。

## Website
AndServer为了实现静态网站内容部署，提供了`Website`这个接口，开发者实现这个接口后注册到AndServer中即可启动网站。

```java
public class MyWeibsite implement Website {
	
	@Override
	public boolean intercept(HttpRequest request, HttpContext context)
			throws HttpException, IOException {
		// 1. 是否拦截这个请求，如果拦截返回true，不拦截返回false。
		// 2. 拦截请求后AndServer将会调用下面的handle()方法。
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		// 处理请求并返回内容。
	}

}
```

## AndServer自带的几个网站实现

* [AssetsWebsite](./assets.md)可部署assets下的网站
* [StorageWebsite](.stoage.md)可部署SD卡下的网站
* [FileBrowser](./file.md)可部署SD卡下的内容，网页访问时以文件浏览器的实行展示