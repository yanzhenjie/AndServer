# 入门

AndServer从2.0.0开始，基于编译时注解实现了和SpringMVC几乎完全一样的注解Api来供开发者使用。

在AndServer中，Http Api分为两大类，第一类是普通的Http Api之间的转发和重定向，第二类是RESTFUL风格的Api。

下方示例均假设服务器端的IP地址是`192.168.1.11`，监听的端口号是`8080`。

## RESTFUL风格的Api
下面我们将写一个模拟用户登录的Http Api：
```java
@RestController
public class UserController {

    @PostMapping("/user/login")
    String login(@RequestParam("account") String account,
        @RequestParam("password") String password) {
        if("123".equals(account) && "123".equals(password)) {
            return "Login successful.";
        } else {
            return "Login failed.";
        }
    }
}
```

> 上述示例完成了一个Http Api，在Java允许的范围内方法名随便取，只需要在类上加`RestController`注解，在目标方法上加`PostMapping`注解即可，再不需要任何操作。

上述Http Api的请求地址是`http://192.168.1.11:8080/user/login`，请求方法是`POST`，客户端需带上帐号`account`参数和密码`password`参数，在帐号和密码都是`123`时，我们返回给客户端的数据是`Login successful`，否则是`Login failed`，默认情况下的响应码是200。

```java
@RestController
public class UserController {

    @PostMapping("/user/get")
    User login(@RequestParam("id") String id) {
        User user = new User();
        user.setId(id);
        user.setName("AndServer");
        return user;
    }
}
```
在RestController中，返回值可以是String、可以是Model对象或者文件等。

## 非RESTFUL风格的API
如果我们想让用户访问`http://192.168.1.111:8080/`时实际请求的是`http://192.168.1.11:8080/index.html`：

```java
@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}
```

返回值中`forward:`表示在服务器内部转发，`forward:/index.html`则表示转发到`/`下的`index.html`页面。

如果我们想让用户访问`http://192.168.1.11/projectInfo`时实际让用户访问的是`http://192.168.1.11/project/info`：

```java
@Controller
public class ProjectController {

    @ResponseBody
    @GetMapping("/project/info")
    public String newInfo() {
        return "I am new api.";
    }

    @GetMapping("/projectInfo")
    public String oldInfo() {
        return "forward:/project/info";
    }
}
```

如果我们不想在服务器内部做转发，而是想发一个重定向响应，让客户端重新请求另一个Http Api，那么只需要把关键词`forward`改成`redirect`即可：

```java
@Controller
public class ProjectController {

    @ResponseBody
    @GetMapping("/project/info")
    public String newInfo() {
        return "I am new api.";
    }

    @GetMapping("/projectInfo")
    public String oldInfo() {
        return "redirect:/project/info";
    }
}
```

**现在，几个简单的Http Api就写完了**，你不需要任何注册或者配置，只需要启动[服务器](/Server.md)就可以通过浏览器或者测试工具访问上面的几个Http Api了。