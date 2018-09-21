# FormPart

`FormPart`和[RequestParam](RequestParam.md)和[QueryParam](QueryParam.md)一样只能用在方法参数上，用来获取客户端的请求参数，不同的是`FormPart`仅用来获取Form中的参数。

大多数情况下，使用`RequestParams`已经可以获取所有参数了，包括表单里面的参数，而`FormPart`可以让一些复杂参数变得更简单。

`FormPart`支持的参数类型有两种，一种是[MultipartFile](../class/MultipartFile.md)，另一种是Model，比如`User`、`List<User>`和`Map<String, User>`这样的参数。

例如客户端在表单的某个Item提交了一段JSON、或者一个JSON文件，当我们使用`RequestParam`获取到这段JSON以后，还需要把这段JSON转为Model对象，这样显得比较麻烦。如果使用了`FormPart`就可以把这样的复杂参数转换为服务端的Model对象。

不过使用`FormPart`需要开发者提供`MessageConverter`来做转换，具体使用方法请参考[Converter](Converter.md)注解和[MessageConverter](../class/MessageConverter.md)类。

## 示例
```java
@RestController
public class UserController {

    @PostMapping("/goods/publish")
    void login(@FormPart("images") List<String> images) {
        ...
    }

    @PostMapping("/goods/sort")
    String upload(@FormPart("items") List<Goods> goodsList) throws IOException {
        File localFile = ...; // Create a file at the target location.
        file.transferTo(localFile); // Transfer the file to the target location.
        return localFile.getAbsolutePath();
    }
}
```

如上所示，第一个Http Api客户端应该在表单中添加一个`key`为`images`的参数，其`value`的JSON结构应该是：
```json
[
    "string1",
    "string2"
]
```

第二个Http Api客户端应该在表单中添加一个`key`为`items`的参数，其`value`的JSON结构应该是：
```json
[
    {
        "id": "123",
        "name":"Kotlin"
    },
    {
        "id":"124",
        "name":"Android"
    }
]
```

> 因为不同的`MessageConverter`实现可能是对JSON的转化、XML的转化或者Protobuf的转化等，上述示例是以JSON为例，开发者应注意举一反三。

----

相关阅读推荐：  
1. [RequestParam](RequestParam.md)  
2. [QueryParam](QueryParam.md)