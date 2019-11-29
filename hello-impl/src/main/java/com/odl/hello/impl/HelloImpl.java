package com.odl.hello.impl;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.Futures;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.AddStudentInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.GetStudentInfoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.GetStudentInfoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.GetStudentInfoOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.HelloService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.PublishStudentNotificationInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.RemoteCallInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.RemoteCallOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.RemoteCallOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.StudentPlayTruant;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.StudentPlayTruantBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.StudentsData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.students.data.Students;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.students.data.StudentsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev190701.students.data.StudentsKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Description hello 实现rpc的类 消费者 Class Consumers Implementing RPC
 * @Author songbo
 * @Date 2019/7/2 15:21
 * @Version 1.0
 **/
public class HelloImpl implements HelloService {
    private static final Logger LOG = LoggerFactory.getLogger(HelloImpl.class);

    /**
     * RPC
     * add student info to data-store
     * 添加单个学生信息到数据库中
     *
     * @param input student info
     * @return void
     */
    @Override
    public Future<RpcResult<Void>> addStudent(AddStudentInput input) {
        WriteTransaction wt = HelloProvider.getDataBroker().newWriteOnlyTransaction();
        InstanceIdentifier<Students> studentsIid = InstanceIdentifier.builder(StudentsData.class).child(Students
                .class, new StudentsKey(input
                .getNumber())).build();
        Students students = new StudentsBuilder(input).build();
        wt.put(LogicalDatastoreType.CONFIGURATION, studentsIid, students);
        try {
            wt.submit().checkedGet();
        } catch (TransactionCommitFailedException e) {
            LOG.error("addStudent error with exc:", e);
            return Futures.immediateFuture(RpcResultBuilder.<Void>failed().withError(RpcError.ErrorType.RPC,
                    "addStudent fail !").build());
        }
        return Futures.immediateFuture(RpcResultBuilder.<Void>success().build());
    }

    /**
     * RPC
     * get student info
     * 根据学号从数据库钟获取单个学生的信息
     *
     * @param input student number
     * @return student info
     */
    @Override
    public Future<RpcResult<GetStudentInfoOutput>> getStudentInfo(GetStudentInfoInput input) {
        if (null != input.getNumber()) {
            InstanceIdentifier<Students> studentsIid = InstanceIdentifier.builder(StudentsData.class).child(Students.class,
                    new StudentsKey(input.getNumber())).build();
            ReadOnlyTransaction rt = HelloProvider.getDataBroker().newReadOnlyTransaction();
            try {
                Optional<Students> optionalStudents = rt.read(LogicalDatastoreType.CONFIGURATION, studentsIid).get();
                if (optionalStudents.isPresent()) {
                    Students students = optionalStudents.get();
                    GetStudentInfoOutput getStudentInfoOutput = new GetStudentInfoOutputBuilder(students).build();
                    return Futures.immediateFuture(RpcResultBuilder.<GetStudentInfoOutput>success().withResult
                            (getStudentInfoOutput).build());
                }
            } catch (InterruptedException | ExecutionException e) {
                LOG.error("getStudentInfo error with exc:", e);
            }
        }
        return Futures.immediateFuture(RpcResultBuilder.<GetStudentInfoOutput>failed().withError(RpcError.ErrorType.RPC, "getStudentInfo fail !").build());
    }

    @Override
    public Future<RpcResult<RemoteCallOutput>> remoteCall(RemoteCallInput input) {
        System.out.println("this is call me");
        LOG.info("this is call me");
        RemoteCallOutput remoteCallOutput = new RemoteCallOutputBuilder()
                .setMsg("hello remote call.")
                .build();
        return Futures.immediateFuture(RpcResultBuilder.<RemoteCallOutput>success().withResult(remoteCallOutput)
                .build());
    }

    /**
     * notification
     * publishStudentNotification
     * 发布学生逃课的信息
     *
     * @param input NotificationInfo
     * @return void
     */
    @Override
    public Future<RpcResult<Void>> publishStudentNotification(PublishStudentNotificationInput input) {
        StudentPlayTruant studentPlayTruant = new StudentPlayTruantBuilder()
                .setLesson(input.getLesson())
                .setName(input.getName())
                .setNumber(input.getNumber())
                .setReason(input.getReason())
                .build();
        HelloProvider.getNotificationPublishService().offerNotification(studentPlayTruant);
        return Futures.immediateFuture(RpcResultBuilder.<Void>success().build());
    }

}
