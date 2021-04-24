# PathVariable

没有阅读[Controller](Controller.md)、[RestController](RestController.md)和[RequestMapping](RequestMapping.md)章节的开发者应先阅读上述几个章节。

## 作用

只能用在方法参数上，用来获取客户端的`path`参数。

`RequestParam`支持注解的参数类型有：`String`、`int`、`long`、`float`、`double`、`boolean`。

## 示例

```java
@RestController
public class UserController {

    @GetMapping("/user/{userId}")
    User idInfo(@PathVariable("userId") String id) {
        ...
    }

    @GetMapping("/user/age/{value}/list")
    List<User> ageInfo(@PathVariable("value") int age) {
        ...
    }

    @GetMapping("/user/gender/{value}/list")
    List<User> ageInfo(@PathVariable("value") boolean boy) {
        ...
    }
}
```

第一个方法的含义是，根据 ID 查询用户，假设服务端的 IP 地址是`192.168.1.111`，监听的端口是`8080`，那么获取用户 ID 为`ID123`的用户信息的地址是：  
`http://192.168.1.11:8080/user/ID123`。

第二个方法的含义是，根据年龄获取一个用户列表，例如获取年龄为`18`岁的用户列表的地址是：  
`http://192.168.1.11:8080/user/age/18/list`。

第三个方法的含义是，获取某性别的用户列表，例如获取男性列表的地址是：  
`http://192.168.1.11:8080/user/gender/male/list`。
