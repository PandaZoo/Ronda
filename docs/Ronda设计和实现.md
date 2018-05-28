
#### Done
1. 实现netty的channel
2. 实现三种协议的实现


#### Todo

1. 实现与Spring Boot结合, 读取配置文件和注解
2. 实现基于Spring的RMI, WebService服务


#### 疑问

1. 等到打成jar后怎么将bean加载
    目前的想法是： 使用Spring配置文件进行实例化
    
2. Ronda完成后其他么使用？  
    直接加载整个包，所以直接新建一个Spring的module，测试的时候直接启动Spring module进行测试