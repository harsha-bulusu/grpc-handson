## 1️⃣ The **contract** is the Remote Interface

* In RMI, **both client and server must share the same `Remote` interface** (e.g., `HelloService`).
* This interface acts as the **contract**:

  * Defines available methods.
  * Declares parameter and return types.
  * Declares that each method throws `RemoteException`.

Example (shared by both):

```java
public interface HelloService extends java.rmi.Remote {
    String sayHello(String name) throws java.rmi.RemoteException;
}
```

---

## 2️⃣ How the client gets this interface

In practice, there are a few ways:

1. **Shared JAR file**

   * You compile the interface into a JAR.
   * Distribute it to both the server and client codebases.
   * The client uses it to compile and call methods.

2. **IDL (Interface Definition Language)** in other RPCs

   * In CORBA, gRPC, Thrift, etc., you define the contract in `.idl` or `.proto` files.
   * Code generators then create language-specific stubs for both client and server.

3. **Dynamic discovery** (not in plain RMI)

   * Some service frameworks allow the client to discover method signatures dynamically at runtime via reflection or metadata services.
   * But plain Java RMI does not have this — it’s compile-time bound.

---

## 3️⃣ Why this is required

When you do:

```java
HelloService service = (HelloService) registry.lookup("HelloService");
service.sayHello("Harsha");
```

The JVM needs:

* **The method signatures** (`sayHello(String)` in this case).
* **The parameter and return types** (so it knows how to serialize/deserialize them).

Without the interface:

* The client wouldn’t compile — it wouldn’t know `sayHello` exists.
* Even if you somehow tried to call it reflectively, you wouldn’t know how to serialize the arguments in a format the server expects.

---

## 4️⃣ Difference from gRPC

* gRPC does not require manually sharing `.java` files — instead, you share a `.proto` file.
* That `.proto` is compiled into Java, Python, Go, etc. stubs for both client and server.
* This makes multi-language communication possible without hand-maintaining interfaces.

