package rpc.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import rpc.common.HelloService;


public class RMIClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            HelloService service = (HelloService) registry.lookup("HelloService");
            String response = service.sayHello("1");
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
