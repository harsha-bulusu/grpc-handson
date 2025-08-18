package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.example.service.OrderServiceImpl;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(8080)
                .addService(new OrderServiceImpl())
                .build();

        System.out.println("Server started running on port 8080");
        server.start();
        server.awaitTermination();
    }
}
