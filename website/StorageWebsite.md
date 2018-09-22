# StorageWebsite

根据[Website](Website.me)类的介绍，`Website`需要结合[Website](../annotation/Website.md)注解使用，首先需要一个类继承`Website`类，然后在该类上加上`Website`注解即可使用，不需要其他配置。

因此如果想使用`StorageWebsite`，则可以像如下一样指定文件夹：
```java
@Website
public class InternalWebsite extends StorageWebsite {

    public InternalWebsite() {
        super("/sdcard/AndServer/web");
    }
}
```

不过这样指定SD卡的路径是非常不合理的，因此我们事先要初始化好网站的SD卡的路径：
```java
public class PathManager {

    private static PathManager sInstance;

    public static PathManager getInstance() {
        if(sInstance == null) {
            synchronized (PathManager.class) {
                if(sInstance == null) {
                    sInstance = new PathManager();
                }
            }
        }
        return mInstance;
    }

    private File mRootDir;

    private PathManager() {
        if (FileUtils.storageAvailable()) {
            mRootDir = Environment.getExternalStorageDirectory();
        } else {
            mRootDir = App.getInstance().getFilesDir();
        }
        mRootDir = new File(mRootDir, "AndServer");
        IOUtils.createFolder(mRootDir);
    }

    public String getRootDir() {
        return mRootDir.getAbsolutePath();
    }

    public String getWebDir() {
        return new File(mRootDir, "web").getAbsolutePath();
    }
}
```

此时开发者就可以将网站文件写入到这个`mRootDir`目录中了，有新文件要写入或者要删除旧文件，都是很方便的。

因此本文开头的示例将变成：
```java
@Website
public class InternalWebsite extends StorageWebsite {

    public InternalWebsite() {
        super(PathManager.getInstance().getWebDir());
    }
}
```

另外开发者也可以指定根目录和每个文件的默认首页叫什么名字：
```java
@Website
public class InternalWebsite extends AssetsWebsite {

    public InternalWebsite() {
        super(PathManager.getInstance().getWebDir(), "index.html");
    }
}
```