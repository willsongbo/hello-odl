# hello-odl
opendaylight demo project
based on oxygen-sr4

The demo example of opendaylight is for learning.
After compiling with maven, ODL packages can be obtained in hello-karaf and run directly in JVM environment.
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
Remote RPC calls input parameter structure：
#
{"input":{"controller-ip":"/hello:controller-ips/hello:controller-ip-list[hello:ip='172.20.14.164']"}}
