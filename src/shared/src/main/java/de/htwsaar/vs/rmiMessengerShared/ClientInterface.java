package de.htwsaar.vs.rmiMessengerShared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Client Callback Interface
 */
public interface ClientInterface extends Remote {

    /**
     * Method refreshChat
     * Called by Server to Notify Client to refresh Message List of Chat
     *
     * @throws RemoteException RMI based Exception
     */
    void refreshChat() throws RemoteException;

    /**
     * Method displayMessage
     * Called by Server to Notify EndUser with a Message
     *
     * @param message Notify Message String
     * @throws RemoteException RMI based Exception
     */
    void displayMessage(String message) throws RemoteException;

    /**
     * Method displayError
     * Called by Server to Notify EndUser with an Error
     *
     * @param message Notify Error String
     * @throws RemoteException RMI based Exception
     */
    void displayError(String message) throws RemoteException;

}
