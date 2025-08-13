package rpc.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import rpc.common.HelloService;

public class RMIServer {
    public static void main(String[] args) {
        try {
            HelloService service = new HelloServiceImpl();
            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("HelloService", service);

            System.out.println("RMI server is running");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
