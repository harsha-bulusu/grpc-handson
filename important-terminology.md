# TCP flow
-------------

Client (49306) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Server (9000)
--------------------------------------------------------
SYN --------------------------------------------> \
         <----------------------------------------- SYN, ACK \
ACK ---------------------------------------------> 
\
DATA (29 bytes) -------------------------------->\
         <------------------------------------------- ACK\
         <----------------------------------------- DATA (4 bytes)\
ACK ------------------------------------------->\
\
FIN ---------------------------------------------->\
         <-------------------------------------- ACK\
         <-------------------------------------- FIN\
ACK ---------------------------------------------->

---

## 1. What happens if client/server quits?

### Case A: **Client quits first**

* If client process exits, the **OS kernel closes the client socket**.
* That triggers a **`FIN` → `ACK` → `FIN` → `ACK`** exchange (graceful TCP shutdown).
* On the server:

  * Any **blocking `recv()` call will immediately return `b''`** (EOF).
  * Any **future `send()` on that socket will fail** with `BrokenPipeError` (Python) or `SIGPIPE` in C.

👉 So yes, server’s subsequent `send()` calls will fail.

---

### Case B: **Server quits first**

* If server process exits, the OS closes its socket.
* That triggers the same **`FIN` handshake** from server → client.
* On the client:

  * A blocking `recv()` will return `b''`.
  * A `send()` will fail with **`Connection reset by peer` (RST)** if the server is truly gone.

---

### Case C: **Crash / Kill -9**

* If the process is killed abruptly (`kill -9`), the OS can’t do graceful `FIN`.
* Instead it sends a **`RST` (Reset)** immediately to tear down the connection.
* The peer sees **“Connection reset by peer”** right away.

---

✅ So your statement is correct:

> if client/server quits before, their calls will fail
> Exactly — future calls on that socket either return EOF (`recv`) or raise errors (`send`).

----------

### Mental picture of the timeline

Handshake (1–3)
  |
  v
Data (5–6):  Client sends 10 bytes → Server ACKs
  |
  v
Idle...
  |
Keep-alive cycle starts:
  7–8   @ 6s
  9–10  @ 12s
  11–12 @ 18s
  13–14 @ 24s
  15–16 @ 30s
  17–18 @ 36s


