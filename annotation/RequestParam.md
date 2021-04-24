# RequestParam

没有阅读[Controller](Controller.md)、[RestController](RestController.md)和[RequestMapping](RequestMapping.md)章节的开发者应先阅读上述几个章节。

## 作用

只能用在方法参数上，用来获取客户端的请求参数。

`RequestParam`支持注解的参数类型有：`MultipartFile`、`String`、`int`、`long`、`float`、`double`、`boolean`，其中[MultipartFile](../class/MultipartFile.md)表示表单中的文件，其它类型可以是 Url 中的参数、Body 中的参数（客户端`Content-Type`是`application/x-www-form-urlencoded`，数据又在 Body 中时）、表单中的参数。

如果要单独获取 Url 中的参数请使用[QueryParam](QueryParam.md)注解。

## 接受普通参数示例

```java
@RestController
public class UserController {

    @PostMapping("/user/login")
    void login(@RequestParam("account") String account,
        @RequestParam("password") String password) {
        ...
    }
}
```

上述示例是一个模拟用户登录的 HTTP API，客户端必须提交`account`和`parssword`参数，否则将会抛出`ParamMissingException`异常。

> 异常处理请参考[ExceptionResolver](../class/ExceptionResolver.md)。

我们也可以让某参数不是必填的，同时也可以选择给它一个默认值：

```java
@RestController
public class UserController {

    @GetMapping("/user/info")
    void info(@RequestParam(name = "id",  required = false, defaultValue = "123")
        long id) {
        ...
    }
}
```

上述示例中，如果客户端请求中没有提交`id`参数或者`id`参数为空，那么开发者接受到的值则是`123`。

## 接受文件示例

```java
@RestController
public class UserController {

    @PostMapping("/user/upload")
    String upload(@RequestParam("image") MultipartFile file) throws IOException {
        File localFile = ...; // Create a file at the target location.
        file.transferTo(localFile); // Transfer the file to the target location.
        return localFile.getAbsolutePath();
    }
}
```

入参`MultipartFile`是一个临时文件，它保存在[WebConfig](../class/WebConfig.me)这里配置的上传文件保存目录，你也可以把这个文件转移到任何位置保存。

**强烈建议不要转移，在 WebConfig 配置好就好了，转移的话要耗费文件拷贝时间，这样降低了接口性能。**

## 无需注解的参数

一些特殊参数不需要注解就可以拿到，支持不用注解的参数有`Context`、`HttpRequest`、`HttpResponse`、`Session`、`RequestBody`。

> 上述`Context`是 Android 中的`android.content.Context`。

```java
@Controller
public class ProjectController {

    @GetMapping("/project/get")
    public String get() {
        ...
    }

    @GetMapping("/project/info")
    public void info(HttpRequest request, HttpResponse response) {
        ...
    }

    @GetMapping("/project/context")
    public Project get(Context context, @RequestParam("name") String name) {
        ...
    }

    @GetMapping("/project/body")
    public void body(RequestBody body, HttpResponse response) {
        ...
    }
}
```

方法的返回值可以有，也可以没有。

---

相关阅读推荐：

- [QueryParam](QueryParam.md)
- [FormPart](FormPart.md)
