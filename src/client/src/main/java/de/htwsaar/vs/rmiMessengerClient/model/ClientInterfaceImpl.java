package de.htwsaar.vs.rmiMessengerClient.model;

import de.htwsaar.vs.rmiMessengerClient.controller.LoginSceneController;
import de.htwsaar.vs.rmiMessengerShared.ClientInterface;
import javafx.application.Platform;

import java.rmi.RemoteException;


/**
 * @author Tim Schneider
 * @author Julian Klotz
 * @author Marcel Hesselbach
 * @version 0.2
 */
public class ClientInterfaceImpl implements ClientInterface {


    /**
     * Method refreshChat
     * Called by Server to Notify Client to refresh Message List of Chat
     *
     * @throws RemoteException RMI based Exception
     */
    @Override
    public void refreshChat() throws RemoteException {
        Platform.runLater(() -> LoginSceneController.getChatSceneController()
                .loadAllExistingChatsFromCurrentUserToListView_Chats());
    }

    /**
     * Method displayMessage
     * Called by Server to Notify EndUser with a Message
     *
     * @param message Notify Message String
     * @throws RemoteException RMI based Exception
     */
    @Override
    public void displayMessage(String message) throws RemoteException {
        //ToDo: Funktion Call zum erstellen einer "Standard Benachrichtigung"
    }

    /**
     * Method displayError
     * Called by Server to Notify EndUser with an Error
     *
     * @param message Notify Error String
     * @throws RemoteException RMI based Exception
     */
    @Override
    public void displayError(String message) throws RemoteException {
        //ToDo: Funktion Call zum erstellen einer "Error Benachrichtigung"
    }
}
