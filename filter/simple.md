# 简单示例

只需要实现`Filter`接口即可：
```java
public class MyFilter implement Filter {

	@Ovrride
    public void doFilter(RequestHandler handler, HttpRequest request, HttpContext context)
    	throws HttpException, IOException {
		boolean isFilted = ... // 一些处理逻辑，过滤器是否处理。

		if(!isFitler) { // 如果过滤器没处理则交给handler处理。
			handler.hande(request, response, context);
		}
    }
}
```

为了方便阅读，这里省去一些非关键代码。