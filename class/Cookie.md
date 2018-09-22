# Cookie

`Cookie`可以用来辨别用户身份，一般会进行加密后发送给客户端，客户端会保存在本地磁盘上。

## 发送Cookie
```java
@RestController
class UserController {

    @PostMapping("/login")
    void login(@RequestParam("account") String account, 
        @RequestParam("password") String password, 
        HttpResponse response) {
        ...

        Cookie cookie = new Cookie("account", account + "=" + password);
        response.addCookie(cookie);
    }
}
```

Cookie还有以下方法可以设置属性：
* setHttpOnly()，设置是否仅支持http
* setPath()，设置可以使用此Cookie的path
* setComment()，设置此Cookie的描述
* setDomain()，设置此Cookie的域
* setMaxAge()，设置此Cookie的过期时间
* setSecure()，此时此Cookie是否该使用安全协议发送，例如SSL
* setValue()，设置此Cookie的值
* setVersion()，设置Cookie版本

更多关于Cookie的知识，请参考以下资料：
* [RFC2019](https://www.ietf.org/rfc/rfc2109.txt)
* [RFC6013](https://www.ietf.org/rfc/rfc6013.txt)
* [RFC6265](https://www.ietf.org/rfc/rfc6265.txt)
* [HTTP cookies - HTTP | MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies)

## 获取Cookie
获取Cookie有两种方式，第一种是使用[CookieValue](../annotation/CookieValue.md)注解，第二种是通过`HttpRequest`对象，本文仅仅介绍第二种方式：

```java
@RestController
class UserController {

    @PostMapping("/user/{id}")
    void get(@PathVariable("id") String id, 
        HttpRequest request) {
        Cookie cookie = request.getCookie("account");
        ...
    }
}
```

----

相关阅读推荐：  
* [CookieValue](../annotation/CookieValue.md)