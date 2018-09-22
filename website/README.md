# Website

Website的原意是网站，它本身可以做一些动态网站和静态网站的部署，但是AndServer它更方便做一些静态页面的部署、文件下载等功能。

AndServer提供了`Website`几个默认实现：
* [AssetsWebsite](AssetsWebsite.md)，基于`asserts`中任意文件夹的网站，支持缓存
* [StorageWebsite](StorageWebsite.md)，基于SD卡任意文件夹的网站，支持缓存，支持热插拔
* [FileBrowser](FileBrowser.md)，基于SD卡的文件浏览器

`Website`需要结合[Website](../annotation/Website.md)注解使用，首先需要一个类继承`Website`类，然后在该类上加上`Website`注解即可使用，不需要其他配置。

> **注意**：实现类必须提供一个无参构造方法供AndServer调用，否则编译不通过。

示例请参考源码中`AssetsWebsite`、`StorageWebsite`、`FileBrowser`的实现。