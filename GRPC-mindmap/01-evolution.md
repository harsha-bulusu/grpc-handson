Alright, let’s unpack this mental model step-by-step and see **why RPC (classic and gRPC) → try to hide remote nature**, **why REST didn’t**, and **why this matters today**.

---

## **1. The core obsession of RPC: “Make remote feel local”**

From the early days of distributed computing (1980s onward), developers hated that calling a function on another machine required:

* Knowing a **hostname and port**
* Writing **networking boilerplate** (socket connect, send, receive)
* Serializing/deserializing data
* Handling **network errors differently** from local errors

💡 *The dream*:
“What if calling a method on a machine across the world felt just like calling a method in your own process?”

So RPC introduced **stubs** (client) and **skeletons** (server):

* Client stub looked like a local method, but internally did:

  * Serialize arguments → send over TCP
  * Wait for response → deserialize
* Developer **never touched hostname/port directly** (it’s hidden in stub metadata or service registry)
* You could literally write:

  ```java
  Order order = orderService.getOrderById(42);
  ```

  …and this could be running on a server in another datacenter.

The **goal** was **transparency** — hide the network, make it “just another function call.”

---

## **2. Why REST broke that pattern**

REST arrived in the 2000s and said:

> “Stop pretending the network is local — embrace it!”

REST embraced the **web’s resource model**:

* Explicit **URLs**: `https://api.example.com/orders/42`
* Explicit **HTTP verbs**: `GET`, `POST`, `PUT`, `DELETE`
* Data represented in **JSON/XML** (human-readable)
* Stateless interactions: each request is self-contained
* You know you are doing a *remote* call (clear mental separation between local code and remote service)

This gave **loose coupling**:

* Services could be rewritten in another language/framework.
* Clients just needed to know the URL and data format — no tight stub binding.

📌 REST’s philosophy: **“Make network calls explicit”** (visible URLs, HTTP methods), even if it means more boilerplate for the developer.

---

## **3. Why gRPC came back to RPC-style thinking**

Around 2015, Google took the learnings from internal Stubby and released gRPC:

* Still **stub-based**: call remote functions like local functions.
* But **built for modern distributed systems**:

  * Protocol Buffers (binary, fast, typed)
  * HTTP/2 (multiplexing, bidirectional streaming)
  * Strong contracts (proto files)
  * Cross-language code generation

Why not REST? Because in large-scale microservices:

* Teams want **strongly typed APIs** (avoid “guessing JSON fields”)
* Need **streaming** for real-time communication
* Need **efficiency** in serialization for high QPS
* Often operate in **trusted internal networks** where hostname/port is managed by service discovery

So gRPC **went back** to “remote = local call” mindset, but with modern features.

---

## **4. Why no `hostname:port` in RPC/gRPC calls**

When you write:

```java
Order order = orderService.getOrderById(42);
```

You don’t see `api.company.com:50051` because:

* **Service discovery** (Consul, Kubernetes DNS, etc.) resolves the actual host/port
* In RPC/gRPC world, **location transparency** is a design goal:

  * Services may move between machines
  * Multiple replicas may exist
  * You may need load balancing without changing client code
* Host\:port is **operational concern**, not a developer concern

---

## **5. Mental model summary**

Think of **RPC vs REST** as two ends of a spectrum:

| Concept            | RPC / gRPC                             | REST                          |
| ------------------ | -------------------------------------- | ----------------------------- |
| **Philosophy**     | Hide remote nature, make it look local | Make remote nature explicit   |
| **Interface**      | Methods defined in IDL (stubs)         | URLs + HTTP verbs             |
| **Data**           | Binary or structured (Protobuf)        | Text-based (JSON/XML)         |
| **Typing**         | Strong (compile-time)                  | Loose (runtime parsing)       |
| **Coupling**       | Tight (codegen needed for client)      | Loose (just parse response)   |
| **Hostname\:Port** | Hidden, via service discovery          | Explicit in every call        |
| **Streaming**      | Built-in (HTTP/2 streams)              | Limited (WebSockets as extra) |

---

## **6. Why REST stood different and still survives**

* **Interoperability**: No stub code generation needed — just HTTP.
* **Debuggability**: Curl or browser can hit an endpoint.
* **Web-native**: Works with browsers, proxies, caches.
* **Loosely coupled**: Teams can evolve independently without regen stubs.

It’s slower, less strict, but much more **open and forgiving** — which is why public APIs mostly use REST, while internal service-to-service calls in big companies lean toward gRPC.

---