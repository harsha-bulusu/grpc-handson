import socket
import json
import time

def run_client(host="127.0.0.1", port=9000):
    client_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_sock.connect((host, port))

    message = {"name": "Harsha", "age": 24}
    json_bytes = json.dumps(message).encode("utf-8")

    print(f"Sending JSON bytes: {json_bytes}")
    client_sock.sendall(json_bytes)

    ack = client_sock.recv(1024)
    print(f"Received from server: {ack}")

    client_sock.close()
    # time.sleep(10000000)

if __name__ == "__main__":
    run_client()
