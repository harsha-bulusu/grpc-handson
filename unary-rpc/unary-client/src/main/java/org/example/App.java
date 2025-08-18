package org.example;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import orders.OrderServiceGrpc;
import orders.OrderServiceOuterClass;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        OrderServiceGrpc.OrderServiceBlockingStub stub = OrderServiceGrpc.newBlockingStub(channel);

        // GetOrder
        OrderServiceOuterClass.OrderRequest orderRequest = OrderServiceOuterClass.OrderRequest.newBuilder()
                        .setOrderId(1)
                        .build();
        OrderServiceOuterClass.Order order = stub.getOrder(orderRequest);
        System.out.println("Order: " + order.getItem());

        // GetOrders
        OrderServiceOuterClass.OrdersRequest ordersRequest = OrderServiceOuterClass.OrdersRequest.newBuilder()
                        .setUserId(1)
                        .build();

        OrderServiceOuterClass.OrderList orders = stub.getOrders(ordersRequest);
        orders.getOrdersList().stream().forEach(odr -> System.out.println(odr.getItem()));
    }
}
