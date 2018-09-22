# MultipartFile

表示客户端在表单中上传的文件，开发者可以将文件转移到想要保存的目标位置。

```java
@RestController
public class UploadController {

    @PostMapping("/upload")
    void upload(@RequestParam("file1") MultipartFile file1,
        @RequestParam("file2") MultipartFile file2) {
        ...
    }
}
```

## 方法说明
* getName()，获取该文件的`key`，也就是表单中的`name`
* getFilename()，获取该文件名称，可能为空
* getContentType()，获取该文件的内容类型
* isEmpty()，判断该文件是否是非空的
* getSize()，获取文件大小
* getBytes()，获取文件的`byte`数组，不推荐使用
* getStream()，获取该文件的输入流
* transferTo(File)，转移该文件到目标位置

## 示例
```java
@RestController
public class UploadController {

    @PostMapping(path = "/upload")
    void upload(@RequestParam(name = "file") MultipartFile file) throws IOException {
        File localFile = ...
        file.transferTo(localFile);
    }
}
```