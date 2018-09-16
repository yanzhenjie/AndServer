# 异常解决者

异常解决者用来处理在每一个`request/response`对中发生的所有异常，包括`Interceptor`、`RequestHandler`、`Filter`等的处理过程。  

它好比SpringMVC中的`HandlerExceptionResolver`或者`@ControllerAdvice` + `@ExceptionHandler`。

我们来看看它的代码：
```java
public interface ExceptionResolver {
    void resolveException(Exception e, HttpRequest request, HttpResponse response,
            HttpContext context);
}
```

第一个参数就是处理某个请求时发生的异常，后面的`Request/Response`就是当前发生异常的请求响应对。

## AndServer中的异常
AndServer中定义了一个基本的异常`BaseException`：
```java
public class BaseException extends HttpException {

    private int mHttpCode;
    private HttpEntity mHttpBody;

    public BaseException() {
        this(500, "Unknown exception occurred on server.");
    }

    public BaseException(int httpCode, String httpBody) {
        super(httpBody);
        this.mHttpCode = httpCode;
        this.mHttpBody = new StringEntity(httpBody, ContentType.TEXT_PLAIN);
    }

    public int getHttpCode() {
        return mHttpCode;
    }

    public HttpEntity getHttpBody() {
        return mHttpBody;
    }
}
```

它包含了返回给客户端的响应码和数据，如果不需要添加响应头等信息时，我们的自定义异常可以继承它。  

例如，在[Http接口](../handler/README.md)章节提到的[请求方法](../handler/method.md)中，AndServer会检查请求方法和`handle()`的注解的请求方法是否匹配，如果不匹配则会抛出一个`MethodNotSupported`异常，这个异常就是继承自`BaseException`。

AndServer中在处理请求时会抛出的两个异常：
* NotFoundException，path指定的资源没有找到
* MethodNotSupported，接口不支持的请求方法