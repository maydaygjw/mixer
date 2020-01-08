package org.gejunwen.mixer.io.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GRPCServer {

    private static final int port = 9999;

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.
                forPort(port)
                .addService(new RPCDateServiceImpl())
                .build().start();
        System.out.println("grpc服务端启动成功, 端口=" + port);
        server.awaitTermination();
    }
}
