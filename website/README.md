# Website

大多数人应该听说过 Tomcat、Nginx、JBoss 等服务器吧，与他们不一样的是，AndServer 没有规定静态文件（网页、文件等）应该放在哪个位置，所以我们需要通过配置的方式告诉 AndServer 静态网站的位置在哪里，我们需要使用到[WebConfig](../class/WebConfig.md)接口和[Config](../annotation/Config.md)注解。

```java
@Config
public class AppConfig implements WebConfig {

    @Override
    public void onConfig(Context context, Delegate delegate) {
        // 增加一个位于assets的web目录的网站
        delegate.addWebsite(new AssetsWebsite(context, "/web/"));

        // 增加一个位于/sdcard/Download/AndServer/目录的网站
        delegate.addWebsite(new StorageWebsite(context, "/sdcard/Download/AndServer/"));
    }
}
```

此时，我们通过浏览器就可以访问上述两个目录的所有文件了，并且通过上述配置，网站可以无限制添加多个。

> 上述`/web/`目录和`/sdcard/Download/AndServer/`目录就是网站根目录，它们的路径是`http://192.168.1.11:8080/`，例如目录下的`android.apk`文件的路径是`http://192.168.1.11:8080/android.apk`。

有人问了，如果我想在网页中浏览目录，让网页列出目录中的子目录和文件呢？同样的，添加一个网站即可：

```java
@Config
public class AppConfig implements WebConfig {

    @Override
    public void onConfig(Context context, Delegate delegate) {
        // 添加一个文件浏览器网站
        delegate.addWebsite(new FileBrowser(context, "/sdcard/"));
    }
}
```

此时，通过浏览器打开网站根目录，就可以看到文件浏览器啦。
