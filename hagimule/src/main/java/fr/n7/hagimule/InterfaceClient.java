package fr.n7.hagimule;

public interface InterfaceClient{

    void connexion(String adresse);//172.22.225.120:1999

    void telecharger(String nomFichier);

    void partager(String nomFichier);
}
