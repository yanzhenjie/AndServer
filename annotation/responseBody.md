# ResponseBody

在学习使用`ReponseBody`之前，开发者应该先掌握[Controller](controller.md)、[RestController](restController.md)和[RequestMapping](requestMapping.md)的使用。

`ResponseBody`是响应包体，`Controller`和`RestController`中的方法返回值最终都会被转化为`ResponseBody`发送，因此如果遇到通过本文开始提到的三者无法实现的需求时，才推荐使用`ResponseBody`。

下文将先介绍`ResponseBody`的使用方法，然后再举例说明。

## 使用方法
`ResponseBody`是一个Interface，AndServer也提供了一些常用实现类。



## 示例