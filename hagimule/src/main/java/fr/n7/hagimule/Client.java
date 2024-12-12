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
            MuleServer s = (MuleServer) Naming.lookup("rmi://localhost:1999/HagimuleServer");
            System.out.println("Found server...");
            System.out.println(s.ping()); // 0
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
