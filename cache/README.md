# 缓存

在Http下行时缓存相关最常用且重要的`Header`：
* Last-Modified
* ETag
* Cache-Controller

在Http上行时缓存相关最常用且重要的`Header`：
* If-Match
* If-None-Match
* If-Modified-Since
* If-Unmodified-Since

在AndServer中，实现支持缓存的两个重要的类是：
* [ETag](cache/ETag.md)
* [LastModified](cache/Modified.md)