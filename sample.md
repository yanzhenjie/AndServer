# 入门

一个Web Framework最基础且最重要的能力就是开发Http Api，本文主要带领读者写一些Http Api并成功运行起来供客户端访问。本文的例子非常简单，如果读者想了解的更深入，可以点击左侧菜单栏查看相关文档。

AndServer从2.0.0开始，基于编译时注解实现了和SpringMVC几乎完全一样的注解Api来供开发者使用，因此下面我们将使用注解来入门。

在AndServer中，Http Api分为两大类，第一类是普通的Http Api之间的转发和重定向，第二类是RESTFUL风格的Api。

## 非RESTFUL风格的API
在服务器中可能有一些页面并不存在，但是我们不能保证用户不访问这个地址，因此我们可以做一些配置，把这些请求转发到其他Http Api或者做重定向。

```java
@Controller
public class PageController {

    @GetMapping(path = "/")
    public String index() {
        return "forward:/index.html";
    }
}
```

如上代码即可完成一个Http Api，在Java允许的范围内方法名随便取，只需要在类上加`Controller`注解，在目标方法上加`GetMapping`注解即可，再不需要任何操作。

假设我们本机的IP地址是`192.168.1.11`，当用户在浏览器地址栏输入`http://192.168.1.11/`时，上面的这个方法会被调用，而返回值中`forward:`表示在服务器内部转发，`forward:/index.html`则表示转发到`/`下的`index.html`页面。

为了帮助读者更深刻的理解这个含义，下面再举一个例子。假设服务端有一个Http Api的地址是：`http://192.168.1.11/project/info`，我们想在用户访问`http://192.168.1.11/projectInfo`时转发到上述地址：

```java
@Controller
public class ProjectController {

    @ResponseBody
    @GetMapping(path = "/project/info")
    public String newInfo() {
        return "I am new api.";
    }

    @GetMapping(path = "/projectInfo")
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
    @GetMapping(path = "/project/info")
    public String newInfo() {
        return "I am new api.";
    }

    @GetMapping(path = "/projectInfo")
    public String oldInfo() {
        return "redirect:/project/info";
    }
}
```

## RESTFUL风格的Api
下面我们将写一个模拟用户登录的Http Api：
```java
@RestController
class TestController {

    @PostMapping(path = "/user/login")
    String login(@RequestParam(name = "account") String account,
                 @RequestParam(name = "password") String password) {
        if("123".equals(account) && "123".equals(password)) {
            return "Login successful.";
        } else {
            return "Login failed.";
        }
    }
}
```

上述Http Api的请求地址是`http://192.168.1.11/user/login`，请求方法是`POST`，客户端需带上帐号`account`参数和密码`password`参数，在帐号和密码都是`123`时，我们返回给客户端的数据是`Login successful.`，否则是`Login failed.`，默认情况下的响应码是200。

在RestController中，返回值可以是JSONString、可以是Model对象等。

**现在，几个简单的Http Api就写完了**，你不需要任何注册或者配置，只需要启动[服务器](/server.md)就可以查看效果了。

更多内容请点击左侧菜单查看相关文档。