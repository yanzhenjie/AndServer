# CookieValue

`CookieValue`注解是帮助开发者方便获取客户端提交的Cookie值的。

## 示例
在不使用`CookieValue`注解的情况下，我们是这样获取Cookie值的：
```java
@RestController
class UserController {

    @GetMapping(path = "/get")
    String info(HttpRequest request) {
        Cookie cookie = request.getCookie("account");
        String cookieValue = cookie == null ? null : cookie.getValue();
        ...
    }
}
```

在使用了`CookieValue`注解时：
```java
@RestController
class UserController {

    @GetMapping(path = "/get")
    String info(@CookieValue("account") String cookie) {
        ...
    }
}
```

> `CookieValue`注解的参数默认是必须的，如果此参数为空，则会抛出`CookieMissingException`异常，异常处理请参考[ExceptionResolver](../class/ExceptionResolver.md)。

如果开发者想让`CookieValue`注解的参数为非必填参数：
```java
@RestController
class UserController {

    @GetMapping(path = "/get")
    String info(@CookieValue(name = "account", required = false) String cookie) {
        ...
    }
}
```