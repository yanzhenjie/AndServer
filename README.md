# AndServer

![Logo](./images/logo.svg)

AndServer is an HTTP and reverse proxy server.

Web server and Web framework of Android platform. It provides annotations like SpringMVC, and if you are familiar with SpringMVC, you can master it very quickly.

- Static html website deployment.
- Dynamic http api deployment.
- Reverse proxy server.

## Web Server

Deploy a web server:

```java
Server server = AndServer.webServer(context)
    .port(8080)
    .timeout(10, TimeUnit.SECONDS)
    .build();

// startup the server.
server.startup();

...

// shutdown the server.
server.shutdown();
```

It also has some features, such as `inetAddress(InetAddress)`, `serverSocketFactory(ServerSocketFactory)` and `sslContext(SSLContext)`, depending on what you want to achieve.

```java
@RestController
@RequestMapping(path = "/user")
public class UserController {

    @PostMapping("/login")
    public String login(@RequestParam("account") String account,
                        @RequestParam("password") String password) {

        ...
        return "Successful.";
    }

    @GetMapping(path = "/{userId}")
    public User info(@PathVariable("userId") String userId,
                     @QueryParam("fields") String fields) {

        User user = findUserById(userId, fields);
        ...

        return user;
    }

    @PutMapping(path = "/{userId}")
    public void modify(@PathVariable("userId") String userId
                       @RequestParam("age") int age) {
        ...
    }
}
```

The above code will generate the following two http apis:

```text
POST http://.../user/login
GET http://.../user/uid_001?fields=id,name,age
PUT http://.../user/uid_001
```

Get connection information with the client:

```java
@GetMapping(path = "/connection")
void getConnection(HttpRequest request, ...) {
    request.getLocalAddr();   // HostAddress
    request.getLocalName();   // HostName
    request.getLocalPort();   // server's port

    request.getRemoteAddr();  // HostAddress
    request.getRemoteHost();  // Especially HostName, second HostAddress
    request.getRemotePort();  // client's port

    ...
}
```

For documentation and additional information see [the website](https://yanzhenjie.com/AndServer).

## Reverse Proxy Server

Deploy a reverse proxy server:

```java
Server server = AndServer.proxyServer()
    .addProxy("www.example1.com", "http://192.167.1.11:8080")
    .addProxy("example2.com", "https://192.167.1.12:9090")
    .addProxy("55.66.11.11", "http://www.google.com")
    .addProxy("192.168.1.11", "https://github.com:6666")
    .port(80)
    .timeout(10, TimeUnit.SECONDS)
    .build();

// startup the server.
server.startup();

...

// shutdown the server.
server.shutdown();
```

**Note**: It is just a reverse proxy and does not have the ability to take care of loading balance.

## Download

Add the plugin to your project build script :

```gradle
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath 'com.yanzhenjie.andserver:plugin:2.1.10'
        ...
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
...
```

And then add `AndServer` dependency to your module:

```gradle
apply plugin: 'com.yanzhenjie.andserver'

...

dependencies {
    implementation 'com.yanzhenjie.andserver:api:2.1.10'
    annotationProcessor 'com.yanzhenjie.andserver:processor:2.1.10'
    ...
}
```

If you are using Kotlin, replace `annotationProcessor` with `kapt`.

## Contributing

Before submitting pull requests, contributors must abide by the [agreement](./CONTRIBUTING.md) .

## License

```text
Copyright 2021 Zhenjie Yan

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
