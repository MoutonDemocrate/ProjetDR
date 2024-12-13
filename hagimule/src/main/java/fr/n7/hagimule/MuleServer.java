package fr.n7.hagimule;
import java.rmi.*;

public interface MuleServer extends Remote {

    public String ping() throws RemoteException;

    
    public int maMethode() throws RemoteException;
    
}
