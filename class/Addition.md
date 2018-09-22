# Addition

本文主要介绍`Addition`类的使用方法，开发者不可将`Addition`类和[Addition](../annotation/Addition.md)注解混淆。

在学习使用`Addition`之前，开发者应该先了解[Addition](../annotation/Addition.md)注解和[HandlerInterceptor](HandlerInterceptor.md)类。

`Addition`类的属性对应的是[Addition](../annotation/Addition.md)注解的参数。

## 示例
我们在一个Http Api的方法上添加[Addition](../annotation/Addition.md)注解，增加一些附加信息：
```java
@RestController
class UserController {

    @Addition(stringType = "needLogin", booleanType = true)
    @GetMapping(path = "/get")
    UserInfo userInfo() {
        ...
    }
}
```

然后可以在[HandlerInterceptor](HandlerInterceptor.md)中获取这些附加信息：
```java
```java
@Interceptor
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean onIntercept(HttpRequest request, HttpResponse response,
        RequestHandler handler) {
        if (handler instanceof MethodHandler) {
            MethodHandler methodHandler = (MethodHandler)handler;
            Addition addition = methodHandler.getAddition();
            ...
        }
        
        ...
    }
}
```

> **注意**：必须先判断`RequestHandler`是不是`MethodHandler`，只有`MethodHandler`才是`Controller`中的方法，其它可能是网页、CSS文件等。