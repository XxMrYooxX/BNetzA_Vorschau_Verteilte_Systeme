package de.htwsaar.vs.rmiMessengerServer;

import de.htwsaar.vs.rmiMessengerShared.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Marcel Hesselbach (kib.marcel.hesselbach)
 * @author Amir Savari (kib.amir.savari)
 * @author Julian Müller
 * @author Alexander Tröger
 * @version 0.1
 * @since 0.1
 */
public class ServerInterfaceImpl implements ServerInterface, Serializable {

    /**
     * Zur Registrierung von verbundenen Usern und deren ClientInterface
     */
    private static HashMap<User, ClientInterface> clientHashMap;

    private final List<String> forbiddenUsernames = new ArrayList<>();

    /**
     * Konstruktor eines ServerInterface
     * initialisiert ClientHashMap und activeClientInterfaces
     * setzt zudem Listener auf activeChatInterfaces zum Tracken von Änderungen (Chat Add/Remove/Change)
     *
     * @throws RemoteException the remote exception
     */
    public ServerInterfaceImpl() throws RemoteException {
        this.forbiddenUsernames.add("Eschkalation");        // Hey, I'm just an Example :)
        clientHashMap = new HashMap<>();
    }


    /**
     * Registers a User and returns registered UserObject for further Clientside usage
     *
     * @param username username of new User
     * @param password hashed Password of new User
     * @throws RemoteException          RMI based Exception
     * @throws ServerInterfaceException if not possible to create User
     */
    @Override
    public void registerUser(String username, String password, ClientInterface clientInterface) throws RemoteException, ServerInterfaceException {
        Session session = HibernateSessionHandler.getSession();
        EntityManager entityManager = HibernateSessionHandler.getEntityManager();
        try {
            for (String forbiddenUsername : forbiddenUsernames) {
                if (forbiddenUsername.matches(".*" + username + ".*")) {
                    throw new ServerInterfaceException("ServerInterfaceException:\tForbidden Username: " + username);
                }
            }
            session.beginTransaction();
            CriteriaQuery<User> userCriteriaQuery = session.getCriteriaBuilder().createQuery(User.class);
            Root<User> userRoot = userCriteriaQuery.from(User.class);
            userCriteriaQuery.select(userRoot);
            userCriteriaQuery.where(session.getCriteriaBuilder().equal(userRoot.get("username"), username));
            if (!session.createQuery(userCriteriaQuery).getResultList().isEmpty()) {
                throw new ServerInterfaceException("ServerInterfaceException:\tUsername already exists: " + username);
            }
            session.getTransaction().commit();
        } catch (HibernateException hibernateException) {
            hibernateException.printStackTrace();
            session.getTransaction().rollback();
            throw new ServerInterfaceException("ServerInterfaceException: Unable to register User");
        } catch (ServerInterfaceException serverInterfaceException) {
            serverInterfaceException.getMessage();
            serverInterfaceException.printStackTrace();
            session.getTransaction().rollback();
            throw new ServerInterfaceException("ServerInterfaceException: Unable to register User");
        } finally {
            session.close();
        }
        try {
            entityManager.getTransaction().begin();
            User tmpUser = new User(username, password);
            entityManager.persist(tmpUser);
            entityManager.getTransaction().commit();
            clientInterface.displayMessage("Benutzer " + tmpUser.getUsername() + " erfolgreich registriert.");
        } catch (PersistenceException persistenceException) {
            persistenceException.printStackTrace();
            entityManager.getTransaction().rollback();
            throw new ServerInterfaceException("ServerInterfaceException: Unable to register User");
        } finally {
            entityManager.close();
        }

    }

    /**
     * Users Login and return of logged-in User Object for further Clientside usage
     *
     * @param username username of User
     * @param password hashed password of User
     * @return logged in User Object
     * @throws RemoteException          RMI based Exception
     * @throws ServerInterfaceException if not possible to Login User
     */
    @Override
    public User loginUser(String username, String password, ClientInterface clientInterface) throws RemoteException, ServerInterfaceException {
        Session session = HibernateSessionHandler.getSession();
        try {
            // Beginn der Transaktion zum Checken ob Username existiert
            session.beginTransaction();
            CriteriaQuery<User> userCriteriaQuery = session.getCriteriaBuilder().createQuery(User.class);
            Root<User> userRoot = userCriteriaQuery.from(User.class);
            userCriteriaQuery.select(userRoot);

            // Predicate zum Kombinieren der beiden Kriterien
            Predicate predicateForUsername = session.getCriteriaBuilder().equal(userRoot.get("username"), username);
            Predicate predicateForPassword = session.getCriteriaBuilder().equal(userRoot.get("password"), password);
            Predicate predicateFinal = session.getCriteriaBuilder().and(predicateForUsername, predicateForPassword);

            userCriteriaQuery.where(predicateFinal);
            List<User> tmpUserInfo = session.createQuery(userCriteriaQuery).getResultList();
            if (tmpUserInfo.isEmpty()) {
                throw new ServerInterfaceException("ServerInterfaceException:\tUsername doesnt exists or Password wrong: " + username);
            }
            session.getTransaction().commit();

            // Adds Callback Interface to Server
            connectUser(clientInterface, tmpUserInfo.get(0));

            return tmpUserInfo.get(0);
        } catch (HibernateException hibernateException) {
            hibernateException.printStackTrace();
            session.getTransaction().rollback();
        } catch (ServerInterfaceException serverInterfaceException) {
            serverInterfaceException.getMessage();
            serverInterfaceException.printStackTrace();
            throw new ServerInterfaceException("ServerInterfaceException:\tUsername doesnt exists or Password wrong: " + username);
        } finally {
            session.close();
        }
        throw new ServerInterfaceException("Login couldn't be handled");
    }

    /**
     * Updates User Attributes after password / username change
     *
     * @throws RemoteException          RMI based Exception
     * @throws ServerInterfaceException if unable to update User
     */
    //TODO: Chat namen Updaten bei umbenennen des users
    @Override
    public void updateUser(User user) throws RemoteException, ServerInterfaceException {
        Session tmpSession = HibernateSessionHandler.getSession();
        try {
            tmpSession.beginTransaction();
            tmpSession.saveOrUpdate(user);
            tmpSession.getTransaction().commit();
        } catch (HibernateException hibernateException) {
            hibernateException.printStackTrace();
            tmpSession.getTransaction().rollback();
            throw new ServerInterfaceException("ServerInterfaceException: Unable to update User Object");
        } finally {
            tmpSession.close();
        }
    }

    /**
     * Fügt sich verbindenden User in die ClientHashMap ein
     * → Server kennt Benutzer und kann Anfragen behandeln (über Callback Interface)
     * → Nicht über das Interface verfügbar! (Sicherheitsrichtlinie)
     *
     * @param clientInterface Das sich verbindende ClientInterface
     * @param user            Der sich verbindende User
     * @throws RemoteException RMI based Exception
     */
    public void connectUser(ClientInterface clientInterface, User user) throws RemoteException {
        System.out.println("Server: connection established - User: " + user.getUsername()
                + " | ClientInterface: " + clientInterface.toString());
        clientHashMap.put(user, clientInterface);
    }

    /**
     * Entfernt Client aus ClientHashMap
     *
     * @param user User des "ausgeloggten" User
     * @throws RemoteException RMI based Exception
     */
    @Override
    public void disconnectUser(User user) throws RemoteException {
        System.out.println("Server: disconnection - User: " + user.getUsername());
        clientHashMap.remove(user);
    }

    /**
     * Gets List of Chats for User
     *
     * @param user Logged in User
     * @return List with Chats from User
     * @throws RemoteException RMI based Exception
     */
    @Override
    public List<Chat> getChatList(User user) throws RemoteException, ServerInterfaceException {
        Session session = HibernateSessionHandler.getSession();
        try{
            session.beginTransaction();
            User tmpUser = session.get(User.class, user.getId());
            session.getTransaction().commit();
            session.close();
            return new ArrayList<>(tmpUser.getChats());
        } catch (HibernateException hibernateException){
            hibernateException.printStackTrace();
        }
        throw new ServerInterfaceException("ServerInterfaceException: Could not load Chats!");
    }

    /**
     * Get Messages of Chat for User
     *
     * @param chat affected Chat
     * @return List with Messages of Chat
     * @throws RemoteException RMI based Exception
     */
    @Override
    public List<Message> getChatMessageList(Chat chat) throws RemoteException, ServerInterfaceException {
        Session session = HibernateSessionHandler.getSession();
        try{
            session.beginTransaction();
            Chat tmpChat = session.get(Chat.class, chat.getId());
            session.getTransaction().commit();
            session.close();
            return new ArrayList<>(tmpChat.getMessages());
        } catch (HibernateException hibernateException){
            hibernateException.printStackTrace();
        }
        throw new ServerInterfaceException("ServerInterfaceException: Could not load Messages!");
    }

    /**
     * Adds Message to Chat
     * Creates Message Object and adds Relation to Chat
     *
     * @param payload payload of the Message
     * @param chat    affected Chat Object
     * @param user    affected User Object
     * @throws RemoteException RMI based Exception
     * @throws ServerInterfaceException if unable to Safe Message or Update Users
     */
    @Override
    public void addMessageToChat(String payload, Chat chat, User user) throws RemoteException, ServerInterfaceException {
        Session session = HibernateSessionHandler.getSession();
        try {
            session.beginTransaction();
            Message tmpMessage = new Message(payload);
            tmpMessage.setAuthor(user);
            chat.getMessages().add(tmpMessage);
            user.getMessageList().add(tmpMessage);
            session.persist(tmpMessage);
            session.saveOrUpdate(chat);
            session.getTransaction().commit();

            // TODO not tested yet!
            for (User usr : chat.getUsers()) {
                try{
                    clientHashMap.get(usr).refreshChat();
                } catch (NullPointerException nullPointerException) {
                    //nullPointerException.printStackTrace();
                    System.out.println("User: " + usr.getUsername() + " is not online! Automatic Refresh when back online!");
                }
            }
        } catch (HibernateException hibernateException) {
            hibernateException.printStackTrace();
            session.getTransaction().rollback();
            throw new ServerInterfaceException("ServerInterfaceException: Fehler beim Speichern der Message!");
        } catch (RemoteException remoteException) {
            remoteException.printStackTrace();
            throw new ServerInterfaceException("ServerInterfaceException: Chats wurden nicht korrekt über Update informiert");
        } finally {
            session.close();
        }

    }

    /**
     * removeMessageFromChat
     * Removes Payload from Message
     *
     * @param message   affected Message Object
     * @throws RemoteException          RMI based Exception
     * @throws ServerInterfaceException if unable to Remove Message or Update other Clients
     */
    @Override
    public synchronized void removeMessageFromChat(Message message) throws RemoteException, ServerInterfaceException {
        Session session = HibernateSessionHandler.getSession();
        try {
            session.beginTransaction();
            message.setPayload("Message has been deleted by: " + message.getAuthor().getUsername());
            session.saveOrUpdate(message);
            session.getTransaction().commit();

            List<Chat> chatList = new ArrayList<>(message.getChats());
            Chat chat = chatList.get(0);
            // TODO not tested yet!
            for (User usr : chat.getUsers()) {
                try{
                    clientHashMap.get(usr).refreshChat();
                } catch (NullPointerException nullPointerException) {
                    //nullPointerException.printStackTrace();
                    System.out.println("User: " + usr.getUsername() + " is not online! Automatic Refresh when back online!");
                }
            }
        } catch (HibernateException hibernateException) {
            hibernateException.printStackTrace();
            session.getTransaction().rollback();
            throw new ServerInterfaceException("ServerInterfaceException: Unable to Remove Message from Database");
        } catch (RemoteException remoteException) {
            session.getTransaction().rollback();
            throw new ServerInterfaceException("ServerInterfaceException: Fehler beim Entfernen der Message!");
        } finally {
            session.close();
        }
    }

    /**
     * Adds Chat to Server
     * Created Chat Object with specified Name
     *
     * @param chatName name of new Chat
     * @throws RemoteException          RMI based Exception
     * @throws ServerInterfaceException if not possible to Add Chat
     */
    @Override
    public synchronized Chat addChat(String chatName) throws RemoteException, ServerInterfaceException {
        EntityManager entityManager = HibernateSessionHandler.getEntityManager();
        Chat tmpChat = new Chat(chatName);
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(tmpChat);
            entityManager.getTransaction().commit();
            return tmpChat;
        } catch (PersistenceException persistenceException) {
            persistenceException.printStackTrace();
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.close();
        }
        throw new ServerInterfaceException("ServerInterfaceException: Fehler beim Anlegen eines Chats.");
    }

    /**
     * Adds User to Chat
     *
     * @param user affected User
     * @param chat affected Chat
     * @throws RemoteException          RMI based Exception
     * @throws ServerInterfaceException if unable to add User to Chat
     */
    @Override
    public synchronized void addParticipantToChat(User user, Chat chat) throws RemoteException, ServerInterfaceException {
        Session tmpSession = HibernateSessionHandler.getSession();
        try {
            tmpSession.beginTransaction();
            chat.getUsers().add(user);
            user.getChats().add(chat);
            tmpSession.update(chat);
            tmpSession.getTransaction().commit();
            tmpSession.close();
            // TODO not tested yet!
            for (User usr : chat.getUsers()) {
                try{
                    clientHashMap.get(usr).refreshChat();
                } catch (NullPointerException nullPointerException) {
                    //nullPointerException.printStackTrace();
                    System.out.println("User: " + usr.getUsername() + " is not online! Automatic Refresh when back online!");
                }

            }
        } catch (HibernateException hibernateException) {
            hibernateException.printStackTrace();
            tmpSession.getTransaction().rollback();
            throw new ServerInterfaceException("ServerInterfaceException: Unable to add User (" + user.getUsername() + ") to Chat");
        } finally {
            tmpSession.close();
        }
    }

    /**
     * Removes User from Chat
     *
     * @param user affected User
     * @param chat affected Chat
     * @throws RemoteException          RMI based Exception
     * @throws ServerInterfaceException if unable to remove user from Chat
     */
    @Override
    public void removeParticipantFromChat(User user, Chat chat) throws RemoteException, ServerInterfaceException {
        Session tmpSession = HibernateSessionHandler.getSession();
        try {
            tmpSession.beginTransaction();
            if(chat.getUsers().size() > 2){
                chat.getUsers().remove(user);
                user.getChats().remove(chat);
                tmpSession.update(chat);
            } else {
                tmpSession.remove(chat);
            }
            tmpSession.getTransaction().commit();
            tmpSession.close();
            // TODO not tested yet!
            clientHashMap.get(user).refreshChat();
            for (User usr : chat.getUsers()) {
                try{
                    clientHashMap.get(usr).refreshChat();
                } catch (NullPointerException nullPointerException) {
                    //nullPointerException.printStackTrace();
                    System.out.println("User: " + usr.getUsername() + " is not online! Automatic Refresh when back online!");
                }
            }
        } catch (HibernateException hibernateException) {
            hibernateException.printStackTrace();
            tmpSession.getTransaction().rollback();
            throw new ServerInterfaceException("ServerInterfaceException: Unable to remove User from Chat");
        } finally {
            tmpSession.close();
        }
    }

    /**
     * Gets all registered Users from Server
     *
     * @return List with all Users
     * @throws RemoteException RMI based Exception
     */
    @Override
    public List<User> getAllUsers() throws RemoteException, ServerInterfaceException {
        Session session = HibernateSessionHandler.getSession();
        try {
            session.beginTransaction();
            CriteriaQuery<User> cq = session.getCriteriaBuilder().createQuery(User.class);
            Root<User> userRoot = cq.from(User.class);
            CriteriaQuery<User> all = cq.select(userRoot);
            TypedQuery<User> allQuery = session.createQuery(all);
            List<User> resultList = allQuery.getResultList();
            session.getTransaction().commit();
            return resultList;
        } catch (HibernateException hibernateException) {
            hibernateException.printStackTrace();
            System.out.println(hibernateException.getMessage());
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        throw new ServerInterfaceException("ServerInterfaceException: Couldn't get UserList");
    }
}
