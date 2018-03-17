# AndServer
![Logo](/image/logo.png)

Android platform web server and development framework.  

* Dynamic website deployment.
* Static website deployment.
* Http api deployment.

Internal realization of the website based on Asset and external memory card. The contents of the external memory card support hot swap. Web site can support Http cache protocol, used to improve server performance.  

Some of its features are learned from SpringMVC, It is perfect for developers who are specializing in android from java, which will be easier to understand how AndServer works.  

It's these characteristics may be you like: Https, interceptors, filters, websites, file browser, http cache protocol, exception resolver, file upload and download.  

For usage and other information see [Document](http://yanzhenjie.github.io/AndServer).  

## Download
* Gradle
```groovy
implementation 'com.yanzhenjie:andserver:1.1.3'
```

* Maven
```xml
<dependency>
  <groupId>com.yanzhenjie</groupId>
  <artifactId>andserver</artifactId>
  <version>1.1.3</version>
</dependency>
```

* Jar
It's in Jar folder of [release package](https://github.com/yanzhenjie/AndServer/releases/tag/1.1.3).  

AndServer requires at minimum Java 7 or Android 2.3(Api level 9).

## ProGuard
```
-keepclassmembers class ** {
    @com.yanzhenjie.andserver.annotation.RequestMapping <methods>;
}
-keepclassmembers public class com.yanzhenjie.andserver.RequestMethod {
    <fields>;
}
```

## License
```text
Copyright 2017 Yan Zhenjie

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