package com.odl.hello.impl;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.opendaylight.mdsal.binding.api.ReadTransaction;
import org.opendaylight.mdsal.binding.api.WriteTransaction;
import org.opendaylight.mdsal.common.api.CommitInfo;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.AddStudentInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.AddStudentOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.AddStudentOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.GetStudentInfoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.GetStudentInfoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.GetStudentInfoOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.HelloService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.PublishStudentNotificationInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.PublishStudentNotificationOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.PublishStudentNotificationOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.RemoteCallInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.RemoteCallOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.RemoteCallOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.StudentPlayTruant;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.StudentPlayTruantBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.StudentsData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.students.data.Students;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.students.data.StudentsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev210508.students.data.StudentsKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * hello-rpc.
 *
 * @Description hello 实现rpc的类 消费者 Class Consumers Implementing RPC
 * @Author songbo
 * @Date 2021/5/11 15:21
 * @Version 1.0
 **/
public class HelloImpl implements HelloService {
    private static final
        Logger LOG = LoggerFactory.getLogger(HelloImpl.class);

    /**
     * RPC.
     * add student info to data-store.
     * 添加单个学生信息到数据库中
     *
     * @param input student info
     * @return void
     */
    @Override
    public ListenableFuture<RpcResult<AddStudentOutput>> addStudent(AddStudentInput input) {
        final SettableFuture<RpcResult<AddStudentOutput>> futureResult = SettableFuture.create();
        WriteTransaction wt = HelloProvider.getDataBroker().newWriteOnlyTransaction();
        InstanceIdentifier<Students> studentsIid = InstanceIdentifier.builder(StudentsData.class).child(Students
                .class, new StudentsKey(input
                .getNumber())).build();
        Students students = new StudentsBuilder(input).build();
        wt.put(LogicalDatastoreType.CONFIGURATION, studentsIid, students);
        wt.commit().addCallback(new FutureCallback<CommitInfo>() {
            @Override
            public void onSuccess(final CommitInfo result) {
                futureResult.set(RpcResultBuilder.success(new AddStudentOutputBuilder().build()).build());
            }

            @Override
            public void onFailure(final Throwable ex) {
                LOG.error("RPC addStudent : student addition failed [{}]", input, ex);
                futureResult.set(RpcResultBuilder.<AddStudentOutput>failed()
                        .withError(RpcError.ErrorType.APPLICATION, ex.getMessage()).build());
            }
        }, MoreExecutors.directExecutor());;
        return futureResult;
    }

    /**
     * RPC.
     * get student info.
     * 根据学号从数据库钟获取单个学生的信息.
     *
     * @param input student number
     * @return student info
     */
    @Override
    public ListenableFuture<RpcResult<GetStudentInfoOutput>> getStudentInfo(GetStudentInfoInput input) {
        if (null != input.getNumber()) {
            InstanceIdentifier<Students> studentsIid = InstanceIdentifier.builder(StudentsData.class)
                    .child(Students.class, new StudentsKey(input.getNumber())).build();
            ReadTransaction rt = HelloProvider.getDataBroker().newReadOnlyTransaction();
            try {
                Optional<Students> optionalStudents = rt.read(LogicalDatastoreType.CONFIGURATION, studentsIid).get();
                if (optionalStudents.isPresent()) {
                    Students students = optionalStudents.get();
                    GetStudentInfoOutput getStudentInfoOutput = new GetStudentInfoOutputBuilder(students).build();
                    return Futures.immediateFuture(RpcResultBuilder.<GetStudentInfoOutput>success()
                            .withResult(getStudentInfoOutput).build());
                }
            } catch (InterruptedException | ExecutionException e) {
                LOG.error("getStudentInfo error with exc:", e);
            }
        }
        return Futures.immediateFuture(RpcResultBuilder.<GetStudentInfoOutput>failed().withError(RpcError.ErrorType.RPC,
                "getStudentInfo fail !").build());
    }

    @Override
    public ListenableFuture<RpcResult<RemoteCallOutput>> remoteCall(RemoteCallInput input) {
        System.out.println("this is call me");
        LOG.info("this is call me");
        RemoteCallOutput remoteCallOutput = new RemoteCallOutputBuilder()
                .setMsg("hello remote call. This is " + HelloProvider.localIp)
                .build();
        return Futures.immediateFuture(RpcResultBuilder.<RemoteCallOutput>success().withResult(remoteCallOutput)
                .build());
    }

    /**
     * notification.
     * publishStudentNotification.
     * 发布学生逃课的信息.
     *
     * @param input NotificationInfo
     * @return void
     */
    @Override
    public ListenableFuture<RpcResult<PublishStudentNotificationOutput>> publishStudentNotification(
            PublishStudentNotificationInput input) {
        StudentPlayTruant studentPlayTruant = new StudentPlayTruantBuilder()
                .setLesson(input.getLesson())
                .setName(input.getName())
                .setNumber(input.getNumber())
                .setReason(input.getReason())
                .build();
        HelloProvider.getNotificationPublishService().offerNotification(studentPlayTruant);
        return Futures.immediateFuture(RpcResultBuilder
                .success(new PublishStudentNotificationOutputBuilder().build()).build());
    }

}
