package com.odl.hello.impl;

import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.HelloListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.StudentPlayTruant;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.StudentsData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.students.data.Students;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.students.data.StudentsKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description HelloNotificationListener 接收通知的类 Classes that receive notifications
 * @Author songbo
 * @Date 2019/7/3 9:13
 * @Version 1.0
 **/
public class HelloNotificationListener implements HelloListener {
    private static final Logger LOG = LoggerFactory.getLogger(HelloNotificationListener.class);

    /**
     * receive notification
     * 收到学生缺课的通知 将其从数据库中移除
     *
     * @param notification play truant
     */
    @Override
    public void onStudentPlayTruant(StudentPlayTruant notification) {
        WriteTransaction wt = HelloProvider.getDataBroker().newWriteOnlyTransaction();
        InstanceIdentifier<Students> studentsIid = InstanceIdentifier.builder(StudentsData.class).
                child(Students.class, new StudentsKey(notification.getNumber())).build();
        wt.delete(LogicalDatastoreType.CONFIGURATION, studentsIid);
        try {
            wt.submit().checkedGet();
        } catch (TransactionCommitFailedException e) {
            LOG.error("onStudentPlayTruant error exc:", e);
        }
    }
}
