# HandlerInterceptor

`HandlerInterceptor`用来拦截客户端对服务端所有的Http Api的请求，如果是不存在的Http Api是不会经过`HandlerInterceptor`处理的。

`HandlerInterceptor`需要结合[Interceptor](../annotation/Interceptor.md)注解使用，首先需要一个类实现`HandlerInterceptor`接口，然后在该类上加上`Interceptor`注解即可使用，不需要其他配置。

> **注意**：实现类必须提供一个无参构造方法供AndServer调用，否则编译不通过。

```java
public interface HandlerInterceptor {

    /**
     * Intercept the execution of a handler.
     *
     * @param request current request.
     * @param response current response.
     * @param handler the corresponding handler of the current request.
     *
     * @return true if the interceptor has processed the request and responded.
     */
    boolean onIntercept(HttpRequest request, HttpResponse response,
        RequestHandler handler) throws Exception;
}
```

`onIntercept()`方法的第三个参数是Http Api方法的Java对象表现形式，开发者可以从`RequestHandler`获取相关注解信息。

此方法的返回值为`false`时，不会对程序的运行产生任何影响；返回值为`true`时，表示开发者拦截了该请求，该请求将终止继续分发，Server将发送此时的响应码和响应数据到客户端，默认响应码为200，响应数据默认为空。

最佳实践是结合[Addition](../annotation/Addition.md)注解和[Addition](Addition.md)类一起使用。

## 示例，打印客户端参数
```java
@Interceptor
public class LoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean onIntercept(HttpRequest request, HttpResponse respons,
        RequestHandler handler) {
        String httpPath = request.getPath();
        HttpMethod method = request.getMethod();
        MultiValueMap<String, String> valueMap = request.getParameter();
        Logger.i("Path: " + httpPath);
        Logger.i("Method: " + method.value());
        Logger.i("Param: " + JsonUtils.toJsonString(valueMap));
        return false;
    }
}
```

## 示例，结合Addition拦截用户登录
例如，有一个Http Api需要用户登录后使用，首先我们提供一个登录的Http Api：
```java
@RestController
@RequestMapping(path = "/user")
class UserController {

    @PostMapping(path = "/login")
    String login(@RequestParam("account") String account, 
        @RequestParam("password") String password) {
        Session session = request.getValidSession();
        session.setAttribute("isLogin", true);
        return "Login successful.";
    }
}
```

在上述登录的Http Api中，我们把用户登录标记保存在Session中，然后我们提供一个获取用户信息的Http Api，但是要求用户登录后才能访问：
```java
@RestController
class UserController {

    @Addition(stringType = "needLogin", booleanType = true)
    @GetMapping(path = "/get")
    UserInfo userInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("123");
        userInfo.setUserName("AndServer");
        return userInfo;
    }
}
```

在上述获取用户信息的Http Api上，我们添加了一个[Addition](../annotation/Addition.md)注解，并且赋值了一个`String`参数为`needLogin`和一个`boolean`参数为`true`，我们约定这样表示此Http Api需要登录后才能访问，于是我们在拦截器中这样做：
```java
@Interceptor
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean onIntercept(HttpRequest request, HttpResponse response,
        RequestHandler handler) {
        if (handler instanceof MethodHandler) {
            MethodHandler methodHandler = (MethodHandler)handler;
            Addition addition = methodHandler.getAddition();
            if (!isLogin(request, addition)) {
                throw new NeedLoginException(401, "You are not logged in yet.");
            }
        }
        return false;
    }

    private boolean isLogin(HttpRequest request, Addition addition) {
        if (isNeedLogin(addition)) {
            Session session = request.getSession();
            if (session != null) {
                Object o = session.getAttribute("isLogin");
                return o != null && (o instanceof Boolean) && ((boolean)o);
            }
            return false;
        }
        return true;
    }

    private boolean isNeedLogin(Addition addition) {
        if (addition == null) return false;

        String[] stringType = addition.getStringType();
        if (ArrayUtils.isEmpty(stringType)) return false;

        boolean[] booleanType = addition.getBooleanType();
        if (ArrayUtils.isEmpty(booleanType)) return false;
        return stringType[0].equalsIgnoreCase("login") && booleanType[0];
    }
}
```

> **注意**：必须先判断`RequestHandler`是不是`MethodHandler`，只有`MethodHandler`才是`Controller`中的方法，其它可能是网页、CSS文件等。

如上所示，就完成了判断哪个Http Api需要登录后才能访问的逻辑，在用户没有登录时访问需要登陆后才能访问的方法时会抛出`NeedLoginException`异常。

在上述示例中，第一个方法是拦截器的方法，第二个方法用来判断用户是否登录，第三个方法用来判断当前请求的Http Api是否需要登录后才能调用。