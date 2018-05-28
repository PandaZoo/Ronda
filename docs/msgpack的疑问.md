在测试msgpack序列化的时候，发现如下问题:

1. Msgpack序列化需要template，通过`@Message`注解或者代码注册
2. Msgpack必须要找到对应type的class，比如Object、Class<?>就不可以
3. Msgpack应该保持单例，通过单例的msgpack创建packer和unpacker进行序列化和反序列化
4. 现在请求结构包含`Object[]`这样的调用方法参数，但是msgpack会序列化失败，所以要考虑的是要不要实现msgpack协议？
    如果要实现的话:
    * 是在原来的基础把不能序列化的给改掉，但是这样需要先对请求和返回信息进行处理，然后在使用msgpack
    进行序列化。
    * 直接对应msgpack新出来一个对象，但是在反序列化的时候，因为协议是在请求信息里，没办法通过bytes[]明确
    使用的何种协议，就会造成序列化失败。或者改掉netty的Encode方式，把序列化协议写进头部,这样在反序列化可以
    提前知道序列化协议。
    
   还没考虑好要怎么做比较合适。

看了其他的实现方案，注册了自定义的Template，这样子解决是一个很好的方案。


自己实现模板的关键是什么？ 看了实现是主要是用MessagePack.writeArrayBegin()和MessagePack.writeArrayEnd()。去了官网并没有一个很好的说明,
现在的理解就是write一次就写一个数组其中的位置。

那么怎么合理能够序列化RequestMessage和ResponseMessage？
 
首先RequestMessage包括的信息有:

```
    private Long messageId;
    private String targetClass;
    private String targetMethod;
    private Class[] argType;
    private Object[] args;
    private String body;
    private Map<String, Object> attributesMap;
```

那总共的Array应该是7个，分别是：

1. messageId，使用Long就可以
2. targetClass, 使用String就可以
3. targetMethod, 使用String就可以
4. Class[] argType, 使用这里需要新建一个array，使用argType.length数组，然后
    得到里边真正的class之后通过template进行write(这里有个疑问, Class序列化之后是什么, 现在决定序列化成String)
5. Object[] args, 新建一个array, 使用args.length进行数组, 通过arg真正的class，
    通过template进行序列化
6. 直接使用String模板进行序列化body
7. attributesMap, 这里需要实现一个自定义的Map序列化模板。因为Map里可能是object，所以需要对map需要特殊处理。


如何反序列化呢?

以写的顺序进行readArrayBegin，然后以对应的模板进行反序列化

