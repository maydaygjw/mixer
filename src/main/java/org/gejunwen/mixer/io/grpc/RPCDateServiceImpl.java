package org.gejunwen.mixer.io.grpc;

import io.grpc.stub.StreamObserver;
import org.gejunwen.grpc.api.RPCDateRequest;
import org.gejunwen.grpc.api.RPCDateResponse;
import org.gejunwen.grpc.api.RPCDateServiceGrpc;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RPCDateServiceImpl extends RPCDateServiceGrpc.RPCDateServiceImplBase {

    @Override
    public void getDate(RPCDateRequest request, StreamObserver<RPCDateResponse> responseObserver) {
        RPCDateResponse rpcDateResponse = null;
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("今天是" + "yyyy年MM月dd日 E kk点mm分");
        String nowTime = simpleDateFormat.format(now);
        try {
            rpcDateResponse = RPCDateResponse
                    .newBuilder()
                    .setServerDate("Welcome " + request.getUserName() + ", " + nowTime)
                    .build();
        } catch (Exception e) {
            responseObserver.onError(e);
        } finally {
            responseObserver.onNext(rpcDateResponse);
        }
        responseObserver.onCompleted();
    }
}
