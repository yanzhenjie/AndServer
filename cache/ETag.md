# ETag

`ETag`应该被添加了[Controller](../annotation/Controller.md)注解或者[RestController](../annotation/RestController.md)注解的类实现。

## 示例
```java
@RestController
public class CacheController implements ETag {

    @GetMapping("/category/list")
    public void categoryList() {
        ...
    }

    @Override
    public String getETag(HttpRequest request) throws IOException {
        String httpPath = request.getPath();
        ...
    }
}
```
实现了`ETag`的Controller中的Http Api被调用时，Controller中的`getETag(HttpRequest)`方法都会被调用。

推荐开发者返回这个Http Api返回数据的唯一值作为ETag，当数据变化时，唯一值应该发生变化，所以MD5值非常合适，如果开发者如果有更好的算法请使用自己的算法。