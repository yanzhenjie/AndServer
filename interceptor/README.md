# 拦截器

拦截器的功能是在请求进入和请求出去时进行拦截，如果某个请求被拦截，那么所有的`Website`或者`RequestHandler`都将收不到任何请求。

```java
public class MyInterceptor implements Interceptor {

    @Override
    public boolean onBeforeExecute(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        ...           // 进行逻辑判断是否要拦截。
        return true;  // 返回true将拦截，返回false不拦截；
    }

    @Override
    public void onAfterExecute(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        ... // 如果不拦截将在请求处理完毕后会调用onAfterExecute。
    }
}
```

## 拦截登录示例

假如我们要拦截某些接口的用户登录，我们可以用注解和反射实现。

### 思路：
1. 定义一个需要登录的注解
2. 需要登录的接口的`handle()`方法上加上该注解
3. 在`onBeforeExecute`上进行拦截

### 实现

定义注解：
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedLogin {
    boolean need() default true;
}
```

需要拦截登录的`handle`：
```java
public class AdminHandler implement RequestHandler {
	
	@NeedLogin()
	@ovrride
	public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
	}

}
```

拦截登录：
```java
@Override
public boolean onBeforeExecute(HttpRequest request, HttpResponse response, HttpContext context)
        throws HttpException, IOException {
    Class<?> clazz = handler.getClass();

    Method method = clazz.getMethod("handle", HttpRequest.class,
        HttpResponse.class, HttpContext.class);
    NeedLogin needLogin = method.getAnnotation(NeedLogin.class);
    if (needLogin != null) {
        if(needLogin.need()) {
        	String token = request.getHeader("Token");
        	if(verifyToken(token)) { // 假设这里验证Token是否有效。
                response.setStatus(401);
                ... // 返回一些数据。

				return true; // 拦截掉。
        	}
        }
    }
	return false;
}
```

> 为了方便阅读代码，这里省去一些非关键代码。

这里就为需要登录的接口拦截了没有登录的请求。还有一些更详细的处理，比如抛出异常等登录，请参考[../exception/README.md](异常解决者)。

### 结合异常解决者
如果你读完了异常解决者一章的文档，那么我们可以简化一下我们的拦截器。

定义自定义异常：
```java
public class NotLoginException extends BaseException {
    ...
}
```

```java
@Override
public boolean onBeforeExecute(HttpRequest request, HttpResponse response, HttpContext context)
        throws HttpException, IOException {
    Class<?> clazz = handler.getClass();

    Method method = clazz.getMethod("handle", HttpRequest.class, HttpResponse.class,
        HttpResponse.class, HttpContext.class);
    NeedLogin needLogin = method.getAnnotation(NeedLogin.class);
    if (needLogin != null) {
        if(needLogin.need()) {
            String token = request.getHeader("Token");
            if(verifyToken(token)) { // 假设这里验证Token是否有效。
                new throw NotLoginException("请您先登录");
            }
        }
    }
    return false;
}
```