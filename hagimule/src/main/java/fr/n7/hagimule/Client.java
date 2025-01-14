package fr.n7.hagimule;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;

public final class Client extends InterfaceClient {

    private static MuleServer serveur;
    private static String adressCli = InetAddress.getLocalHost().getHostName();

    private Client() {
    }

    public void connexion(String adresseServ){
        System.out.println("Starting client...");
        try {
            this.serveur = (MuleServer) Naming.lookup("rmi://" + adresseServ + "/HagimuleServer");
            System.out.println("Found server...");

            System.out.println("Pinging server...");
            String response = s.ping();
            System.out.println("[SERVER] "+response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void telecharger(String nom){
        String[] host = getClientsForFile(nom);
        if (host == null) {
            System.out.println("Fichier introuvables, échec de la requête");
        } else {
            ReceveurClient receveur = new ReceveurClient(host.length, host, nom);
            ReceveurClient.lancement();
        }
    }

    public void partager(String nom){
        try {
            String log = serveur.addFileToDirectory(nom, this.adressCli);
            System.out.println(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

      public static void main(final String[] args) {
        ClientGUI.main(args);
    }

}

