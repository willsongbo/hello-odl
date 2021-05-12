package com.odl.hello.impl;

import org.opendaylight.mdsal.binding.api.DataObjectModification;
import org.opendaylight.mdsal.binding.api.DataTreeChangeListener;
import org.opendaylight.mdsal.binding.api.DataTreeIdentifier;
import org.opendaylight.mdsal.binding.api.DataTreeModification;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.StudentsData;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @Description 数据监听器 监听student数据库 Data listener listens on student database
 * @Author songbo
 * @Date 2019/7/31 9:44
 * @Version 1.0
 **/
public class HelloStudentDataListener implements AutoCloseable, DataTreeChangeListener<StudentsData> {
    private static final Logger LOG = LoggerFactory.getLogger(HelloStudentDataListener.class);
    private ListenerRegistration listenerRegistration;

    public HelloStudentDataListener() {
        this.init();
    }

    public void init() {
        /**
         * 实例化数据ID 并将其注册到ODL监听器中Instantiate the data ID and register it in the ODL listener
         */
        DataTreeIdentifier<StudentsData> studentDataTreeChangeListener = DataTreeIdentifier.create(LogicalDatastoreType.CONFIGURATION,
                InstanceIdentifier.builder(StudentsData.class).build());
        listenerRegistration = HelloProvider.getDataBroker().registerDataTreeChangeListener(studentDataTreeChangeListener, this);
        LOG.info("HelloStudentDataListener init start.");
    }

    @Override
    public void onDataTreeChanged(Collection<DataTreeModification<StudentsData>> changes) {
        LOG.info("{} data tree changed.", getClass().getSimpleName());
        for (final DataTreeModification<StudentsData> change : changes) {
            final DataObjectModification<StudentsData> rootChange = change.getRootNode();
            switch (rootChange.getModificationType()) {
                case WRITE:
                    handleWrite(rootChange.getDataBefore(), rootChange.getDataAfter());
                    break;
                case DELETE:
                    handleDelete(rootChange.getDataBefore(), rootChange.getDataAfter());
                    break;
                case SUBTREE_MODIFIED:
                    handleSubtreeModify(rootChange.getDataBefore(), rootChange.getDataAfter());
                    break;
                default:
                    handleSubtreeModify(rootChange.getDataBefore(), rootChange.getDataAfter());
            }
        }
    }

    private void handleWrite(StudentsData before, StudentsData after) {
        //todo  do write change job
        LOG.info("onDataTreeChanged write");
    }

    private void handleDelete(StudentsData before, StudentsData after) {
        //todo  do delete change job
        LOG.info("onDataTreeChanged delete");
    }

    private void handleSubtreeModify(StudentsData before, StudentsData after) {
        //todo  do modify change job
        LOG.info("onDataTreeChanged update");
    }

    @Override
    public void close() {
        try {
            listenerRegistration.close();
        } catch (Exception e) {
            LOG.error("HelloStudentDataListener close exc:", e);
        }
    }
}
