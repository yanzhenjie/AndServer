# RequestBody

本文主要介绍`RequestBody`注解的使用方法，开发者不可将`RequestBody`注解和[RequestBody](../class/RequestBody.md)注解混淆。

客户端可以把一段JSON或者一个文件整体写到请求的Body中发送服务端，服务端的处理方法有两种，第一种是拿到[RequestBody](../class/RequestBody.md)对象，由开发者自行转换为目标对象，第二种是使用`RequestBody`注解结合[MessageConverter](../class/MessageConverter.md)自动转换为目标对象。

## 第一种方式
```java
@RestController
public class UserController {

    @PostMapping(path = "/pushUser")
    String push(RequestBody body) throws IOException {
        String content = body.string();
        User user = JSONUtils.parseObject(content, User.class);
        ...
    }
}
```

## 第二种方式
```java
@RestController
public class UserController {

    @PostMapping(path = "/pushUser")
    String push(@RequestBody User user) {
        ...
    }
}
```

可以直观的看出，第二种方式比第一种省去了两行代码和一个异常抛出。

如果开发者实现的[MessageConverter](../class/MessageConverter.md)支持转化文件，那么我们还可以将Body转化为文件：
```java
@RestController
public class FileController {

    @PostMapping(path = "/pushFile")
    String upload(@RequestBody File file) {
        ...
    }
}
```

> `RequestBody`注解的参数默认是必须的，如果此参数为空，则会抛出`BodyMissingException`异常，异常处理请参考[ExceptionResolver](../class/ExceptionResolver.md)。

如果开发者想让`RequestBody`为非必填参数：
```java
@RestController
public class FileController {

    @PostMapping(path = "/pushFile")
    String upload(@QueryParam("id") String id, 
        @RequestBody(required = false) File file) {
        ...
    }
}
```