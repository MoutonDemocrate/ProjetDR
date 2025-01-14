# <p style="text-align: center;"> HagiMule </p>
__<p style="text-align: center;"> Projets données réparties </p>__
//
//
<p style="text-align: right;"> Bontinck Laérian </p>
<p style="text-align: right;"> Demazure Clément </p>


### Présentation Rapide:
L'objectif du projet est de créer une application de partages de fichiers sur le principe du peer-2-peer en java. 
L'architecture du programme se decompose en 2 parties: 
- Partie Client: Les demons responsables de la fragmentation des fichiers et de l'envoi de ces fragments de fichiers avant de reconstituez le fichier original.
- Partie Serveur: L'annuaire qui gère l'inventaire des clients, de quels fichiers est disponible chez tel client, et derrière quel adresse ce cache le client.

## Qui a fait quoi:
### Laérian:
Mise en place tous les outils satellites au projet:
- Maven pour gerer les dépendances entre les différents fichiers, et pour aider au fonctionnement des autres composants.
- L'interface grafique du client en JavaFX 
- La base de données de l'annuaires en SQL grâce à JavaSQL

Pour que chacuns des composants se comportent normalement, il est important d'utiliser JDK-23.

Laérian a aussi fait l'annuaire a savoir les fichiers: 
- ClientGUI.java sauf les procédures partager et telecharger
- Client.java
- MuleServer.java
- Serveur.java
- ServeurImpl.java

### Clément:
la partie client et les demons associés, nottament les fichiers:
- Envoyeur.java
- Receveur.java
- InterfaceClient.java
- les procedures partager et telecharger de Client.GUI

## Bilan du projet:
Pour lancer le client il suffit de lancer les scripts:
startPlayer.sh et startClient.sh
On a techniquement terminé le codes, notre applications devrait être capable de soumettre des fichiers (Qui sont dans le même dossier que le code) à l'annuaire, de télécharger l'un des fichiers connus par l'annuaire depuis tous les clients répértoriées comme ayant soumis le fichier a l'annuaire.
Mais il reste une erreur de **Connexion refusée** quand un client essaye de se connecter au serveur depuis une machines.
L'application ne produits donc pour l'instant pas de resultats. On peut quand même notés quelques fonctionalitées "bonus":
- Un échange de ping toutes les quelques secondes entre le serveur et tous les clients connéctés pour s'assurer que tous les clients sont toujours disponibles.
- 

## Ce qui reste à faire/Voies d'améliorations:
- Une analyse de performance.
- Une vraie utilisation du service de détection des clients "morts", on pourrait les stockées dans une autres bases de données en attendant leurs reconexion, ou les supprimer de la liste des clients disponibles pour l'envois de paquets. 
