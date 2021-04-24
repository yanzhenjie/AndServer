# RequestBody

本文主要介绍`RequestBody`类的使用方法，开发者不可将`RequestBody`类和[RequestBody](../annotation/RequestBody.md)注解混淆。

客户端可以把一段 JSON 或者一个文件整体写到请求的 Body 中发送服务端，服务端的处理方法有两种，第一种是拿到 RequestBody 对象，由开发者自行转换为目标对象，第二种是使用[RequestBody](../annotation/requestBody.md)自动转换为目标对象。

在学习使用`RequestBody`之前，开发者应该先掌握[Controller](../annotation/Controller.md)、[RestController](../annotation/RestController.md)和[RequestMapping](../annotation/RequestMapping.md)的使用。

因为`RequestBody`最终的呈现形式是通过流，所以客户端可以把任何内容放到`RequestBody`的请求包体中发送到服务端，服务端可以把`RequestBody`再转为任何自己想要的数据类型。

## 转换为字符串示例

`RequestBody`类提供了一个`string()`方法，因此转为`string()`是比较简单的：

```java
@RestController
public class UploadController {

    @PostMapping("/pushString")
    void upload(RequestBody body) throws IOException {
        String content = body.string();
        ...
    }
}
```

## 转换为文件示例

`RequestBody`类提供了一个`stream()`方法，可以拿到输入流，因此可以轻松转为文件。

```java
@RestController
public class UploadController {

    @PostMapping("/pushFile")
    void upload(RequestBody body) throws IOException {
        InputStream is = body.stream();
        File file = ...;
        OutputStream os = new FileOutputStream(file);
        IOUtils.write(is, os);
    }
}
```

**建议**：建议开发者在遇到使用[RequestBody](../annotation/RequestBody.md)注解实在难以解决的需求时使用本章节的方法。对于 AndServer 来说使用`RequestBody`注解更加简易和规范。
