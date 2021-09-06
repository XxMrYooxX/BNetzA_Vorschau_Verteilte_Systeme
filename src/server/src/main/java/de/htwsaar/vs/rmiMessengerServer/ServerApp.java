package de.htwsaar.vs.rmiMessengerServer;

import de.htwsaar.vs.rmiMessengerShared.ServerInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Julian Müller
 * @version 1.0
 * @since 1.0
 */
public class ServerApp {
    /**
     * Erstellung Serverinterface
     * Bindet rmi Name auf rmi Chatserver
     *
     * @param args Keine Argumente vorhanden
     */
    public static void main(String[] args) {
        try {
            //String pathToImage = "../../../client/java/de/htwsaar/vs/rmiMessengerClient/assets/customer_52px.png";
            ServerInterfaceImpl server = new ServerInterfaceImpl();
            System.setProperty("java.rmi.server.hostname", "localhost");
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);
            LocateRegistry.createRegistry(42424);
            Registry registry = LocateRegistry.getRegistry(42424);
            registry.bind("ServerInterface", stub);
            //Naming.rebind("rmichatserver", server);
            System.out.println("Server ready, waiting for Clients...");


            //User user = new User("username", "password");
            //Message message = new Message("testMessage");
            //Chat chat = new Chat("testChat");


        } catch (Exception e){
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }
}