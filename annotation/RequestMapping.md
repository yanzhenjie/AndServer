# RequestMapping

`RequestMapping`在AndServer是扮演的角色非常重要，它可以规定一个Http Api请求路径、请求方法、参数校验、请求头校验、Accept、ContentType等重要规则。

`RequestMapping`的变体有`GetMapping`、`PostMapping`、`PutMapping`、`PatchMapping`、`DeleteMapping`，`GetMapping`表示仅支持`GET`请求、`PostMapping`表示只支持`POST`请求，以此类推，下文将统称为`RequestMapping`。

`RequestMapping`既可以用在类上，也可以用在方法上。当它仅仅用在类上时没有任何作用，当使用它的类中有方法也使用了它时，相当于把用在类上的`RequestMapping`的参数合并在方法的`RequestMapping`上。

> 使用`RequestMapping`注解的方法，其所在的类必须使用[Controller](Controller.md)注解或者[RestController](RestController.md)注解才能生效，否则是无意义的。

----

文本目录：
- [path示例](#path示例)
- [method示例](#method示例)
- [param示例](#param示例)
- [header示例](#header示例)
- [consume示例](#consume示例)
- [produce示例](#produce示例)

## path示例
```java
@RestController
public class UserController {

    @GetMapping("/user/info")
    void info() {
        ...
    }

    @PostMapping("/user/register")
    void register() {
        ...
    }
}
```

假设服务器的IP地址是`192.168.1.11`，监听的端口是`8080`，上述示例的访问地址则是：`http://192.168.1.11:8080/user/info`和`http://192.168.1.11:8080/user/register`。

如上所示，一个`Controller`中一般会写多个属于同一个模块的Http Api，则每个Http Api的路径都会以`/user`开头，这样会显得很麻烦，因此我们可以在类上添加`RequestMapping`来简化它：
```java
@RequestMapping("/user")
@RestController
public class UserController {

    @GetMapping("/info")
    void info() {
        ...
    }

    @PostMapping("/register")
    void register() {
        ...
    }
}
```

以上两个示例是等价的。

另外同一个方法可以拥有多个`path`：
```java
@RestController
public class UserController {

    @PostMapping(path = {"/user/register", "/user/create"})
    void register() {
        ...
    }
}
```
或者：
```java
@RequestMapping("/user")
@RestController
public class UserController {

    @PostMapping(path = {"/register", "/create"})
    void register() {
        ...
    }
}
```

> 如果客户端请求的地址在服务器上不存在，将会抛出`NotFoundException`异常，异常处理请参考[ExceptionResolver](../class/ExceptionResolver.md)。

## method示例
除了使用`PostMapping`这样明确请求方法的注解之外，还可以使用`RequestMapping`指定请求方法，而且可以支持一个Http Api支持多种请求方法：
```java
@RestController
public class UserController {

    @RequestMapping(path = "/info"
        method = RequestMethod.GET)
    void info() {
        ...
    }

    @RequestMapping(path = "/register"
        method = {RequestMethod.GET, RequestMethod.POST})
    void register() {
        ...
    }
}
```

也可以由方法所在的类来指定它的请求方法是什么：
```java
@RequestMapping(method = RequestMethod.GET)
@RestController
public class UserController {

    @RequestMapping(path = "/info")
    void info() {
        ...
    }

    @RequestMapping(path = "/register")
    void register() {
        ...
    }

    @DeleteMapping("/delete")
    void delete() {
        ...
    }
}
```

如上示例，前两个方法没有指定请求方法，但是由它们所在的类指定了它们的请求方法是`GET`，第三个方法同事支持`GET`请求方法和`DELETE`请求方法。

> 如果客户端请求的地址不支持客户端使用的请求方法，将会抛出`MethodNotSupportException`异常，异常处理请参考[ExceptionResolver](../class/ExceptionResolver.md)。

## param示例
为了方便展开说明，我们先看一段示例：
```java
@RestController
public class UserController {

    @GetMapping(path = "/info", param = "name=123")
    void info() {
        ...
    }
}
```

示例中的代码中`param="name=123"`是`param`的其中一个语法，`param`有四种语法：
1. `key=value`，规定了某key必须等于某value，例如：`param = "name=123"`。
2. `key!=value`，规定了某key必须不等于某value，例如：`param = "name!=123"`。
3. `key`，规定了参数中必须有某key，且值不能为空，例如：`param = "name"`。
4. `!key`，规定了参数中必须不能由某key，例如：`param = "!name"`。

上述语法是可以混合使用的：
```java
@RestController
public class UserController {

    @GetMapping(path = "/info", param = {"name!=123", "password"})
    void info() {
        ...
    }
}
```

上述示例中，`param`的含义是不能包含`name=123`这一参数键值对，但是必须包含`password`参数。

> 如果客户端的请求违反约束，则会抛出`ParamValidateException`异常，异常处理请参考[ExceptionResolver](../class/ExceptionResolver.md)。

## header示例
`header`的使用方法和`param`完全一致，只是它用来规定请求头，而`param`用来规定请求参数。

> 如果客户端的请求违反约束，则会抛出`HeaderValidateException`异常，异常处理请参考[ExceptionResolver](../class/ExceptionResolver.md)。

## consume示例
Consume单词的字面意思是消耗、消费，在转换到程序中来就是说：*你能消费什么？你能处理什么？*因此它适合用于校验客户端的`Content-Type`头，Contnet-Type的意思是内容类型，因此`consume`的含义就是*能消费什么内容*了。

`consume`的语法有两种：
1. `application/json`，规定了客户端提交的`Content-Type`须是JSON格式。
2. `!applicatioln/xml`，规定了客户端提交的`Content-Type`不能是XML格式。

同时它支持`*`，例如`application/*`则**支持**客户端提交`application/json`和`application/zip`等类型数据，例如`!text/*`则**不支持**客户端提交`text/plain`和`text/xml`等类型数据。

示例：
```java
@RestController
public class UserController {

    @PostMapping(path = "/info", consume = "application/json")
    void info() {
        ...
    }

    @PostMapping(path = "/create", consume = {"!text/*", "!application/xml"})
    void create() {
        ...
    }
}
```

上述示例中，第一个方法表示客户端请求时的`Content-Type`只能是`application/json`，比如客户端是`application/json; charset=utf-8`也是允许通过的；第二个示例表示客户端请求时`Content-Type`不能是`text/plain`、`text/xml`和`text/css`等，也不能是`application/xml`。

> 如果客户端的请求违反约束，则会抛出`ContentNotSupportedException`异常，异常处理请参考[ExceptionResolver](../class/ExceptionResolver.md)。

## produce示例
`produce`的语法和`consume`完全一致，只是它用来规定客户端的`Accept`头，而`consume`用来规定客户端的`Content-Type`头。

与`consume`不同的是，它在服务端不支持`*`，但是它支持客户端的`*`。因为对于`Content-Type`来说，客户端上行或者服务端下行时内容类型都是明确的，因此服务端校验`Content-Type`时可以用非明确的值去做包含。而对于`Accept`，因为不知道服务端下发的`Content-Type`，所以客户端可以用非明确的值做包含，因此客户端的`Accept`值有可能是`*/*`。

例如，规定客户端能接受JSON：
```java
@RestController
public class UserController {

    @PostMapping(path = "/info", produce = "application/json")
    String info() {
        ...
    }
}
```
上述示例中，如果客户端的`Accept`是`*/*`或者`application/json`就可以校验通过。

例如，规定能接受JSON的客户端校验不通过：
```java
@RestController
public class UserController {

    @PostMapping(path = "/info", produce = "!application/json")
    String info() {
        ...
    }
}
```
上述示例中，如果客户端的`Accept`是`*/*`或者`application/json`是不能通过校验的。

> 如果客户端的请求违反约束，则会抛出`ContentNotAcceptableException`异常，异常处理请参考[ExceptionResolver](../class/ExceptionResolver.md)。

**特别注意**，`produce`的值会作为服务端响应消息的`Content-Type`发送到客户端。

如下所示，`produce`不会作为`Content-Type`被发送客户端：
```java
@RestController
public class UserController {

    @PostMapping(path = "/info", produce = "!application/json")
    String info() {
        ...
    }
}
```

如下所示，`produce`会作为`Content-Type`被发送客户端：
```java
@RestController
public class UserController {

    @PostMapping(path = "/info", produce = "application/json; charset=utf-8")
    String info() {
        ...
    }
}
```

----

相关阅读推荐：  
* [Controller](Controller.md)  
* [RestController](RestController.md)
* [RequestParam](RequestParam.md)