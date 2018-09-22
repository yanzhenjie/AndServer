# LastModified

`LastModified`应该被添加了[Controller](../annotation/Controller.md)注解或者[RestController](../annotation/RestController.md)注解的类实现。

## 示例
```java
@RestController
public class CacheController implements LastModified {

    @GetMapping("/category/list")
    public void categoryList() {
        ...
    }

    @Override
    public long getLastModified(HttpRequest request) throws IOException {
        String httpPath = request.getPath();
        ...
    }
}
```
实现了`LastModified`的Controller中的Http Api被调用时，Controller中的`getLastModified(HttpRequest)`方法都会被调用。

推荐开发者返回这个Http Api返回数据被修改时的时间。