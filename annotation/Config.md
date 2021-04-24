# Config

我们知道，每个机器上可以部署多个 Web Server，每个 WebServer 可以有不同的配置，同样的 AndServer 也提供了这样的能力，开发者可以使用`Config`注解来标记某个类是 Server 的配置类，这个配置总不能瞎写吧？所以`Config`注解的类需要实现[WebConfig](../class/WebConfig.md)接口，[WebConfig](../class/WebConfig.md)接口用来统一提供配置的 API。

```java
@Config
public class AppConfig implements WebConfig {

    @Override
    public void onConfig(Context context, Delegate delegate) {
        // 增加一个静态网站
        delegate.addWebsite(new AssetsWebsite(context, "/web"));

        // 自定义配置表单请求和文件上传的条件
        delegate.setMultipart(Multipart.newBuilder()
            .allFileMaxSize(1024 * 1024 * 20) // 单个请求上传文件总大小
            .fileMaxSize(1024 * 1024 * 5) // 单个文件的最大大小
            .maxInMemorySize(1024 * 10) // 保存上传文件时buffer大小
            .uploadTempDir(new File(context.getCacheDir(), "_server_upload_cache_")) // 文件保存目录
            .build());
    }
}
```
