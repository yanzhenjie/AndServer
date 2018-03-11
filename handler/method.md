# 请求方法

很多时候我们需要限制某个接口的请求方法，比如POST、PUT、GET，为了避免每个接口都需要我们检查请求方法，AndServer提供了注解来简化它。

我们只需要在`RequestHandler#handle()`上加上`@RequestMapping()`注解即可，例如我们限制某个接口只能用POST请求：
```java
public class LoginHandler implements RequestHandler {

    @RequestMapping(method = RequestMethod.POST)
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
    	...;
    }
}
```

当然我们也可以允许让它支持多种请求方法：
```java
public class LoginHandler implements RequestHandler {

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
    	...;
    }
}
```

**注意**：如果使用注解必须要在`RequestHandler#handle()`方法上，也就是说它不支持简化`RequestHandler`的实现。当然这也不是不能做到，例如我们可以写个基类：
```java
public class GetHandler implement RequestHandler {

    @RequestMapping(method = RequestMethod.GET)
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
    	View view = handle(request, response);
        response.setStatusCode(view.getHttpCode());
        response.setEntity(view.getHttpEntity());
        response.setHeaders(view.getHeaders());
    }

    protected View handle(HttpRequest request, HttpResponse response)
            throws HttpException, IOException {
        return handle(request);
    }

    protected View handle(HttpRequest request) throws HttpException, IOException {
        return new View(200);
    }
}
```