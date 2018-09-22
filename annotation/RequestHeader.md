# RequestHeader

`RequestHeader`注解是帮助开发者方便获取客户端的Header值的。

## 示例
在不使用`RequestHeader`注解的情况下，我们是这样获取Header值的：
```java
@RestController
class UserController {

    @GetMapping(path = "/get")
    String info(HttpRequest request) {
        String header = request.getHeader("location");
        ...
    }
}
```

在使用了`RequestHeader`注解时：
```java
@RestController
class UserController {

    @GetMapping(path = "/get")
    String info(@RequestHeader("location") String location) {
        ...
    }
}
```

> `RequestHeader`注解的参数默认是必须的，如果此参数为空，则会抛出`HeaderMissingException`异常，异常处理请参考[ExceptionResolver](../class/ExceptionResolver.md)。

如果开发者想让`RequestHeader`注解的参数为非必填参数：
```java
@RestController
class UserController {

    @GetMapping(path = "/get")
    String info(@RequestHeader(name = "account", required = false) String header) {
        ...
    }
}
```