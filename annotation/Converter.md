# Converter

Converter是转换器的意思，在AndServer中`Converter`注解用来标记某个类是[MessageConverter](../class/MessageConverter.md)的子类，并且作为消息转换器参与到`AndServer`的运行中。

`Converter`注解可以添加到多个类，但是只有其中一个添加了`Converter`注解的类参与程序运行，具体是哪个要看编译器先扫描到哪个类，**因此建议开发者只为一个类添加`Converter`注解**。

## 示例
```java
@Converter
public class AppMessageConverter implements MessageConverter {

    @Override
    public ResponseBody convert(Object output, MediaType mediaType) {
        ...
    }

    @Nullable
    @Override
    public <T> T convert(InputStream stream, MediaType mediaType, Type type)
        throws IOException {
        ...
    }
}
```

> 注意，添加`Converter`注解的类必需是[MessageConverter](../class/MessageConverter.md)的子类，否则编译时将不通过。