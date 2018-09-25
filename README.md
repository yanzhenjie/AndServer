# AndServer

![Logo](./images/logo.svg)

Web server and Web framework of Android platform. It provides annotations like SpringMVC, and if you are familiar with SpringMVC, you can master it very quickly.

* Static html website deployment
* Dynamic http api deployment

```java
@RestController
@RequestMapping(path = "/user")
public class UserController {

    @PostMapping("/login")
    public String login(@RequestParam("account") String account, 
        @RequestParam("password") String password) {
        if (...) {
            return "Successful.";
        }
        return "Failed.";
    }

    @GetMapping(path = "/info/{userId}")
    public User detail(@PathVariable("userId") String userId) {
        User user = ...;
        return userInfo;
    }
}
```

The above code will generate the following two http apis:
```text
POST http://host:port/user/login
GET http://host:port/user/info/uid_001
```

For documentation and additional information see [the website](https://www.yanzhenjie.com/AndServer).

## Download
```groovy
dependencies {
    implementation 'com.yanzhenjie.andserver:api:2.0.1'
    annotationProcessor 'com.yanzhenjie.andserver:processor:2.0.1'
}
```

AndServer requires at minimum Android 2.3(Api level 9).

## License
```text
Copyright 2018 Yan Zhenjie

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