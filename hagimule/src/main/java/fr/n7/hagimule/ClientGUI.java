package fr.n7.hagimule;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Hello world!
 
 */
public final class ClientGUI extends Application {
    public ClientGUI() {
    }

    // // // // Frontend // // // // 
  
    @FXML public Label LabelIP;
    @FXML public Label LabelServerState;

    @FXML
    private void initialize() 
    {
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
       Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
    
        Scene scene = new Scene(root, 600, 400);
    
        stage.setTitle("HagiMule - Projet Intergiciel - L1");
        stage.setScene(scene);
        stage.show();
    }

    // // // // Backend // // // // 
    
    public void startClient(){
        System.out.println("Starting client...");

        try {
            String host_adress = InetAddress.getLocalHost().getHostAddress();
            LabelIP.setText(host_adress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.err.println("Can't access localhost : you messed up something preeeeetty badly here.");
        }

        try {
            // LocateRegistry.getRegistry("localhost",1999);
            // System.out.println("Found registry...");
            MuleServer s = (MuleServer) Naming.lookup("rmi://192.168.43.241:1999/HagimuleServer");
            System.out.println("Found server...");
            LabelIP.setText("Found server...");
            System.out.println("Pinging server...");
            String response = s.ping() ;
            System.out.println("[SERVER] "+response);
        } catch (Exception e) {
            LabelIP.setText("Server not found at 192.168.43.241:1999");
            e.printStackTrace();
        }
    }
}
