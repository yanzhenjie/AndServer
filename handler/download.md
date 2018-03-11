# 下载文件

如果你还没来得及看了前两节对`RequestHandler`的讲解，那么你应该先看前两节。

```java
public class FileHandler extends SimpleRequestHandler {

    @Override
    public View handle(HttpRequest request) throws HttpException, IOException {
    	// 为了示例，创建一个临时文件。
        File file = File.createTempFile("AndServer", ".txt", App.get().getCacheDir());
        OutputStream outputStream = new FileOutputStream(file);
        IOUtils.write("天上掉下个林妹妹。", outputStream, Charset.defaultCharset());

        HttpEntity httpEntity = new FileEntity(file);
		View view = new View(200, httpEntity);
        view.addHeader("Content-Disposition", "attachment;filename=AndServer.txt");
		return view;
    }
}
```

这里其实和[返回图片](./image.md)章节没啥却别，唯一值得注意的是：
```java
view.addHeader("Content-Disposition", "attachment;filename=AndServer.txt");
```

这里我们添加了一个`Content-Disposition`的响应头，`attachment`的意思是告诉浏览器，这个文件应该被下载，`filename=AndServer.txt`的意思是告诉浏览器，这个文件默认被命名为`AndServer.txt`。

如果不添加上述相应头，比如没有`attachment`大部分浏览器会直接打开自己可以直接打开的文件，没有`filename=AndServer.txt`浏览器将会用自己的算法给文件取一个默认名称。