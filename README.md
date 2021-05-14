# Hello-ODL-Aluminum
opendaylight demo project based on Aluminum


After compiling with maven, ODL packages can be obtained in hello-karaf and run directly in JVM environment
#
Jdk version need jdk11
#
There are several basic typical functional examples:

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

# Notice

In general, there is little difference between aluminum version and oxygen version, but there are many changes in some details. for example：
#
Changes of common class paths of some controller modules
#
Not only remote RPC, ordinary RPC also need to be registered in blueprint
#
There is no init method in blueprint. I can compile normally during development, but it fails（ Maybe it's my misuse.)
#
Some initialization processes are transferred from init to the construction method of provider
#
Database operation classes like readonlytransaction have been removed and replaced with readtransaction
#
More detailed needs to do it yourself development to discover.
#


Aluminium 版本开发框架上总体和oxygen版本差别不大，但是开发上的一些细节有不少的改变，例如：
#
一些controller模块常用类路径的变化
#
不仅是远程rpc，普通rpc也需要在蓝图注册了
#
蓝图没有init方法我在开发过程中可以正常使用编译，但是运行失败。（也许是我的错误使用问题）
#
初始化的一些过程由init转入provider的构造方法
#
数据库操作类像是ReadOnlyTransaction已经移除并替换为ReadTransaction

更详细的需要自己动手开发来发现
