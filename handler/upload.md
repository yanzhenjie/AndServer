# 上传文件

作为服务器，在某些情况下可能会接受客户端上传文件的请求。

上传文件涉及到文件保存，所以代码稍微多几行：
```java
public class UploadHandler implements RequestHandler {

	/**
	 * 保存文件的文件夹。
	 */
	private File mDirectory = Environment.getExternalStorageDirectory();

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) ... {
        if (!HttpRequestParser.isMultipartContentRequest(request)) { // 是否Form传文件的请求。
            response(403, "说好的文件呢", response);
        } else {
            try {
                processFileUpload(request);
                response(200, "上传成功", response);
            } catch (Exception e) {
                response(500, "保存文件失败", response);
            }
        }
    }

    private void response(int responseCode, String message, HttpResponse response) ... {
        response.setStatusCode(responseCode);
        response.setEntity(new StringEntity(message, "utf-8"));
    }

    /**
     * 保存文件和参数处理。
     */
    private void processFileUpload(HttpRequest request) throws Exception {
        FileItemFactory factory = new DiskFileItemFactory(1024 * 1024, mDirectory);
        HttpFileUpload fileUpload = new HttpFileUpload(factory);

        // 你还可以监听上传进度：
        // fileUpload.setProgressListener(new ProgressListener(){...});

		HttpUploadContext context = new HttpUploadContext((HttpEntityEnclosingRequest) request);
        List<FileItem> fileItems = fileUpload.parseRequest(context);

        for (FileItem fileItem : fileItems) {
            if (fileItem.isFormField()) { // 普通参数。
            	String key = fileItem.getName();      // 表单参数名。
                String value = fileItem.getString();  // 表单参数值。
                ...;
            } else { // 文件。
                // fileItem.getFieldName();           // 表单参数名。
                // fileItem.getName();                // 客户端指定的文件名。
                // fileItem.getSize();                // 文件大小。
                // fileItem.getContentType();         // 文件的MimeType。

				// 把流写到文件夹。
                File uploadedFile = new File(mDirectory, fileItem.getName());
                fileItem.write(uploadedFile);
            }
        }
    }
}

```