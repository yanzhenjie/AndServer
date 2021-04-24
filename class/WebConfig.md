# WebConfig

在 AndServer 中[Config](../annotation/Config.md)注解用来标记某个类是 Server 的配置类，而`WebConfig`用来规范配置类的规则，相当于提供统一的 API 来供开发者进行 Server 配置，`WebConfig`接口需要配合[Config](../annotation/Config.md)注解使用。

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
