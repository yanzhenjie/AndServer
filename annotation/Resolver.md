# Resolver

Resolver是解决者的意思，在AndServer中`Resolver`注解用来标记某个类是[ExceptionResolver](../class/ExceptionResolver.md)的子类，并且作为异常解决者参与到`AndServer`的运行中。

`Resolver`注解可以添加到多个类，但是只有其中一个添加了`Resolver`注解的类参与程序运行，具体是哪个要看编译器先扫描到哪个类，**因此建议开发者只为一个类添加`Resolver`注解**。

## 示例
```java
@Resolver
public class AppExceptionResolver implements ExceptionResolver {

    @Override
    public void onResolve(HttpRequest request, HttpResponse response, Throwable e) {
    }
}
```

> 注意，添加`Resolver`注解的类必需是[ExceptionResolver](../class/ExceptionResolver.md)的子类，否则编译时将不通过。