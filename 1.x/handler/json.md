# 返回JSON

返回JSON就比较简单了，其实JSON就是把实体对象转为JSON字符串发送给客户端，归根结底就是发送字符串。

这里我们只要把实体对象转为JSON字符串解决即可：
```java
public class LoginHandler extends SimpleRequestHandler {

    @Override
    public View handle(HttpRequest request) throws HttpException, IOException {
        User user = new User();
        user.setName("林妹妹");
        user.setSex("女");

        String json = JSON.toJSONString(user);
        return new OkView(json);
    }
}
```