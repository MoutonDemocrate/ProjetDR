package fr.n7.hagimule;

import java.rmi.server.*;
import java.rmi.*;

public class ServerImpl extends UnicastRemoteObject implements MuleServer {

    public ServerImpl() throws RemoteException {
        // System.setProperty("java.rmi.server.hostname","192.128.0.1");
        System.out.println("Server created !");
    }

    public int maMethode() throws RemoteException {
        return 0;
    }

    @Override
    public String ping() throws RemoteException {
        System.out.println("Ping called !");
        return "Server online.";
    }
}
