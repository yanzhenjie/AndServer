# FileBrowser

`FileBrowser`比较简单，它可以以列表的形式展示开发者指定目录的文件和目录，并可以点击下载列表中的文件和查看列表中的目录的内容。

开发者只需要指定目录即可：
```java
@Website
public class SimpleBrowser extends FileBrowser {

    public SimpleBrowser() {
        super(PathManager.getInstance().getWebDir());
    }
}
```

`PathManager`请参考[StorageWebsite](StorageWebsite.md)。