# HTTP 接口

对于没有做过服务端开发的 Android 开发者来说，我们一般都是对接服务端开发人员写好的接口，在 AndServer 中我们也可以写类似的 HTTP 接口。

在 AndServer 中，每一个 HTTP 的 path 就对应一个`RequestHandler`。它好比 SpringMVC 中`Controller`的某一个方法上加了`RequestMapping`注解一样，但是很遗憾 AndServer 目前没有提供像 SpringMVC 那样的注解来实现 path 注册（它是我的计划，AndServer2.0 将会用编译时注解来实现）。

在[基本 Api](../base/README.md)章节我们演示了如何注册一个 path 到服务器，下面来演示如果实现一个`RequestHandler`。

步骤：

1. 实现`RequestHandler`接口
2. 读取客户端提交的参数
3. 处理业务
4. 返回业务数据

例如一个登录接口：

```java
public class LoginHandler implements RequestHandler {

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        Map<String, String> params = HttpRequestParser.parseParams(request);

        if (!params.containsKey("username") || !params.containsKey("password")) {
            StringEntity stringEntity = new StringEntity("缺少参数", "utf-8");

            response.setStatusCode(400);
            response.setEntity(stringEntity);
            return;
        }

        String userName = URLDecoder.decode(params.get("username"), "utf-8");
        String password = URLDecoder.decode(params.get("password"), "utf-8");

        if ("123".equals(userName) && "123".equals(password)) {
            StringEntity stringEntity = new StringEntity("登录成功", "utf-8");

            response.setStatusCode(200);
            response.setEntity(stringEntity);
        } else {
            StringEntity stringEntity = new StringEntity("登录失败", "utf-8");

            response.setStatusCode(400);
            response.setEntity(stringEntity);
        }
    }
}
```

上述流程只是一个示例（请不要计较业务流程），如果你觉得上述代码冗余。AndServer 还提供了一个简单的`RequestHandler`的实现：

```java
public class LoginHandler extends SimpleRequestHandler {

    @Override
    public View handle(HttpRequest request) throws HttpException, IOException {
        Map<String, String> params = HttpRequestParser.parseParams(request);

        if (!params.containsKey("username") || !params.containsKey("password")) {
            return new View(400, "缺少参数");
        }

        String userName = URLDecoder.decode(params.get("username"), "utf-8");
        String password = URLDecoder.decode(params.get("password"), "utf-8");

        if ("123".equals(userName) && "123".equals(password)) {
            return new View(400, "登录成功");
        } else {
            return new View(400, "帐号或者密码错误");
        }
    }
}
```

更多和 View 相关的用法请参数扩展。
