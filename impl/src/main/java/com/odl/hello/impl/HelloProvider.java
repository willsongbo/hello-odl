/*
 * Copyright © 2017 odl and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.odl.hello.impl;

import com.google.common.collect.ImmutableSet;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.NotificationPublishService;
import org.opendaylight.mdsal.binding.api.RpcProviderService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.ControllerIps;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.HelloService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.controller.ips.ControllerIpList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.controller.ips.ControllerIpListKey;
import org.opendaylight.yangtools.concepts.ObjectRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description hello 服务类-工程服务启动入口. 生产者 Service Class - Engineering Services Start-up Entry. Producer
 * @Author songbo
 * @Date 2019/7/2 15:21
 * @Version 1.0
 **/
public class HelloProvider {

    private static final Logger LOG = LoggerFactory.getLogger(HelloProvider.class);
    private HelloStudentDataListener helloStudentDataListener;
    private static Set<ObjectRegistration<?>> regs = new HashSet<>();
    private static RpcProviderService rpcProviderService;
    private static HelloService helloRpcImplementation;
    /**
     * 数据库服务 Database Services
     */
    private static DataBroker dataBroker;
    /**
     * 通知发布服务 Notification Publishing Service
     */
    private static NotificationPublishService notificationPublishService;


    public static final String IP_PATH = "etc/ip.properties";
    public static final String IP = "IP";
    public static String localIp;

    /**
     * 服务类构造方法，用于蓝图加载此类. 注意构造方法参数和蓝图文件的对应
     * Service class construction method for blueprint loading of this class. Pay attention to the correspondence of
     * construction method parameters and blueprint files
     *
     * @param dataBroker
     * @param notificationPublishService
     */
    public HelloProvider(final DataBroker dataBroker, NotificationPublishService notificationPublishService,
                         RpcProviderService rpcProviderService, HelloService helloRpcImplementation) {
        HelloProvider.dataBroker = dataBroker;
        HelloProvider.notificationPublishService = notificationPublishService;
        HelloProvider.helloRpcImplementation = helloRpcImplementation;
        HelloProvider.rpcProviderService = rpcProviderService;

        // Add global registration
        regs.add(rpcProviderService.registerRpcImplementation(HelloService.class, helloRpcImplementation));
    }

    public static DataBroker getDataBroker() {
        return dataBroker;
    }

    public static NotificationPublishService getNotificationPublishService() {
        return notificationPublishService;
    }

    /**
     * 服务类初始化方法用于初始化注册监听等服务，蓝图初始化会调用此方法
     * Method called when the blueprint container is created.
     */
    public void init() {
        helloStudentDataListener = new HelloStudentDataListener();

        localIp = Property.getProperties(IP_PATH).getOrDefault(IP, "");

        final InstanceIdentifier.InstanceIdentifierBuilder<ControllerIpList> controllerIpListIID =
                InstanceIdentifier.builder(ControllerIps.class).child(ControllerIpList.class, new ControllerIpListKey
                        (localIp));
        final InstanceIdentifier<ControllerIpList> controllerIpIID = controllerIpListIID.build();

        regs.add(rpcProviderService.registerRpcImplementation(HelloService.class, helloRpcImplementation,
                ImmutableSet.of(controllerIpIID)));
        LOG.info("HelloProvider Session Initiated");
    }

    /**
     * 服务类结束方法，odl退出时，释放资源和关闭服务，蓝图结束时会调用此方法
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
        regs.forEach(ObjectRegistration::close);
        regs.clear();
        helloStudentDataListener.close();
        LOG.info("HelloProvider Closed");
    }
}