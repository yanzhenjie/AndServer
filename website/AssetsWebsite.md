# AssetsWebsite

根据[Website](Website.me)类的介绍，`Website`需要结合[Website](../annotation/Website.md)注解使用，首先需要一个类继承`Website`类，然后在该类上加上`Website`注解即可使用，不需要其他配置。

因此如果想使用`AssetsWebsite`，则可以像如下一样指定文件夹：
```java
@Website
public class InternalWebsite extends AssetsWebsite {

    public InternalWebsite() {
        super("/web");
    }
}
```

另外开发者也可以指定根目录和每个文件的默认首页叫什么名字：
```java
@Website
public class InternalWebsite extends AssetsWebsite {

    public InternalWebsite() {
        super("/web", "index.html");
    }
}
```

如上所示，`/web`表示项目下的`assets`中的`web文件夹`：
```
├─app
│   ├─src
│   │  ├─assets
│   │  │  └─web
│   │  │     ├─..
│   │  │     └─...
│   │  │  
│   │  ├─java
│   │  └─res
│   │
│   ├─build.gradle
│   └─app.iml
│
└─library
``