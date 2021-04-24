# Controller

Controller 这个单词的愿意是控制器或者调节器，在开发中我们习惯性的把它叫做控制器。在 AndServer 中有两种控制器，一种是添加了`Controller`注解的控制器，另一种是添加了`RestController`注解的控制器。开发者万不可将此二注解与 Controller 的含义混淆了，例如我们都是开发者，但是分为 Java 开发者、Python 开发者、C 开发者一样，所以程序中有 Controller，但是加个`Controller`注解和`RestController`就表示它们是不同功能的控制器。

`Controller`注解和`RestController`注解只能添加到类上，一般用作 HTTP API 的分类，例如`UserController`用来提供处理用户的相关 HTTP API，`ShopController`用来提供处理店铺的相关 HTTP API。

## 作用

添加了`Controller`注解的类中的方法，拥有将请求转发、重定向、~~使用 Model 渲染模板~~等能力。

> AndServer 还没有实现模板引擎，也没有接入第三方模板引擎，因此上方第三个功能暂时不可用，这里也不做介绍。

添加了`Controller`注解的控制器中的方法的返回值不会被直接输出到客户端，返回值经过`ViewResolver`分析，如果符合转发、重定向语法，将被正常处理，如果不能被正常处理，则抛出`ServerInternalException`异常。

## 返回值

添加了`Controller`注解的控制器中的方法的返回值分为三种：

1. `/static/shop`， 第一种是符合`path`语法的，属于服务器内部请求转发，遇到此返回值，会在返回值后面添加`.html`作为新`path`，然后在所有的控制器和网站中搜索这个新`path`，如果找不到则抛出`NotFoundException`异常。
2. `forward:/project/info`，第二种是符合`forward:{path}`语法的，属于服务器内部请求转发，遇到此返回值，会拿出后面的`path`，然后在所有的控制器和网站中搜索这个`path`，如果找不到则抛出`NotFoundException`异常。
3. `redirect:/user/info`，第三种是符合`redirect:path`语法的，属于客户端请求重定向，遇到此返回值，会设置`Response`的响应码为`302（Found）`，设置`Response`的`Location`头为这个`path`。

> 异常处理请参考[ExceptionResolver](../class/ExceptionResolver.md)。

## 示例

在服务器中可能有一些页面并不存在，但是我们不能保证用户不访问这个地址，因此我们可以做一些配置，把这些请求转发到其他 HTTP API 或者做重定向。

```java
@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}
```

> 示例中的`GetMapping`注解请参考[RequestMapping](RequestMapping.md)章节。

在上述示例中，类名和方法名在 Java 允许的范围内随便取，只要添加相应的注解即写好了一个 HTTP API，开发者不需要做任何注册、配置，只需要[启动服务器](/server/README.md)就可以访问该 HTTP API 了。当然，因为我们还没有添加`/index.html`这个地址，所以此时如果访问会响应 404，错误信息应该是`The resource [/index.html] is not found.`，不过也说明了服务器内转发是成功的。

为了帮助开发者更深刻的理解这个例子的含义，下面再举一个例子。假设服务端有一个 HTTP API 的地址是：`http://.../project/info`，我们想在用户访问`http://.../projectInfo`时转发到上一个地址：

```java
@Controller
public class ProjectController {

    @ResponseBody
    @GetMapping("/project/info")
    public String newInfo() {
        ...
    }

    @GetMapping("/projectInfo")
    public String oldInfo() {
        return "forward:/project/info";
    }
}
```

> 示例中`ResponseBody`注解能让添加了`Controller`注解的类中的方法把返回值直接输出到客户端，相当于给这个方法单独加了[RestController](RestController.md)，具体请参考[ResponseBody](ResponseBody.md)。

如果我们不想在服务器内部做转发，而是想发一个重定向响应，让客户端重新请求另一个 HTTP API，那么只需要把关键词`forward`改成`redirect`即可：

```java
@Controller
public class ProjectController {

    @ResponseBody
    @GetMapping("/project/info")
    public String newInfo() {
        ...
    }

    @GetMapping("/projectInfo")
    public String oldInfo() {
        return "forward:/project/info";
    }
}
```

## 无返回值示例

开发者也不可以不写返回值，直接操作`HttpRequest`和`HttpResponse`：

```java
@Controller
public class ProjectController {

    @GetMapping("/project/info")
    public void info(HttpRequest request, HttpResponse response) {
        ...
    }
}
```

直接操作`HttpResponse`时可能会涉及到设置[ResponseBody](ResponseBody.md)。

```java
@Controller
public class ProjectController {

    @GetMapping("/project/info")
    public void info(HttpRequest request, HttpResponse response) {
        String content = ...;
        RequestBody body = new StringBody(content);
        response.setBody(body);
    }
}
```

更多使用方法请参考[ResponseBody](../class/ResponseBody.md)类。

---

相关阅读推荐：

- [RestController](RestController.md)
- [RequestMapping](RequestMapping.md)
- [ResponseBody](ResponseBody.md)
