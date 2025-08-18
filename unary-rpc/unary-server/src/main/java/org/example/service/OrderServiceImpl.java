package org.example.service;

import io.grpc.stub.StreamObserver;
import orders.OrderServiceGrpc;
import orders.OrderServiceOuterClass;

import java.util.ArrayList;
import java.util.List;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    @Override
    public void getOrder(OrderServiceOuterClass.OrderRequest orderRequest, StreamObserver<OrderServiceOuterClass.Order> responseObserver) {
        OrderServiceOuterClass.Order order = OrderServiceOuterClass.Order.newBuilder()
                .setId(1)
                .setItem("itm-1")
                .setQuantity(2)
                .build();

        responseObserver.onNext(order);
        responseObserver.onCompleted();
    }

    @Override
    public void getOrders(OrderServiceOuterClass.OrdersRequest ordersRequest, StreamObserver<OrderServiceOuterClass.OrderList> responseObserver) {
        List<OrderServiceOuterClass.Order> orders = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            OrderServiceOuterClass.Order order = OrderServiceOuterClass.Order.newBuilder()
                    .setItem("itm-" + i)
                    .setQuantity(i + 1)
                    .setId(i + 1)
                    .build();
            orders.add(order);
        }

        OrderServiceOuterClass.OrderList orderList = OrderServiceOuterClass.OrderList.newBuilder().addAllOrders(orders).build();
        responseObserver.onNext(orderList);
        responseObserver.onCompleted();


    }

}
