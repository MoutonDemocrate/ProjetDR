package fr.n7.hagimule;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Math;
import java.io.File;

public class EnvoyeurClient extends Thread{

	public Socket sock;

    public EnvoyeurClient(Socket sock) {
        this.sock = sock;
    }

    //thanks to Wytze form stackOverflows: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
    // public static byte[] longToBytes(long l) {
    // byte[] result = new byte[Long.BYTES];
    // for (int i = Long.BYTES - 1; i >= 0; i--) {
    //     result[i] = (byte)(l & 0xFF);
    //     l >>= Byte.SIZE;
    // }
    // return result;
    // }

    public void run() {
        try {
            OutputStream os = sock.getOutputStream();
            InputStream is = sock.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            String demande = (String) ois.readObject();
            String[] tokens = demande.split(";");
            int nb = Integer.parseInt(tokens[0]);
            int frag = Integer.parseInt(tokens[1]);

			File fileInput = new File(tokens[2]);
			FileInputStream fis = new FileInputStream(fileInput);
			Long lengthLong = fileInput.length();
			int length = lengthLong.intValue();

            int debut =  frag*( (int) Math.ceil(length/nb));
            int taille = (int) Math.min(Math.ceil(length/nb),length-debut);

            // byte[] inputHeader = longToBytes(lengthLong);
            // os.write(inputHeader, 0, 8);

            byte[] inputCore = new byte[(int) taille];
			fis.skip(debut);

			fis.read(inputCore);
            os.write(inputCore);
            os.close();
            fis.close();
            is.close();
            sock.close();}
        catch (Exception e) {
            System.out.println(e);
        }
    }


    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(8083);
            while (true) {
                Socket clientSocket = s.accept();
                Thread t = new EnvoyeurClient(clientSocket);
                t.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

