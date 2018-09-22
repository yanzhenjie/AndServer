# RestController

Controller这个单词的愿意是控制器或者调节器，在开发中我们习惯性的把它叫做控制器。在AndServer中有两种控制器，一种是添加了`Controller`注解的控制器，另一种是添加了`RestController`注解的控制器。开发者万不可将此二注解与Controller的含义混淆了，例如我们都是开发者，但是分为Java开发者、Python开发者、C开发者一样，所以程序中有Controller，但是加个`Controller`注解和`RestController`就表示它们是不同功能的控制器。

`Controller`注解和`RestController`注解只能添加到类上，一般用作Http Api的分类，例如`UserController`用来提供处理用户的相关Http Api，`ShopController`用来提供处理店铺的相关Http Api。

## 作用
添加了`RestController`注解的类中的方法拥有将返回值直接输出到客户端的能力。

添加了`Controller`注解的控制器中的方法的返回值经过`ViewResolver`分析，如果是[ResponseBody](ResponseBody.md)则会直接写出到客户端、如果是其它类型的数据会先经过[MessageConverter](../class/MessageConverter.md)转化成`ResponseBody`再输出到客户端，如果开发者没有提供`MessageConverter`怎会将返回值`toString()`后组成`StringBody`输出。

> MessageConverter非常有用，比如将客户端的参数转化为Model，将服务端的Model转化为JSON、Prorobuf等个时候输出等，具体使用方法请参考[MessageConveter](../class/MessageConverter.md)类和[Converter](Converter.md)注解。

根据字面意思`RestController`就是写RESTFUL风格的Api的，因此它更加适合输出一些JSON格式、Protubuf格式的数据。

## 返回String示例
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

> 示例中的`PostMapping`注解请参考[RequestMapping](RequestMapping.md)章节，`RequestParam`注解请参考[RequestParam](RequestParam.md)章节。

上述示例则完成了一个模拟用户登录的Http Api，假设服务器的IP地址是`192.168.1.11`，监听的端口是`8080`，此时即可通过`http://192.168.1.11:8080/user/login`方法此Http Api，需要带上`account`和`password`参数。

## 返回JSON示例
```java
@RestController
public class UserController {

    /**
     * Get user information.
     */
    @GetMapping("/user/info")
    String userList(@RequestParam("id") String id) {
        User user = new User();
        user.setId(id);
        user.setName("AndServer");
        return JSON.toJSONString(user);
    }

    /**
     * Get user list.
     */
    @GetMapping("/user/list")
    String userList() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = new User();
            user.setName("User" + i);
            users.add(user);
        }
        return JSON.toJSONString(users);
    }

    /**
     * Get user list.
     */
    @GetMapping("/user/get")
    JSONObject userList() {
        JSONObject jsonObj = ...;
        return jsonObj;
    }
}
```

上述代码中第一个方法是用户详情Http Api，第二个方法用户列表Http Api，数据格式都为JSON，示例中是由开发者手动把数据转为JSON的，略嫌麻烦。

## 返回Model示例
```java
@RestController
public class UserController {

    /**
     * Get user information.
     */
    @GetMapping("/user/info")
    User userList(@RequestParam("id") String id) {
        User user = new User();
        user.setId(id);
        user.setName("AndServer");
        return user;
    }

    /**
     * Get user list.
     */
    @GetMapping("/user/list")
    List<User> userList() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = new User();
            user.setName("User" + i);
            users.add(user);
        }
        return users;
    }
}
```

> 直接返回Model需要开发者提供`MessageConverter`来做数据转换，否则返回出去的Model将会被`toString()`后当作`String`输出，具体使用方法请参考[Converter](Converter.md)注解和[MessageConverter](../class/MessageConverter.md)类。

## 返回ResponseBody示例
事实上，以上示例中的返回值，最后无不被包装为[ResponseBody](../class/ResponseBody.md)后发送出去，因此如果开发者返回`ResponseBody`将被直接发送到客户端而不经过`MessageConverter`，因此我们可以直接返回`ResponseBody`：
```java
@Controller
public class ProjectController {

    @GetMapping("/project/info")
    public void info() {
        String context = ...;
        return new StringBody(context);
    }
}
```

更多使用方法请参考[ResponseBody](../class/ResponseBody.md)类。

## 无返回值示例
开发者也不可以不写返回值，直接操作`HttpRequest`和`HttpResponse`：
```java
@Controller
public class ProjectController {

    @GetMapping("/project/get")
    public void get() {
        ...
    }

    @GetMapping("/project/info")
    public void info(HttpRequest request, HttpResponse response) {
        ...
    }
}
```

AndServer支持一些方法参数是不需注解，直接写上就可以获取到，支持不用注解的参数有`Context`、`HttpRequest`、`HttpResponse`、`RequestBody`：
```java
@Controller
public class ProjectController {

    @GetMapping("/project/context")
    public void get(Context context, @RequestParam("name") String name) {
        ...
    }

    @GetMapping("/project/body")
    public void body(RequestBody body, HttpResponse response) {
        ...
    }
}
```

直接操作`HttpResponse`时可能会涉及到设置[ResponseBody](../class/ResponseBody.md)。
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

----

相关阅读推荐：  
* [Controller](Controller.md)
* [RequestMapping](RequestMapping.md)
* [ResponseBody](ResponseBody.md)