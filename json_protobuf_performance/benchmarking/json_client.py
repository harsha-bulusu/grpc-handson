import socket
import json
import random
import string
import time

def random_string(length):
    return ''.join(random.choices(string.ascii_letters, k=length))

def random_tags(n):
    return [random_string(5) for _ in range(n)]

def run_client(host="127.0.0.1", port=9000, num_messages=10000):
    client_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_sock.connect((host, port))

    start = time.time()
    for i in range(num_messages):
        # Vary payload size
        msg = {
            "name": random_string(random.randint(5, 50)),
            "age": random.randint(18, 80),
            "email": random_string(random.randint(5, 20)) + "@test.com",
            "tags": random_tags(random.randint(0, 10))
        }
        payload = json.dumps(msg).encode("utf-8")
        client_sock.sendall(payload)
        client_sock.recv(3)  # "ACK"
    end = time.time()

    print(f"JSON Benchmark: Sent {num_messages} messages")
    print(f"Total time: {end - start:.3f} seconds")
    print(f"Throughput: {num_messages / (end - start):.2f} messages/sec")

    client_sock.close()

if __name__ == "__main__":
    run_client()
