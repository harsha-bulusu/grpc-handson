import socket
import time

def run_server(host="0.0.0.0", port=9000, expected=100000):
    server_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_sock.bind((host, port))
    server_sock.listen(1)
    print(f"Server listening on {host}:{port}")

    conn, addr = server_sock.accept()
    print(f"Connection from {addr}")

    count = 0
    start = time.time()

    while True:
        data = conn.recv(4096)
        if not data:
            break
        count += 1
        conn.sendall(b"ACK")

        if count % 10000 == 0:
            print(f"Received {count} messages...")

        if count >= expected:
            break

    end = time.time()
    print(f"Total messages: {count}")
    print(f"Total time: {end - start:.3f} seconds")
    print(f"Throughput: {count / (end - start):.2f} messages/sec")

    conn.close()
    server_sock.close()

if __name__ == "__main__":
    run_server()
