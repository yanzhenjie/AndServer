# ExeceptionResolver

`ExeceptionResolver`用来处理所有请求Http Api时发生的异常，默认情况下会输出异常的`Message`到客户端。

`ExeceptionResolver`需要结合[Resolver](../annotation/Resolver.md)注解使用，首先需要一个类实现`ExeceptionResolver`接口，然后在该类上加上`Resolver`注解即可使用，不需要其他配置。

> **注意**：实现类必须提供一个无参构造方法供AndServer调用，否则编译不通过。

```java
public interface ExceptionResolver {

    /**
     * Resolve exceptions that occur in the program, replacing the default 
     * outputinformation for the exception.
     *
     * @param request current request.
     * @param response current response.
     * @param e an exception occurred in the program.
     */
    void onResolve(HttpRequest request, HttpResponse response, Throwable e);
}
```

`onResolve()`方法的第三个参数表示任何异常，AndServer框架中抛出的异常几乎全部继承`BasicException`异常，其中包含了发生异常时对应的不同的`StatusCode`。

## 示例
本示例和[MessageConverter](MessageConverter.md)的返回数据一样为JSON格式：
```java
@Resolver
public class AppExceptionResolver implements ExceptionResolver {

    @Override
    public void onResolve(HttpRequest request, HttpResponse response,
        Throwable e) {
        if (e instanceof BasicException) {
            BasicException ex = (BasicException)e;
            response.setStatus(ex.getStatusCode());
        } else {
            response.setStatus(StatusCode.SC_INTERNAL_SERVER_ERROR);
        }

        ReturnData returned = new ReturnData();
        returned.setSuccess(false);
        returned.setMessage(e.getMessage());

        String jsonString = JsonUtils.toJsonString(returned);
        response.setBody(new JsonBody(jsonString));
    }
}
```

上述示例中，我们先判断了异常是不是`BasicException`本身或者子类，如果是则设置响应码为异常中自定义的响应码，如果不是则设置响应码为`500`。

## 架构建议
推荐开发者自定义一些业务异常，并继承`BasicException`。

例如我们定义一个权限异常，让一部分用户访问某Http Api时抛出此异常告诉客户端为什么不能访问，下面是自定义异常：
```java
public class PermissionException extends BasicException {

    private static final String MESSAGE = "Access is denied because your
        permissions are insufficient.";

    public PermissionException() {
        super(StatusCode.SC_FORBIDDEN, MESSAGE);
    }

    public PermissionException(Throwable cause) {
        super(StatusCode.SC_FORBIDDEN, MESSAGE, cause);
    }
}
```

下面是Http Api的实现：
```java
@RestController
public class UserController {

    @DeteleMapping("/user/{userId}")
    public boolean deleteUser(@PathVariable("userId") String id) {
        if(!"0".equals(id)) {
            return true;
        }
        throw new PermissionException();
    }
}
```

上述示例表示，如果要删除的ID为 **`0`** 则抛出权限不足异常。