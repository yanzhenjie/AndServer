# 基于Assets

`AssetsWebsite`可以部署`assets`中的内容为网站，只要传入你要部署的网站在`assets`中的路径即可。比如我的网站放在`/assets/web/webiste`目录下，那么我可以这么写：
```java
AssetManager assetsManager = context.getAssets();
Website website = new AssetsWebsite(assetsManager, "assets/web/website");
```

这里值得注意的是，因为`assets`比较特殊，所以在路径的前后不要带上`/`，不然将无法解析。  

例如`/assets/web`下的网站：`new AssetsWebsite(getAssets(), "assets/web");`，例如`/assets`下的网站：`new AssetsWebsite(getAssets(), "assets");`。

> **注意**：`AssetsWebsite`不支持热插拔。

## 网站首页和index.html
传入的目录中如果存在`index.html`将作为网站的首页，每一个目录中如果存在`index.html`，将作为这个目录路径的默认页面。

例如我们把`assets`根目录作为网站部署目录：
```
 assets
 ├─index.html
 ├─user
 │  ├─index.html
 │  └─admin.html
 ├─shop
 │  └─index.html
 └─items
    └─index.html
```

假设我们在某个手机上部署`new AssetsWebsite(getAssets(), "assets");`为网站，这个手机的本机局域网IP是`192.168.1.11`，我们指定AndServer监听的端口是8080。  

那么对于上方这个结构，默认首页是`http://192.168.1.11:8080`或者`http://192.168.1.11:8080/index.html`，那么访问到的资源就是`/assets/index.html`。  

如果有人访问`http://192.168.1.11:8080/user`，那么他访问到的资源是`/assets/user/index.html`；同样的这个用户访问`http://192.168.1.11:8080`也可以访问到`/assets/user/index.html`这个资源。对于`/assets/user/admin.html`必须明确指定访问资源的路径`http://192.168.1.11:8080/`。对于`/assets/shop`目录和`/assets/items`目录同上。