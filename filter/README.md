# 过滤器

当我们第一眼看到过滤器的时候很容易和`Inteceptor`联系到一起，这也是难免容易混淆的两个概念。

## 拦截器和过滤器的区别
`Inteceptor`是拦截请求/响应的，也就是拦截`Request/Response`对的。每一个`Request/Response`对都会经过拦截器，关于拦截器的详情可以看[拦截器](/interceptor/README.md)章节。  

`Filter`是过滤`RequestHandler`的。当服务器接受到一个请求后，先经过`Inteceptor`拦截，如果没有被拦截，会根据`Request`匹配对应的`RequestHandler`，如果没有找到则抛出`NotFoundException`异常，如果找到对应的`RequestHandler`，如果开发者设置了`Filter`，那么把`RequestHandler`和`Request/Response`对交给`Filter`处理，如果没有设置`Filter`，则由对应的`RequestHandler`处理。

底层的代码大概是这样的：
```java
if (mInterceptor != null && mInterceptor.onBeforeExecute(request, response, context))
    return;

RequestHandler handler = getRequestHandler(request, context);
if (handler == null) {
    throw new NotFoundException(path);
} else {
    if (mFilter != null) {
            mFilter.doFilter(handler, request, response, context);
    } else {
        handler.handle(request, response, context);
    }
}

if (mInterceptor != null)
    mInterceptor.onAfterExecute(request, response, context);
```