# AndServer
QQ交流群1：46505645  
QQ交流群2：46523908  
[群行为规范][0]  
群资源有限，请不要重复加群，谢谢。

----
##简介
　　AndServer是Android Http Server的简写，顾名思义AndServer是Android端搭建Http服务器的一个项目。
　　目的在于在Android可以很方便的搭建Http服务器，对于有为什么会在Android搭建Http服务器的同学，请移步[严振杰的CSDN博客][1]。
　　需要说明一下AndServer1.0.1是基于ApacheHttpCore的，因为Android弃用了ApacheHttpClient相关API，代码中会有启用的警告，这一点大家不要担心，AndServer已经处理过了，不会影响使用的。下面是个大概的介绍，看更详细请下载Demo查看。

##使用方法
* Eclipse使用Jar包，如果需要依赖源码，请自行下载。
> [下载Jar包][2]

* AndroidStudio使用Gradle构建添加依赖（推荐）
```groovy
compile 'com.yanzhenjie:andserver:1.0.1'
```

##实现AndServerRequestHandler接口，相当于Java的Servlet一样
　　我们每写一个服务端接口，就要一个对应的类来处理，这里要实现`AndServerRequestHandler`接口，相当于Java继承Servet一样，我们只需要处理Request，在Response中给出响应即可：
```java
public class AndServerTestHandler implements AndServerRequestHandler {
    @Override
    public void handle(HttpRequest rq, HttpResponse rp, HttpContext ct) throws HttpException, IOException {
        response.setEntity(new StringEntity("请求成功。", "utf-8"));
    }
}
```

##在AndServer上注册接口名称，并启动服务器
　　在启动的时候最好放在Service中，这里给出启动的关键代码。
```java
AndServerBuild andServerBuild = AndServerBuild.create();
andServerBuild.setPort(4477);// 指定http端口号。

// 注册接口。
andServerBuild.add("test", new AndServerTestHandler());
// 这里还可以注册很多接口。

// 启动服务器。
AndServer andServer = andServerBuild.build();
andServer.launch();
```
　　到这里就完成了，相当于写好Servlet，然后注册一下就好了。

##其他设备如何访问
　　如果是浏览器方法，和我们普通访问网站没有区别，比如访问我们上面的接口：
```html
在Android本机访问的地址就是：http://locahost:4477/test。
局域网其他设置访问地址类似：http://192.168.1.116:4477/test。
```
　　但是我们一般都是APP直接访问的，推荐[使用NoHttp][3]，NoHttp是我的另一个Http客户端的项目，和AndServer正好是相对的，一个做服务端，一个做客户端。

##停止AndServer
```java
if(andServer != null && andServer.isRunning()) {
    andServer.close();
}
```

#License
```text
Copyright 2016 Yan Zhenjie

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[0]: https://github.com/yanzhenjie/SkillGroupRule
[1]: http://blog.csdn.net/yanzhenjie1003
[2]: https://github.com/yanzhenjie/AndServer/blob/master/Jar/andserver1.0.1.jar?raw=true
[3]: https://github.com/yanzhenjie/NoHttp