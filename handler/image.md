# 返回图片

返回图片的方式有很多种，最终我们都要返回`HttpEntity`这个对象，所以图片可以来自Assets，drawable、SD卡，下面是一个来自SD卡的图片示例：
```
public class ImageHandler extends SimpleRequestHandler {

    private File mFile = new File(Environment.getExternalStorageDirectory(), "xxx.jpg");

    @Override
    protected View handle(HttpRequest request) throws HttpException, IOException {
        HttpEntity httpEntity = new FileEntity(mFile);
        return new View(200, httpEntity);
    }
}
```

这里只给出了关键代码，正式开发中当然不应该如此简单，但是上述代码足以辅助开发者理解`RequestHandler`该如何实现了。