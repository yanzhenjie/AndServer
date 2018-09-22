# MessageConverter

`MessageConverter`需要结合[Converter](../annotation/Converter.md)注解使用，首先需要一个类实现`MessageConverter`接口，然后在该类上加上`Converter`注解即可使用，不需要其他配置。

> **注意**：实现类必须提供一个无参构造方法供AndServer调用，否则编译不通过。

```java
public interface MessageConverter {

    /**
     * Convert a specific output to the response body. Some of the return values
     * of handlers that cannot be recognized by {@link ViewResolver} require a 
     * message converter to be converted to a response body.
     *
     * @param output output of handle.
     * @param mediaType the content media type specified by the handler.
     */
    ResponseBody convert(Object output, MediaType mediaType);

    /**
     * Convert RequestBody to a object.
     *
     * @param stream {@link InputStream}.
     * @param mediaType he content media type.
     * @param type type of object.
     * @param <T> type of object.
     *
     * @return object.
     */
    <T> T convert(InputStream stream, MediaType mediaType, Type type);
}
```

第一个方法用来把添加了[RestController](../annotation/RestController.md)注解的类中的方法返回值转化为开发者想要发送给客户端的格式。

第二个方法用来把客户端的请求参数转换为添加了[RequestBody](../annotation/RequestBody.md)注解或者[FormPart](../annotation/FormPart.md)注解的服务端参数对应的Model对象。


## 示例，服务端 -> 客户端
一般情况下，我们发送到客户端的消息格式、结构都是确定且统一的。假设我们给客户端的数据是JSON，且有以下三个字段：
```json
{
    "isSuccess" : true,
    "message" : "Success",
    "data": ?,
}
```

如上所示，当`isSuccess`字段是`true`时，客户端应该读取`data`字段，当`isSuccess`是`false`时，客户端应该读取`message`字段并提示用户，因此我们有一个对应的Model类：
```java
public class ReturnData {
    private boolean isSuccess;
    private String errorMsg;
    private Object data;

    ...
}
```

如上所示，省去了`getter`和`setter`方法，那么`MessageConverter`的第一个方法的实现应该是：
```java
@Converter
public class AppMessageConverter interface MessageConverter {

    @Override
    public ResponseBody convert(Object output, MediaType mediaType) {
        String data = JsonUtils.toJSONString(output);

        ReturnData returned = new ReturnData();
        returned.setSuccess(true);
        returned.setData(data);
        return returned;
    }

    ...
}
```

## 示例，客户端 -> 服务端
在[RequestBody](../annotation/RequestBody.md)注解和[FormPart](../annotation/FormPart.md)注解中说到，可以利用`MessageConverter`将一些复杂的参数转化为服务端的Model来简化服务端的开发步骤，就是利用上文中提到的第二个方法。

这里先假设开发者要转化的数据都是JSON，例如这样一个Http Api：
```java
@RestController
public class UserController {

    @PostMapping(path = "/pushUser")
    String push(@RequestBody User user) {
        ...
    }
}
```

其实无论Http Api的注解方法是什么都不影响`MessageConverter`的写法，这里是举例只是为了让开发者更好的理解。

如果客户端提交的复杂参数，数据格式都是JSON，那么第二个方法的实现应该是：
```java
@Converter
public AppMessageConverter interface MessageConverter {

    ...

    @Override
    public <T> T convert(InputStream stream, MediaType mediaType, Type type) {
        Charset charset = mediaType == null ? null : mediaType.getCharset();
        if (charset == null) {
            return JsonUtils.parseJson(IOUtils.toString(stream), type);
        }
        return JsonUtils.parseJson(IOUtils.toString(stream, charset), type);
    }
}
```

当然，这里还可能是File或者其他对象，开发者可以判断Type的类型，例如：
```java
if(type instance File.class) {
    ...
}
```

## 完整示例
本示例约定，服务端下行数据和客户端上行数据都是JSON格式：
```java
@Converter
public class AppMessageConverter interface MessageConverter {

    @Override
    public ResponseBody convert(Object output, MediaType mediaType) {
        String data = JsonUtils.toJSONString(output);
        ReturnData returned = new ReturnData();
        returned.setSuccess(true);
        returned.setData(data);
        return returned;
    }

    @Override
    public <T> T convert(InputStream stream, MediaType mediaType, Type type) {
        Charset charset = mediaType == null ? null : mediaType.getCharset();
        if (charset == null) {
            return JsonUtils.parseJson(IOUtils.toString(stream), type);
        }
        return JsonUtils.parseJson(IOUtils.toString(stream, charset), type);
    }
}
```

如上述示例，开发者可以举一反三写出更加丰富的程序。