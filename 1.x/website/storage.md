# 基于SD卡

`StorageWebsite`可以部署SD卡中的内容为网站，只要传入你要部署的网站在SD卡中的路径即可。比如我的网站放在`/sdcard/andserver/webiste`目录下，那么我可以这么写：
```java
Website website = new StorageWebsite("/sdcard/andserver/website");
```

这里值得注意的是因为SD卡也是磁盘，所以路径必须是绝对路劲。

> **注意**：`StorageWebsite`支持热插拔。

## 网站首页和index.html
传入的目录中如果存在`index.html`将作为网站的首页，每一个目录中如果存在`index.html`，将作为这个目录路径的默认页面。

例如我们把`/sdcard/andserver/webiste`目录作为网站部署目录：
```
...
 ├─index.html
 ├─user
 │  ├─index.html
 │  └─admin.html
 ├─shop
 │  └─index.html
 └─items
    └─index.html
```

假设我们在某个手机上部署`new StorageWebsite("/sdcard/andserver/webiste");`为网站，这个手机的本机局域网IP是`192.168.1.11`，我们指定AndServer监听的端口是8080。  

那么对于上方这个结构，默认首页是`http://192.168.1.11:8080`或者`http://192.168.1.11:8080/index.html`，那么访问到的资源就是`/assets/index.html`。  

如果有人访问`http://192.168.1.11:8080/user`，那么他访问到的资源是`/assets/user/index.html`；同样的这个用户访问`http://192.168.1.11:8080`也可以访问到`/assets/user/index.html`这个资源。对于`/assets/user/admin.html`必须明确指定访问资源的路径`http://192.168.1.11:8080/`。对于`/assets/shop`目录和`/assets/items`目录同上。