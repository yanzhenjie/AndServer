# 入门

AndServer 从 2.0.0 开始，基于编译时注解实现了和 SpringMVC 几乎完全一样的注解 API 来供开发者使用。

那么作为一个 WebServer 和 WebFramework 如何让开发者快速入门呢？我们理解应该是从 WebServer 的能力切入，那么一个 WebServer 有什么能力呢？

1. 部署静态网站
2. 开发 HTTP API

下面我们将给出最基本的几个入门示例，下方入门示例均假设**服务器端的 IP 地址是`192.168.1.11`，监听的端口号是`8080`。**

## 部署静态网站

大多数人应该听说过 Tomcat、Nginx、JBoss 等服务器吧，与他们不一样的是，AndServer 没有规定静态文件（网页、文件等）应该放在哪个位置，所以我们需要通过配置的方式告诉 AndServer 静态网站的位置在哪里，我们需要使用到[WebConfig](../class/WebConfig.md)接口和[Config](../annotation/Config.md)注解。

```java
@Config
public class AppConfig implements WebConfig {

    @Override
    public void onConfig(Context context, Delegate delegate) {
        // 增加一个位于assets的web目录的网站
        delegate.addWebsite(new AssetsWebsite(context, "/web/"));

        // 增加一个位于/sdcard/Download/AndServer/目录的网站
        delegate.addWebsite(new StorageWebsite(context, "/sdcard/Download/AndServer/"));
    }
}
```

此时，我们通过浏览器就可以访问上述两个目录的所有文件了，并且通过上述配置，网站可以无限制添加多个。

> 上述`/web/`目录和`/sdcard/Download/AndServer/`目录就是网站根目录，它们的路径是`http://192.168.1.11:8080/`，例如目录下的`android.apk`文件的路径是`http://192.168.1.11:8080/android.apk`。

有人问了，如果我想在网页中浏览目录，让网页列出目录中的子目录和文件呢？同样的，添加一个网站即可：

```java
@Config
public class AppConfig implements WebConfig {

    @Override
    public void onConfig(Context context, Delegate delegate) {
        // 添加一个文件浏览器网站
        delegate.addWebsite(new FileBrowser(context, "/sdcard/"));
    }
}
```

此时，通过浏览器打开网站根目录，就可以看到文件浏览器啦。

## HTTP API

接触过后端开发的人都知道，HTTP API 在能力上分位两大类，一类是返回数据类型，我们在这里约定把它叫做 RESTful 风格 HTTP API，另一类是重定向类型，我们约定把它叫做非 RESTful 风格 HTTP API。

## RESTful 风格的 HTTP API

下面我们将写一个模拟用户登录的 HTTP API：

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

上述 API 的请求地址是`http://192.168.1.11:8080/user/login`，请求方法是`POST`，客户端需带上帐号`account`参数和密码`password`参数，在帐号和密码都是`123`时，我们返回给客户端的数据是`Login successful`，否则是`Login failed`，默认情况下的响应码是 200。

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

在 RestController 中，返回值可以是 String、可以是 Model 对象或者文件等。

## 非 RESTful 风格的 API

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

如果我们不想在服务器内部做转发，而是想发一个重定向响应，让客户端重新请求另一个 API，那么只需要把关键词`forward`改成`redirect`即可：

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

**现在，几个简单的 HTTP API 就写完了**，你不需要任何注册或者配置，只需要启动[服务器](/server/README.md)就可以通过浏览器或者测试工具访问上面的几个 HTTP API 了。
