package fr.n7.hagimule;

import java.rmi.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Hello world!
 */
public final class Server {
    private Server() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        System.out.println("Starting server...");
        try {
            Registry registry = LocateRegistry.createRegistry(1999);
            ServerImpl server = new ServerImpl();
            Naming.rebind("rmi://localhost:1999/HagimuleServer", (Remote) server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
