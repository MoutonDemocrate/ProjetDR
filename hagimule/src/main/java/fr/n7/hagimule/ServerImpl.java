package fr.n7.hagimule;

import org.json.*;
import java.rmi.server.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.*;
import java.sql.*;
import java.util.Arrays;

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

        String url = "jdbc:sqlite:files.db";

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

    private void insertNew(String name, int size, String client_adress) throws SQLException {
        Statement st = sqlConnection.createStatement();
        st.executeQuery("insert into files values (" + name + "," + String.valueOf(size) + "," + client_adress + ")");
    }

    @Override
    public String ping() throws RemoteException {
        System.out.println("Ping called !");
        return "Server online.";
    }

    @Override
    public String addFileToDirectory(String filename, int size, String client_adress) throws RemoteException {
        // JSONArray client_array;
        // if (directory.has(filename)) {
        //     client_array = (JSONArray) directory.get(filename);
        //     client_array.put(client_adress);
        //     directory.put(filename, client_array);
        //     return "Added client for " + filename + "...";
        // } else {
        //     client_array = new JSONArray();
        //     client_array.put(client_adress);
        //     directory.put(filename, client_array);
        //     return "Created new entry for " + filename + "...";
        // }
        try {
            String q = String.format("SELECT DISTINCT files FROM files WHERE name='%s';",filename);
            ResultSet rs = query(q);
            // If file is in database
            while (rs.next()) {
                String hosters = rs.getString("hosters");
                // If client is not already a hoster
                if (!Arrays.stream(hosters.split(";")).anyMatch("client_adress"::equals)) {
                    hosters += ";" + client_adress ;
                    PreparedStatement st = sqlConnection.prepareStatement("UPDATE item SET hosters = ?, WHERE name = ?");
                    st.setString(1, hosters);
                    st.setString(2, filename);
                    st.executeUpdate();
                    return "Client succesfully added as hoster.";
                } else {
                    // Client is already a hoster.
                    return "Client is already a hoster.";
                }
            }
            // If file is not in the database
            insertNew(filename, size, client_adress);
            return "File added in database, with client as hoster.";
        } catch (SQLException e) {
            return "There was an error with the database.";
        }
    }

    @Override
    public String[] getClientsForFile(String filename) throws RemoteException {
        System.out.println("Client requested : " + filename);
        String hosters = "";
        try {
            String q = String.format("SELECT DISTINCT hosters FROM files WHERE name='%s';",filename);
            ResultSet rs = query(q);
            while (rs.next()) {
                hosters = rs.getString("hosters");
                return hosters.split(";");
            }
            return new String[]{};
        } catch (SQLException e) {
            return new String[]{};
        }
    }
}
