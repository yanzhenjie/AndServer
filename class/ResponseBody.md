# ResponseBody

本文主要介绍`ResponseBody`类的使用方法，开发者不可将`ResponseBody`类和[ResponseBody](../annotation/ResponseBody.md)注解混淆。

在学习使用`ReponseBody`之前，开发者应该先掌握[Controller](../annotation/controller.md)、[RestController](../annotation/restController.md)和[RequestMapping](../annotation/requestMapping.md)的使用。

`ResponseBody`是响应包体，`Controller`和`RestController`中的方法返回值最终都会被转化为`ResponseBody`发送，因此如果遇到通过本文开始提到的三者无法实现的需求时，才推荐使用`ResponseBody`。

## 使用方法
`ResponseBody`是一个`Interface`，AndServer也提供了一些常用实现类。

### FileBody
```java
File file = ...;
RequestBody body = new FileBody(file);
```
### JsonBody
```java
// 1.
String jsonStr = ...;
RequestBody body = new JsonBody(jsonStr);

// 2.
JSONObject jsonObj = ...;
RequestBody body = new JsonBody(jsonObj);
```
### StreamBody
```java
InputStream stream = ...;

// 1.
RequestBody body = new StreamBody(stream);

// 2.
int len = ...; // Length of stream.
RequestBody body = new StreamBody(stream, len);

// 3.
MediaType mimeType = ...; // MimeType of stream.
RequestBody body = new StreamBody(stream, len, mimeType);
```
The parameters are detailed, the better the performance, the better the compatibility with the client.

### StringBody
```java
// 1.
String content = ...;
RequestBody body = new StringBody(content);

// 2.
MediaType mimeType = ...; // MimeType of content.
RequestBody body = new StringBody(content, mimeType);
```

## 返回字符串示例
```java
@Controller
public class ProjectController {

    @GetMapping("/project/info")
    public void info(HttpResponse response) {
        String content = ...;
        RequestBody body = new StringBody(content);
        response.setBody(body);
    }
}
```

## 返回JSON示例
```java
@Controller
public class ProjectController {

    @GetMapping("/project/info")
    public void info(HttpResponse response) {
        JSONObject jsonObj = ...;
        RequestBody body = new JsonBody(jsonObj);
        // Or
        String json = ...;
        RequestBody body = new JsonBody(json);
        response.setBody(body);
    }
}
```

## 返回文件示例
```java
@Controller
public class ProjectController {

    @GetMapping("/project/info")
    public void info(HttpResponse response) {
        File file = ...;
        RequestBody body = new FileBody(file);
        response.setBody(body);
    }
}
```

## 自定义
```java
public class DefineBody implements ResponseBody {

    @Override
    public long contentLength() {
        // TODO return the length of content.
    }

    @Nullable
    @Override
    public MediaType contentType() {
        // TODO return the content type.
    }

    @Override
    public void writeTo(@NonNull OutputStream output) throws IOException {
        // TODO write the content.
    }
}
```

----

相关阅读推荐：  
[Controller](../annotation/controller.md)  
[RestController](../annotation/restController.md)  