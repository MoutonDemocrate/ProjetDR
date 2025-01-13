package fr.n7.hagimule;
import java.rmi.*;

public interface MuleServer extends Remote {

    public String ping() throws RemoteException;

    public String addFileToDirectory(String filename, int size, String client_adress) throws RemoteException;

    public String[] getClientsForFile(String filename) throws RemoteException;
    
}
