import socket
import person_pb2  # generated from .proto

def run_client(host="127.0.0.1", port=9000):
    client_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_sock.connect((host, port))

    # Create Protobuf object
    person = person_pb2.Person()
    person.name = "Harsha"
    person.age = 24

    proto_bytes = person.SerializeToString()

    print(f"Sending Protobuf bytes: {proto_bytes.hex()}")
    client_sock.sendall(proto_bytes)

    ack = client_sock.recv(1024)
    print(f"Received from server: {ack}")

    client_sock.close()

if __name__ == "__main__":
    run_client()
