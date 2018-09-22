# View

`View`被用在一些接口的简单实现类中，比如`RequestHandler`的简单实现类`SimpleRequestHandler`，减少了入参，可以直接返回`View`对象。

View的关键Api如下：
```java
public class View {
	/**
	 * 直接传入响应码，没有包体。
	 */
    public View(int httpCode);

	/**
	 * 传入响应码，用String作为包体。
	 */
    public View(int httpCode, String httpBody);

	/**
	 * 传入响应码，用制定的HttpEntity作为包体。
	 */
    public View(int httpCode, HttpEntity httpEntity);

	/**
	 * 设置某个响应头。
	 */
    public void setHeader(String key, String value);

	/**
	 * 添加某个相应头。
	 */
    public void addHeader(String key, String value);
}
```

## 简单子类

**成功**：
```java
public class OkView extends View {

	/**
	 * 直接返回成功，没有包体。
	 */
    public OkView();

	/**
	 * 返回成功和String包体。
	 */
    public OkView(String httpBody);

	/**
	 * 返回成功和指定的HttpEntity包体。
	 */
    public OkView(HttpEntity httpEntity);
}
```

例如：
* `return new OkView("成功")`
* `return new OkView(JSON.toJSONString(user))`

**重定向**：
```java
public class RedirectView extends View {

	/**
	 * 传入重定向的path或者url即可。
	 */
    public RedirectView(String path);
}
```

例如：
* `return new RedirectView("/user/admin")`
* `return new RedirectView("http://www.yanzhenjie.com")`