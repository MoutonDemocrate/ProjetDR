package fr.n7.hagimule;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * Hello world!
 */
public final class Client {
    private Client() {
    }

    /**
     * Says hello to the world.
     * 
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        System.out.println("Starting client...");
        try {
            // LocateRegistry.getRegistry("localhost",1999);
            // System.out.println("Found registry...");
            MuleServer s = (MuleServer) Naming.lookup("rmi://172.22.225.120:1999/HagimuleServer");
            System.out.println("Found server...");
            System.out.println("Pinging server...");
            String response = s.ping("Yo !") ;
            System.out.println("[SERVER] "+response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
