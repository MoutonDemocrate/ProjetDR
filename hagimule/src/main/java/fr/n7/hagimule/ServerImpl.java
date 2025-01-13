package fr.n7.hagimule;

import org.json.*;
import java.rmi.server.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.*;
import java.sql.*;

import javax.naming.spi.DirStateFactory.Result;

public class ServerImpl extends UnicastRemoteObject implements MuleServer {

    private JSONObject directory;
    private Connection sqlConnection;

    public ServerImpl() throws RemoteException {
        System.out.println("Searching for directory file...");
        // Path filePath = Path.of("directory.json");
        String content = "";

        // Chargement du driver SQL
        // Class.forName("org.sqlite.JDBC");

        String url = "jdbc:sqlite:test.db";

        try {
            sqlConnection = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        

        try {
            String q = "SELECT * FROM files;";
            ResultSet rs = query(q);
            while (rs.next()) {
                System.out.println((String.valueOf(rs.getRow()))
                + " " + rs.getString("name") 
                + " " + String.valueOf(rs.getInt("size")) 
                + " " + rs.getString("hosters"));
            }
        } catch (SQLException e) {
            System.err.println("--- SQL ERROR ---");
            e.printStackTrace();
            System.err.println("---           ---");
        }

        // try {
        //     ResultSet rs = query("SELECT * FROM files;");
        //     while (rs.next()) {
        //         System.out.println(rs.getString("name"));
        //     }
        // } catch (SQLException e) {
        //     System.err.println("--- SQL ERROR ---");
        //     e.printStackTrace();
        //     System.err.println("---           ---");
        // }

        // try {
        //     content = Files.readString(filePath);
        // } catch (IOException e) {
        //     System.out.println("Directory not found in local folder !");
        //     try {
        //         Files.createFile(filePath);
        //         Files.writeString(filePath,"{}");
        //         System.out.println("Creating directory...");
        //     } catch (IOException e2) {
        //         System.out.println("Can't create directory at path which allows directory creation. RUN.");
        //     }
        // }

        // System.out.println("Directory found.");

        // try {
        //     directory = new JSONObject(content);
        // } catch (JSONException e) {
        //     System.out.println("Invalid JSON !");
        //     e.printStackTrace();
        // }

        System.out.println("Server instance created...");
    }

    private ResultSet query(String sql) throws SQLException {
        Statement st = sqlConnection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        return rs;
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
