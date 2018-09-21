# Interceptor

Interceptor是拦截器的意思，在AndServer中`Interceptor`注解用来标记某个类是[HandlerInterceptor](../class/HandlerInterceptor.md)的子类，并且作为拦截器参与到`AndServer`的运行中。

`Interceptor`注解可以添加到多个类，所有添加了`Interceptor`注解的类都将参与程序运行。

## 示例
```java
@Interceptor
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean onIntercept(HttpRequest request, HttpResponse response) {
        ...
    }
}
```

> 注意，添加`Interceptor`注解的类必需是[HandlerInterceptor](../class/HandlerInterceptor.md)的子类，否则编译时将不通过。