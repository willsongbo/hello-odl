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

public class HelloProvider {

    private static final Logger LOG = LoggerFactory.getLogger(HelloProvider.class);

    private static DataBroker dataBroker;
    private static NotificationPublishService notificationPublishService;

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
     * Method called when the blueprint container is created.
     */
    public void init() {
        LOG.info("HelloProvider Session Initiated");
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
        LOG.info("HelloProvider Closed");
    }
}