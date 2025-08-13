package rpc.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import rpc.common.HelloService;

public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {


    protected HelloServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String sayHello(String name) throws RemoteException {
        return "Hello " + name;
    }
    

}
