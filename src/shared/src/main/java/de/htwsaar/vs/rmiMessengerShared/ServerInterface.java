package de.htwsaar.vs.rmiMessengerShared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @author Marcel Hesselbach (kib.marcel.hesselbach)
 * @author Amir Savari (kib.amir.savari)
 * @version 0.1
 * @since 0.1
 */
public interface ServerInterface extends Remote {

    /**
     * Registers a User and returns registered UserObject for further Clientside usage
     *
     * @param username username of new User
     * @param password hashed Password of new User
     * @throws RemoteException          RMI based Exception
     * @throws ServerInterfaceException if not possible to create User
     */
    void registerUser(String username, String password, ClientInterface clientInterface) throws RemoteException, ServerInterfaceException;

    /**
     * Users Login and return of logged-in User Object for further Clientside usage
     *
     * @param username username of User
     * @param password hashed password of User
     * @return logged in User Object
     * @throws RemoteException          RMI based Exception
     * @throws ServerInterfaceException Messenger Server based Exception
     */
    User loginUser(String username, String password, ClientInterface clientInterface) throws RemoteException, ServerInterfaceException;

    /**
     * Updates User Attributes after password / username change
     *
     * @param user Returns updates User Object
     * @throws RemoteException RMI based Exception
     */
    void updateUser(User user) throws RemoteException, ServerInterfaceException;


    /**
     * Disconnects Client Callback Interface from Server
     *
     * @param user User Object of logged-out user
     * @throws RemoteException RMI based Exception
     */
    void disconnectUser(User user) throws RemoteException;

    /**
     * Gets List of Chats for User
     * @param user  Logged in User
     * @return List with Chats from User
     * @throws RemoteException  RMI based Exception
     */
    List<Chat> getChatList(User user) throws RemoteException, ServerInterfaceException;

    /**
     * Get Messages of Chat for User
     *
     * @param chat affected Chat
     * @return List with Messages of Chat
     * @throws RemoteException RMI based Exception
     */
    List<Message> getChatMessageList(Chat chat) throws RemoteException, ServerInterfaceException;

    /**
     * Adds Message to Chat
     * Creates Message Object and adds Relation to Chat
     *
     * @param payload payload of the Message
     * @param chat    affected Chat Object
     * @param user    affected User Object
     * @throws RemoteException RMI based Exception
     */
    void addMessageToChat(String payload, Chat chat, User user) throws RemoteException, ServerInterfaceException;

    /**
     * Removes Message from Chat
     * Removes Message Object and removes Relation to Chat
     *
     * @param message   affected Message Object
     * @throws RemoteException RMI based Exception
     */
    void removeMessageFromChat(Message message) throws RemoteException, ServerInterfaceException;

    /**
     * Adds Chat to Server
     * Created Chat Object with specified Name
     *
     * @param chatName name of new Chat
     * @throws RemoteException RMI based Exception
     */
    Chat addChat(String chatName) throws RemoteException, ServerInterfaceException;

    /**
     * Adds User to Chat
     *
     * @param user affected User
     * @param chat affected Chat
     * @throws RemoteException RMI based Exception
     */
    void addParticipantToChat(User user, Chat chat) throws RemoteException, ServerInterfaceException;

    /**
     * Removes User from Chat
     *
     * @param user affected User
     * @param chat affected Chat
     * @throws RemoteException RMI based Exception
     */
    void removeParticipantFromChat(User user, Chat chat) throws RemoteException, ServerInterfaceException;

    /**
     * Gets all registered Users from Server
     *
     * @return List with all Users
     * @throws RemoteException RMI based Exception
     */
    List<User> getAllUsers() throws RemoteException, ServerInterfaceException;
}
