# QueryParam

`QueryParam`和[RequestParam](requestParam.md)相同只能用在方法参数上，用来获取客户端的请求参数，支持类型除了`MultipartFile`外其他完全一致。不同的是`QueryParam`仅用来获取Url中的参数。

## 示例
```java
@RestController
public class UserController {

    @GetMapping("/user/info")
    String info(@QueryParam(name = "id") long id) {
        ...
    }
}
```

假设服务端的IP是`192.168.1.11`，监听的端口是`8080`，服务端访问上述Http Api时的Url应该是：
```
http://192.168.1.11:8080/user/infi?name=123
```

如果在Url中没有`name`参数则会抛出`ParamMissingException`异常，开发者可以使用`required`来取消必填能力，详情参考[RequestParam](requestParam.md)。

----

相关阅读推荐：  
* [RequestParam](requestParam.md)  
* [FormPart](formPart.md)  