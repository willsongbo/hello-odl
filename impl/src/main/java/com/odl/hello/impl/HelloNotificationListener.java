package com.odl.hello.impl;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.MoreExecutors;
import org.opendaylight.mdsal.binding.api.WriteTransaction;
import org.opendaylight.mdsal.common.api.CommitInfo;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.HelloListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.StudentPlayTruant;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.StudentsData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.students.data.Students;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.students.data.StudentsKey;
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
        wt.commit().addCallback(new FutureCallback<CommitInfo>() {
            @Override
            public void onSuccess(final CommitInfo result) {
                LOG.info("onStudentPlayTruant success [{}]",notification);
            }

            @Override
            public void onFailure(final Throwable ex) {
                LOG.error("onStudentPlayTruant failed [{}]", notification);
            }
        }, MoreExecutors.directExecutor());;
    }
}
