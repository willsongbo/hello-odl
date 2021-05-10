# hello-odl
opendaylight demo project
based on oxygen-sr4

The demo example of opendaylight is for learning.
After compiling with maven, ODL packages can be obtained in hello-karaf and run directly in JVM environment. 
Jdk version need jdk11.
There are several basic typical functional examples:

ODL的demo项目，基于oxygen-sr4版本开发，适合新手同学学习。
项目用maven编译后，odl包会在karaf的目录生成，拷贝到JVM环境就可运行，要求jdk1.8及以上。
主要包含下面几个技术点：

#
1) rpc
#
2) datastore
#
3) notification
#
4) dataChangeListener
#
5) remote-rpc (routed-rpc)
#

远程rpc的入参比较特殊，所以单独写一下示例：
{"input":{"controller-ip":"/hello:controller-ips/hello:controller-ip-list[hello:ip='1.1.1.1']"}}

远程rpc和集群需要一个ip.properties文件还有集群配置文件，都放在了karaf的config文件夹中。运行ODL前要把ip.properties放在odl的etc目录下，两个集群配置文件放在odl的bin目录。

ip.properties文件配置示例：IP=1.1.1.1（当前控制器管理地址）

在远程rpc中，因为每个集群成员都需要一个key，这里我使用成员的ip地址作为key，获取ip的方式就是读取这个ip.properties文件。

更多关于此项目的讲解，可参考我的sdnlab文章链接：https://www.sdnlab.com/23834.html
