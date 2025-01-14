package fr.n7.hagimule;

import java.rmi.server.*;
import java.rmi.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class ServerImpl extends UnicastRemoteObject implements MuleServer {

    public ServerImpl() throws RemoteException {
        System.out.println("Searching for directory file...");

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

        System.out.println("Server instance created...");
    }

    // // // // Clients & States // // // //

    private class ClientState {
        String Adress;
        Boolean Dead;
        Long LastTimeSeen;
        public ClientState(String adress,
            Boolean dead) {
                this.Adress = adress;
                this.Dead = dead;
                this.LastTimeSeen = System.currentTimeMillis();
        }
    }

    private ClientState[] clients = {};

    // Returns -1 if client not in clients array
    private Integer getClientIndex(String client_adress) {
        Integer i = 0;
        while (i < clients.length) {
            if (clients[i].Adress == client_adress) {
                return i;
            }
            i += 1;
        }
        return -1;
    }

    @Override
    public Boolean refreshPresence(String client_adress) throws RemoteException {
        // If client is in list
        Integer index = getClientIndex(client_adress) ;
        if (index != -1) {
            clients[index].LastTimeSeen = System.currentTimeMillis();
            if (clients[index].Dead) {
                clients[index].Dead = false;
                return false;
            }
            return true;
        } else {
            clients[clients.length] = new ClientState(client_adress, false);
            return true;
        }
    }

    // On crée un timer qui va exécuter un tâche toutes les 10 secondes
    public class Taskn extends TimerTask {
        public void main(String args[]){
            TimerTask timerTask = new Taskn(); //reference created for TimerTask class
             
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(timerTask, 0, 20000); // 1.task 2.delay 3.period
        }
        public void run(){
            System.out.println("Checking for dead clients...");
            Long time = System.currentTimeMillis();
            for (ClientState clientState : clients) {
                if ((time - clientState.LastTimeSeen) >= 20000) {
                    clientState.Dead = true;
                }
            }
        }
    }

    // // // // SQL and Requests // // // //

    private Connection sqlConnection;

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
