# 自定义

自定义异常解决者可以实现`ExceptionResolver`接口或者继承`SimpleExceptionResolver`类。  

为了方便，我们这里以继承`SimpleExceptionResolver`类为例。  

如果开发者需要自定义异常解决者，那么想必肯定是自定义了异常了，所以我们先定义一个异常的基类：
```java
public class MyException extends HttpException {
    ...
}
```

定义异常解决者
```java
public class MyResolver extends SimpleExceptionResolver {

	@ovrride
    protected View resolveException(Exception e) {
        if (e instanceof MyException) {
            MyException exception = (MyException) e;

            // 按照自己的逻辑处理后返回。
            return ...;
        }

		// 其它未知异常处理。
        String message = String.format("Server error occurred:\n%1$s", e.getMessage());
        HttpEntity httpEntity = new StringEntity(message, ContentType.TEXT_PLAIN);
        return new View(500, httpEntity);
    }
}
```

## 建议
一般服务器接口会统一返回数据格式，所以我们这里可以把`Exception`转为JSON返回给客户端。

定义异常:
```java
public class MyException extends HttpException {
    private int code;
    private String message;

    public MyException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
```

定义异常解决者：
```java
public class MyResolver extends SimpleExceptionResolver {

    @ovrride
    protected View resolveException(Exception e) {
        if (e instanceof MyException) {
            MyException exception = (MyException) e;

            String json = JSON.toJSONString(exception);
            return new View(exception.getCode(), json);
        }

        // 其它未知异常处理。
        String message = String.format("服务器发生异常:\n%1$s", e.getMessage());
        MyException exception = new MyException(500, message);
        return new View(exception.getCode(), json);
    }
}
```

这样处理后，当服务器抛出异常后，客户端接收到的数据将是JSON格式：
```json
{
    "code":"401",
    "message":"请您先登录"
}
```

未知异常：
```json
{
    "code":"500",
    "message":"服务器发生异常:/user/admin"
}
```