# Website

本文主要介绍`Website`注解的使用方法，开发者不可将`Website`注解和[Website](../class/Website.md)类混淆。

在AndServer中`Website`注解用来标记某个类是[Website](../class/Website.md)的子类，并且作为静态网站参与到`AndServer`的运行中。

`Website`注解可以添加到多个类，所有添加了`Website`注解的类都将参与程序运行。

## 示例
```java
@Website
public class AppWebsite extends Website {

    @Override
    public boolean intercept(HttpRequest request) {
        return false;
    }

    @Override
    public ResponseBody getBody(HttpRequest request) throws IOException {
        return null;
    }
}
```

> 注意，添加`Website`注解的类必需是[Website](../class/Website.md)的子类，否则编译时将不通过。