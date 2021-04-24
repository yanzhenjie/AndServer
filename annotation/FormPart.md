# FormPart

`FormPart`和[RequestParam](RequestParam.md)和[QueryParam](QueryParam.md)一样只能用在方法参数上，用来获取客户端的请求参数，不同的是`FormPart`仅用来获取 Form 中的参数。

大多数情况下，使用`RequestParams`已经可以获取所有参数了，包括表单里面的参数，而`FormPart`可以让一些复杂参数变得更简单。

`FormPart`支持的参数类型有两种，一种是[MultipartFile](../class/MultipartFile.md)，另一种是 Model，比如`User`、`List<User>`和`Map<String, User>`这样的参数。

例如客户端在表单的某个 Item 提交了一段 JSON、或者一个 JSON 文件，当我们使用`RequestParam`获取到这段 JSON 以后，还需要把这段 JSON 转为 Model 对象，这样显得比较麻烦。如果使用了`FormPart`就可以把这样的复杂参数转换为服务端的 Model 对象。

不过使用`FormPart`需要开发者提供`MessageConverter`来做转换，具体使用方法请参考[Converter](Converter.md)注解和[MessageConverter](../class/MessageConverter.md)类。

## 示例

```java
@RestController
public class UserController {

    @PostMapping("/user/update")
    void updateUser(@FormPart("user") User user) {
        ...
    }

    @PostMapping("/user/children")
    String upload(@FormPart("items") List<Child> children) throws IOException {
        ...
    }
}
```

第一个 HTTP API 客户端应该在表单中添加一个`key`为`user`的参数，其`value`的 JSON 就是`User`转为 JSON 字符串后的结果。

```json
{
  "name": "AndServer",
  "age": 18,
  "gender": "male"
}
```

第二个 HTTP API 客户端应该在表单中添加一个`key`为`items`的参数，其`value`的 JSON 结构应该是：

```json
[
  {
    "name": "Child1",
    "age": 18,
    "gender": "male"
  },
  {
    "name": "Child2",
    "age": 18,
    "gender": "female"
  }
]
```

> 因为不同的`MessageConverter`实现可能是对 JSON 的转化、XML 的转化或者 Protobuf 的转化等，上述示例是以 JSON 为例，开发者应注意举一反三。

---

相关阅读推荐：

1. [RequestParam](RequestParam.md)
2. [QueryParam](QueryParam.md)
