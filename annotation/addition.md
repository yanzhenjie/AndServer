# Addition

`Addition`注解用在Http Api的方法上，用来帮助开发者为此Http Api增加一些额外的信息，方便开发者在[HandlerInterceptor](../class/HandlerInterceptor.md)（拦截器）中处理一些业务，例如某些接口需要登录后才能请求。

`Addition`注解提供了`String`、`long`、`int`、`short`、`double`、`float`、`byte`、`char`等多个数据类型的**数组**参数，因此交叉后可以得出无数中注解结果。

## 示例
例如，我们需要标记某个方法需要登录才能访问，否则将在[拦截器](../class/HandlerInterceptor.md)中拦截掉：
```java
@RestController
class TestController {

    @Addition("needLogin")
    @GetMapping(path = "/userInfo")
    UserInfo userInfo() {
        ...
    }
}
```

例如，某个方法需要取消验证客户端签名，否则将被拦截：
```java
@RestController
class TestController {

    @Addition(stringType = "signature", booleanType = false)
    @GetMapping(path = "/get")
    UserInfo userInfo() {
        ...
    }
}
```

可能性很多，开发者可以自行发散。

我们将在`HandlerInterceptor`中拿到这些注解信息，具体请参考[HandlerInterceptor](../class/HandlerInterceptor.md)。