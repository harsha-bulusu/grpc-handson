import socket
import time

def run_server(host="0.0.0.0", port=9000):
    server_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_sock.bind((host, port))
    server_sock.listen(1)
    print(f"Server listening on {host}:{port}")

    conn, addr = server_sock.accept()
    print(f"Connection from {addr}")

    while True:
        data = conn.recv(1024)
        if not data:
            break
        print(f"Received raw bytes: {data.hex()}")
        conn.sendall(b"ACK\n")

    conn.close()
    server_sock.close()
    # time.sleep(10000000)

if __name__ == "__main__":
    run_server()
