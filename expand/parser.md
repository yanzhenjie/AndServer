# Request解析

这里介绍一个比较有用的帮助类`HttpRequestParser`：
```java
public class HttpRequestParser {

	/**
	 * 解析当前Request的请求参数。
	 */
    public static Map<String, String> parseParams(HttpRequest request);

	/**
	 * 解析当前Request的请求参数，可控制是否把参数名转为小写。
	 */
    public static Map<String, String> parseParams(HttpRequest request, boolean lowerCaseNames);

    /**
     * 拿到当前Request的path。
     */
    public static String getRequestPath(HttpRequest request);

    /**
     * 当前Requst是否是允许带有Body的。
     */
    public static boolean isAllowRequestBody(HttpRequest request);

    /**
     * 拿到当前Request的请求方法。
     */
    public static RequestMethod getRequestMethod(HttpRequest request);

    /**
     * 当期Request是否带有表单Body。
     */
    public static boolean isMultipartContentRequest(HttpRequest request);

    /**
     * 解析Request的某个Date类型的头为毫秒，如果不存在则为-1。
     */
    public static long parseDateHeader(HttpRequest request, String headerName);
}

```

还有其它几个方法，几乎不会用到，这里不做介绍，有兴趣的开发者可以自行看源码。