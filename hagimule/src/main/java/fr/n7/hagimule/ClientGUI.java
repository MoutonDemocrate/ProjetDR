package fr.n7.hagimule;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import fr.n7.hagimule.MuleServer.FileInfo;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Hello world!
 
 */
public final class ClientGUI extends Application implements InterfaceClient {
    public ClientGUI() {
    }

    // // // // Frontend // // // // 
  
    @FXML public Label LabelIP;
    @FXML public Label LabelServerState;

    @FXML public TextArea TextAreaPort;
    @FXML public TextArea TextAreaServer;

    @FXML public TableView<LocalFileInfo> localTable;
    @FXML public TableView<MuleServer.FileInfo> serverTable;

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
    
    public MuleServer server;
    private String selfAdress;
    public ObservableList<MuleServer.FileInfo> filesList;

    private class LocalFileInfo {
        public String Name;
        public Integer Size;
        public LocalFileInfo(String name, Integer size) {
            this.Name = name;
            this.Size = size;
        }    
    }

    @FXML
    public void startClient(){
        System.out.println("Starting client...");

        try {
            selfAdress = InetAddress.getLocalHost().getHostAddress();
            LabelIP.setText(selfAdress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.err.println("Can't access localhost : you messed up something preeeeetty badly here.");
        }

        System.out.println("System adress found, looking for server...");

        try {
            server = (MuleServer) Naming.lookup("rmi://"+TextAreaServer.getText()+":"+TextAreaPort.getText()+"/HagimuleServer");
            System.out.println("Found server...");
            LabelServerState.setText("Found server...");
            System.out.println("Pinging server...");
            String response = server.ping() ;
            System.out.println("[SERVER] "+response);
            Timer t = new Timer();
            t.scheduleAtFixedRate(
                new TimerTask() {public void run() {fetchServerFiles();}},
                0,      // run first occurrence immediately
                20000);  // run every 20 seconds
        } catch (MalformedURLException e) {
            LabelServerState.setText("Server not found at "+TextAreaServer.getText()+":"+TextAreaPort.getText()+" !");
            e.printStackTrace();
            System.err.println("RMI Error : Server not found, malformed adress.");
        } catch (NotBoundException e) {
            LabelServerState.setText("Server not bound at "+TextAreaServer.getText()+":"+TextAreaPort.getText()+" !");
            e.printStackTrace();
            System.err.println("Not Bound : Server was found but isn't active.");
        } catch (RemoteException e) {
            e.printStackTrace();
            System.err.println("Remote Exception : Server is either offline or call was incorrect.");
        }
    }

    public void fetchServerFiles(){
        try {
            ArrayList<MuleServer.FileInfo> files = server.getAllFiles();
            System.out.println("Receiving files : " + files.toString());
            filesList = FXCollections.observableArrayList(files);
            serverTable.setItems(null);
            ObservableList<TableColumn<MuleServer.FileInfo, ?>> cols = serverTable.getColumns();
            cols.get(0).setCellValueFactory(new PropertyValueFactory<>("Name"));
            cols.get(1).setCellValueFactory(new PropertyValueFactory<>("Size"));
            cols.get(2).setCellValueFactory(new PropertyValueFactory<>("Hosters"));
            serverTable.setItems(filesList);
        } catch (RemoteException e) {
            e.printStackTrace();
            System.err.println("Server error : can't call getAllFiles() method.");
        }
    }

    public void startDownload(){
        try {
            MuleServer.FileInfo file = serverTable.getSelectionModel().getSelectedItem();
            System.out.println("Trying to download file : "+file.Name);
            telecharger(file.Name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void addFileToShare() {
        TextInputDialog tid = new TextInputDialog("");
        tid.setTitle("HagiMule - Add File");
        tid.setHeaderText("Input file name");
        tid.setContentText("The file must be in the same folder !");
        Optional<String> result = tid.showAndWait();
        result.ifPresent(name -> partager(name));

    }

    // // // //  TCP  // // // //

    public void telecharger(String nom){
        String[] hosts = null;
        int size = -1;
        try {
            hosts = server.getClientsForFile(nom);
            size = server.getSizeForFile(nom);
        } catch (RemoteException e) {
            e.printStackTrace();
            System.err.println("There was an error trying to interact with the server.");
        }
        if (hosts == null) {
            System.out.println("Fichier introuvables, échec de la requête");
        } else {
            ReceveurClient receveur = new ReceveurClient(hosts.length, nom, size, hosts);
            ReceveurClient.lancement();
        }
    }

    public void partager(String nom){
        try {
            File file = new File("./"+nom);
            Long size = file.length();
            String log = server.addFileToDirectory(nom, size.intValue() ,selfAdress);
            System.out.println(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
