# 自定义

如果AndServer自带的网站不够用或者和开发者的需求有出入，那么们可以自定义网站。

1. 实现`Website`接口，或者继承`SimpleWebsite`基类
2. 在`Website`中拦截要处理的请求
3. 处理Request并返回内容

```java
public class MyWeibsite implement Website {
	
	private List<String> mPathList;

	...

	@Override
	public boolean intercept(HttpRequest request, HttpContext context) ... {
		// 拿到http的路径。
		String httpPath = HttpRequestParser.getRequestPath(request);

		// 判断是否要拦截。
		if(mPathList.contains(httpPath)) { // 这里可以写一些判断逻辑。
			return true;
		} else {
			return false;
		}

		...

		// 如果真是这么简单的判断逻辑，简单点
		return mPathList.contains(httpPath);
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) ... {
		// 这里和RequestHandler的处理方式完全相同，请参考RequestHandler一章。
	}

}
```