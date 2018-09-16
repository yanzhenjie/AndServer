# 默认

AndServer默认的异常解决者：
```java
if (e instanceof BaseException) {
    BaseException exception = (BaseException) e;
    return new View(exception.getHttpCode(), exception.getHttpBody());
}

String message = String.format("Server error occurred:\n%1$s", e.getMessage());
HttpEntity httpEntity = new StringEntity(message, ContentType.TEXT_PLAIN);
return new View(500, httpEntity);
```

默认的异常解决者先判断异常是否是自定义异常，如果是那么返回自定义的`响应码`和`响应数据`；如果不是则返回500响应码，并告诉客户端哪一个接口出了问题。

## 建议
比如我们在[拦截器](/interceptor/README.md)中提到的拦截登录，其实我们可以定义一个`NotLoginException`异常，直接在拦截到用户未登录时抛出这个异常即可。  

当然还可以定义很多验证类型的异常，不必每次都自己组织数据结构。