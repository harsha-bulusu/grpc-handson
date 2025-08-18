## 🔄 gRPC Java Life Cycle with Maven Dependencies

### 1. **You write a proto file**

```proto
syntax = "proto3";

package orders;

service OrderService {
  rpc GetOrders (OrderRequest) returns (OrderList);
}

message OrderRequest {
  int32 user_id = 1;
}

message Order {
  string id = 1;
  string description = 2;
}

message OrderList {
  repeated Order orders = 1;
}
```

---

### 2. **Maven build phase → Code generation**

👉 When you run `mvn clean install`, Maven triggers the **protobuf-maven-plugin**.

* **os-maven-plugin**

  * Runs first and detects your OS (Linux, Mac, Windows).
  * Injects `${os.detected.classifier}` into Maven properties so correct `protoc` and `protoc-gen-grpc-java` binaries can be fetched.

* **protobuf-maven-plugin**

  * Downloads `protoc` compiler (`protoc:3.25.3`).
  * Downloads `protoc-gen-grpc-java` plugin (`grpc-java:1.65.0`).
  * Runs:

    ```bash
    protoc --java_out=target/generated-sources \
           --grpc-java_out=target/generated-sources \
           -I=src/main/proto order.proto
    ```
  * Generates two sets of code:

    1. **Message classes** (from `protobuf-java`):

       * `Order.java`, `OrderRequest.java`, `OrderList.java`
       * These extend `com.google.protobuf.GeneratedMessageV3`.
    2. **Service classes** (from `grpc-stub`):

       * `OrderServiceGrpc.java`

         * Contains `OrderServiceBlockingStub`, `OrderServiceStub` (async), and server base class.

---

### 3. **Compile phase → Java compiler checks dependencies**

* The generated message classes extend `GeneratedMessageV3` and use parser utilities from **protobuf-java**.
  👉 So without **protobuf-java**, compilation fails.

* The generated service class (`OrderServiceGrpc.java`) extends base classes from **grpc-stub**.
  👉 So without **grpc-stub**, compilation fails.

---

### 4. **Runtime → Running server & client**

#### On the server side:

* You implement your service:

  ```java
  public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
      @Override
      public void getOrders(OrderRequest req, StreamObserver<OrderList> responseObserver) {
          Order order = Order.newBuilder().setId("123").setDescription("Book").build();
          OrderList list = OrderList.newBuilder().addOrders(order).build();
          responseObserver.onNext(list);
          responseObserver.onCompleted();
      }
  }
  ```

* You start the server:

  ```java
  Server server = ServerBuilder.forPort(8080)
                  .addService(new OrderServiceImpl())
                  .build()
                  .start();
  ```

* Here:

  * **grpc-netty-shaded** provides the actual **HTTP/2 server** over TCP sockets.
  * **grpc-protobuf** is used to **serialize** `OrderList` into Protobuf binary format before sending on the wire.

---

#### On the client side:

* You create a channel & stub:

  ```java
  ManagedChannel channel = ManagedChannelBuilder
        .forAddress("localhost", 8080)
        .usePlaintext()
        .build();

  OrderServiceGrpc.OrderServiceBlockingStub stub = OrderServiceGrpc.newBlockingStub(channel);
  OrderRequest req = OrderRequest.newBuilder().setUserId(42).build();
  OrderList list = stub.getOrders(req);
  ```
* Here:

  * **grpc-stub** gives you the client stub (`OrderServiceBlockingStub`).
  * **grpc-protobuf** handles marshalling: converting `OrderRequest` → bytes and bytes → `OrderList`.
  * **grpc-netty-shaded** manages HTTP/2 streams on the connection.

---

### 5. **End-to-end network communication**

1. Client calls stub → request object (`OrderRequest`) created.
2. **grpc-protobuf** encodes request into bytes.
3. **grpc-netty-shaded** multiplexes the bytes into an **HTTP/2 stream**.
4. Server receives the stream → **grpc-netty-shaded** extracts bytes.
5. **grpc-protobuf** decodes into `OrderRequest` object.
6. Your service implementation processes it and returns `OrderList`.
7. Response is encoded back (protobuf) and written on same HTTP/2 stream.
8. Client stub decodes response into `OrderList`.

---

## 🎯 Quick Mapping (Lifecycle → Dependency)

| Stage                      | Dependency Used                                | Purpose                                                 |
| -------------------------- | ---------------------------------------------- | ------------------------------------------------------- |
| Code Generation            | **protobuf-maven-plugin**, **os-maven-plugin** | Runs protoc + gRPC plugin, generates Java code.         |
| Compile                    | **protobuf-java**                              | Provides runtime classes for generated message classes. |
| Compile                    | **grpc-stub**                                  | Provides base classes for stubs and service.            |
| Runtime (network)          | **grpc-netty-shaded**                          | HTTP/2 transport for gRPC calls.                        |
| Runtime (marshalling)      | **grpc-protobuf**                              | Serialize/deserialize Protobuf messages.                |
| Runtime (core message ops) | **protobuf-java**                              | Parsing, building, validation of messages.              |

---

So in simple terms:

* **Build time**: os-maven-plugin + protobuf-maven-plugin.
* **Compile time**: protobuf-java + grpc-stub.
* **Runtime**: grpc-netty-shaded + grpc-protobuf + protobuf-java.

---