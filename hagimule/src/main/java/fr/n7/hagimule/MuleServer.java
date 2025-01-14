package fr.n7.hagimule;
import java.io.Serializable;
import java.rmi.*;
import java.rmi.server.RemoteObject;
import java.util.ArrayList;

public interface MuleServer extends Remote {
    public class FileInfo implements Serializable {
        public String Name;
        public String getName() {return this.Name;};
        public Integer Size;
        public Integer getSize() {return this.Size;};
        public String Hosters;
        public String getHosters() {return this.Hosters;};
        public FileInfo(String name, Integer size, String hosters) {
            this.Name = name;
            this.Size = size;
            this.Hosters = hosters;
        }
    }
    
    public String ping() throws RemoteException;

    public String addFileToDirectory(String filename, int size, String client_adress) throws RemoteException;

    public String[] getClientsForFile(String filename) throws RemoteException;

    public int getSizeForFile(String filename) throws RemoteException;

    public ArrayList<FileInfo> getAllFiles() throws RemoteException;
    
    // Returns false if client was considered "dead" before refresh
    public Boolean refreshPresence(String client_adress) throws RemoteException;
}
