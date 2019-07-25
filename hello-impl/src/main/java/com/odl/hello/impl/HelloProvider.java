/*
 * Copyright © 2017 odl and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.odl.hello.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description hello 服务类-工程服务启动入口. 生产者 Service Class - Engineering Services Start-up Entry. Producer
 * @Author songbo
 * @Date 2019/7/2 15:21
 * @Version 1.0
 **/
public class HelloProvider {

    private static final Logger LOG = LoggerFactory.getLogger(HelloProvider.class);

    /**
     * 数据库服务 Database Services
     */
    private static DataBroker dataBroker;
    /**
     * 通知发布服务 Notification Publishing Service
     */
    private static NotificationPublishService notificationPublishService;

    /**
     * 服务类构造方法，用于蓝图加载此类. 注意构造方法参数和蓝图文件的对应
     * Service class construction method for blueprint loading of this class. Pay attention to the correspondence of
     * construction method parameters and blueprint files
     * @param dataBroker
     * @param notificationPublishService
     */
    public HelloProvider(final DataBroker dataBroker, NotificationPublishService notificationPublishService) {
        HelloProvider.dataBroker = dataBroker;
        HelloProvider.notificationPublishService = notificationPublishService;
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
        LOG.info("HelloProvider Session Initiated");
    }

    /**
     * 服务类结束方法，odl退出时，释放资源和关闭服务，蓝图结束时会调用此方法
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
        LOG.info("HelloProvider Closed");
    }
}