# 常见问题

- **是否可以不使用 AndServer 插件？在 build.gradle 中不加入`apply plugin`？**

  答案是不可以。我们很理解开发者提出这样的问题，不想在主工程上使用插件，我们可以新开一个 module 来开发和 AndServer 相关的功能，开发好之后发布这个 module 到 maven 仓库，主工程只需要添加这个 module 的远程依赖即可，不用引用 AndServer 的依赖和任何插件。

- **出现 AndServer 编译失败或者`The resource was not found.`提示?**

  请检查 AndServer 的所有依赖版本需要统一，例如全部统一为`2.1.8`（请在 GitHub 项目主页查看最新版本）。

- **如何获取客户端的 IP 地址？**

  请将 AndServer 依赖更新到`2.1.8`或者 GitHub 项目主页上公布的更新版：

  ```java
    @GetMapping(path = "/connection")
    public Object getConnection(HttpRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("getLocalAddr", request.getLocalAddr());
        map.put("getLocalName", request.getLocalName());
        map.put("getLocalPort", request.getLocalPort());
        map.put("getRemoteAddr", request.getRemoteAddr());
        map.put("getRemoteHost", request.getRemoteHost());
        map.put("getRemotePort", request.getRemotePort());
        ...
    }
  ```

- **Mapping 配置 path 时如何使用通配符？**

  如下所示，`{name}`可以匹配任何 path 规则允许的字符，通过`PathVariable`注解来获取匹配到的值：

  ```java
    @RequestMapping(
        path = "/get/{name}")
    public String info(@PathVariable(name = "name") String name) {
        ...
    }
  ```

- **如何让 HTTP API 支持跨域？**

  使用`CrossOrigin`注解即可：

  ```java
    @CrossOrigin(methods = {RequestMethod.POST, RequestMethod.GET})
    @RequestMapping(path = "/get/{userId}")
    public String info(@PathVariable(name = "userId") String userId) {
        return userId;
    }
  ```

- **如何配置上传文件的大小限制？**

  使用`Config`注解和`WebConfig`接口即可：

  ```java
  @Config
  public class AppConfig implements WebConfig {

    @Override
    public void onConfig(Context context, Delegate delegate) {
      File uploadDir = ...;

      delegate.setMultipart(Multipart.newBuilder()
        .allFileMaxSize(1024 * 1024 * 20) // 单个请求所有文件总大小
        .fileMaxSize(1024 * 1024 * 5) // 单个请求每个文件大小
        .maxInMemorySize(1024 * 10) // 内存缓存大小
        .uploadTempDir(uploadDir) // 上传文件保存目录
        .build());
    }
  }
  ```

- **如何给让 AndServer 固定 IP 地址？**

  显然，有这样问题的同学是对网络不了解，可以读一下严振杰的这篇文章：
  https://yanzhenjie.blog.csdn.net/article/details/93098495

  总之，IP 地址在网络中是指某个设备的地址，和什么网络框架没有关系，因此，想要固定设备的网络访问地址只有一种方法：

  1. 向电信运营商申请一个公网固定 IP，那到我们家里的网线头的 IP 地址就固定了，现在这个网线上插的设备的 IP 地址就是我们申请到的这个 IP 地址。
  2. 我们的 IP 可能会总是变化，那么我们还可以买一个域名，把域名解析指向上述固定 IP，通过域名就可以访问这个地址了，如果 IP 变化了，那么重新设置解析即可。
