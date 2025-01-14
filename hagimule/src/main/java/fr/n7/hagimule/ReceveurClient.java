package fr.n7.hagimule;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
import java.io.ObjectOutputStream;
import java.lang.Long;

public class ReceveurClient extends Thread {
    private static String pathFichier;
    private String[] hosts;
    // private int port;
    public Socket sock;
    private static int nb; // le nombre de sources différentes i.e. le nombre de fragment à recevoir.
    private int frag; // le numeros de fragment traité par le thread en question.
    private File fileOutput;
    private static String filename;
    private static int size;


    public ReceveurClient(int i){
        this.frag = i;
    }

    public ReceveurClient(int nB, String fileName, int fileSize, String[] hosts) {
        nb = nB;
        this.hosts = hosts;
        filename = fileName;
        size = fileSize;
    }

    public void run() {
        try {
            Socket sock = new Socket(hosts[frag], 8083);
            OutputStream os = sock.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(String.valueOf(nb) + ";" + String.valueOf(frag) + ";" + fileOutput.getName());
            // On envoie un string avec nb;frag;nomfichier
            InputStream is = sock.getInputStream();

            // byte[] inputFragHeader = new byte[8];
            // is.read(inputFragHeader, 0, 8);
            // int size = inputFragHeader.longValue().intValue();

            byte[] inputFragCore = new byte[size];
            is.read(inputFragCore);

            FileOutputStream fos = new FileOutputStream(fileOutput);;
            int debut =  frag*( (int) Math.ceil(size/nb));
            int taille = (int) Math.min(Math.ceil(size/nb),size-debut);
            fos.write(inputFragCore, debut, taille);
            is.close();
            fos.close();
            sock.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void lancement() {
        try {
            String pathOutput = "./" + filename;
            File fileOutput = new File(pathOutput);
            Thread t[] = new Thread[nb];
            for (int i=0; i < nb; i++) {
                t[i] = new ReceveurClient(i);
                t[i].start();
            }
            for (int i=0; i < nb; i++) {
                t[i].join();
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
