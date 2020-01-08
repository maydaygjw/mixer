package org.gejunwen.mixer.io.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.gejunwen.grpc.api.RPCDateRequest;
import org.gejunwen.grpc.api.RPCDateResponse;
import org.gejunwen.grpc.api.RPCDateServiceGrpc;

public class GRPCClient {

    private static final String host = "localhost";
    private static final int serverPort = 9999;

    public static void main( String[] args ) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress( host, serverPort ).usePlaintext().build();
        try {
            RPCDateServiceGrpc.RPCDateServiceBlockingStub rpcDateService = RPCDateServiceGrpc.newBlockingStub( managedChannel );
            RPCDateRequest rpcDateRequest = RPCDateRequest
                    .newBuilder()
                    .setUserName("gejunwen")
                    .build();
            RPCDateResponse rpcDateResponse = rpcDateService.getDate( rpcDateRequest );
            System.out.println( rpcDateResponse.getServerDate() );
        } finally {
            managedChannel.shutdown();
        }
    }
}
