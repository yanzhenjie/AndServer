# ResponseBody

本文主要介绍`ResponseBody`注解的使用方法，开发者不可将`ResponseBody`注解和[ResponseBody](../class/ResponseBody.md)注解混淆。

`ResponseBody`注解用于使用了[Controller](Controller.md)注解的类中的方法上，它的作用是让使用了[Controller](Controller.md)注解的类中的方法像使用了[RestController](RestController.md)注解的类中的方法一样，直接将返回值作为响应发送给客户端。

## 示例
在[Controller](Controller.md)章节中介绍了以下示例：
```java
@Controller
public class PageController {

    @GetMapping("/")
    public String forward() {
        return "forward:/index.html";
    }

    @GetMapping("/welcome")
    public String redirect() {
        return "redirect:/index.html";
    }
}
```

如果我们想在上述示例中的返回值当作响应消息返回，而不是做转发和重定向时，就需要`ResponseBody`注解了：
```java
@Controller
public class PageController {

    @GetMapping("/")
    public String forward() {
        return "forward:/index.html";
    }

    @ResponseBody
    @GetMapping("/welcome")
    public String redirect() {
        return "redirect:/index.html";
    }
}
```

如上所示，第一个方法仍然做服务器内部转发，第二个方法会直接输出`redirect:/index.html`到客户端。

另外，如果使用`Controller`注解加`ResponseBody`注解将达到`RestController`的效果：
```java
@ResponseBody
@Controller
public class PageController {

    @GetMapping("/")
    public String forward() {
        return "forward:/index.html";
    }

    @GetMapping("/welcome")
    public String redirect() {
        return "redirect:/index.html";
    }
}
```

如上所示，两个方法的返回值都将会作为响应消息输出到客户端。

> **注意**：如果开发者使用了[MessageConverter](../class/MessageConverter.md)，那么有`ResponseBody`注解的返回值还会经过`MessageConverter`转换。

----

相关阅读推荐：  
[MessageConverter](../class/MessageConverter.md)  
[Controller](Controller.md)  
[RestController](RestController.md)  