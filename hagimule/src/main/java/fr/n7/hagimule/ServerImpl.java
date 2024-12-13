package fr.n7.hagimule;

import org.json.*;
import java.rmi.server.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.*;

public class ServerImpl extends UnicastRemoteObject implements MuleServer {

    private JSONObject directory;

    public ServerImpl() throws RemoteException {
        System.out.println("Searching for directory file...");
        Path filePath = Path.of("directory.json");
        String content = "";

        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            System.out.println("Directory not found in local folder !");
            try {
                Files.createFile(filePath);
                Files.writeString(filePath,"{}");
                System.out.println("Creating directory...");
            } catch (IOException e2) {
                System.out.println("Can't create directory at path which allows directory creation. RUN.");
            }
        }

        System.out.println("Directory found.");

        try {
            directory = new JSONObject(content);
        } catch (JSONException e) {
            System.out.println("Invalid JSON !");
            e.printStackTrace();
        }

        System.out.println("Server instance created...");
    }

    public int maMethode() throws RemoteException {
        return 0;
    }

    @Override
    public String ping() throws RemoteException {
        System.out.println("Ping called !");
        return "Server online.";
    }

    @Override
    public String addFileToDirectory(String filename, String client_adress) throws RemoteException {
        JSONArray client_array;
        if (directory.has(filename)) {
            client_array = (JSONArray) directory.get(filename);
            client_array.put(client_adress);
            directory.put(filename, client_array);
            return "Added client for " + filename + "...";
        } else {
            client_array = new JSONArray();
            client_array.put(client_adress);
            directory.put(filename, client_array);
            return "Created new entry for " + filename + "...";
        }
    }

    @Override
    public String[] getClientsForFile(String filename) throws RemoteException {
        System.out.println("Client requested : " + filename);
        if (directory.has(filename)) {
            JSONArray client_array = (JSONArray) directory.get(filename);
            System.out.println("File found, returning clients...");
            return (String[]) client_array.toList().toArray();
        } else {
            System.out.println("File not present in directory ! Returning null...");
            return null;
        }
    }
}
