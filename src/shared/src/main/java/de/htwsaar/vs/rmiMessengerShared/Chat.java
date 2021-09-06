package de.htwsaar.vs.rmiMessengerShared;


import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Chat Object
 *
 * @author Marcel Hesselbach
 * @version 1.0
 * @since 1.0
 */
@Entity(name = "chat")
@Table(name = "chats", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class Chat implements Serializable {
    /**
     * Represents Chat Identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true)
    private int id;

    /**
     * Represents Chat Name
     */
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * Represents Chat creationDate as in Millisec since 1970
     */
    @Column(name = "creationDate", nullable = false)
    private long creationDate;

    //TODO: Test
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "chat_message_relation", joinColumns = {
            @JoinColumn(referencedColumnName = "id")
    }, inverseJoinColumns = {
            @JoinColumn(referencedColumnName = "id")
    })
    private final Set<Message> messages = new HashSet<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "chat_user_relation", joinColumns = {
            @JoinColumn(referencedColumnName = "id")
    }, inverseJoinColumns = {
            @JoinColumn(referencedColumnName = "id")
    })
    private final Set<User> users = new HashSet<>();


    /**
     * Erstellt ein Chatobjekt, welches vorher nicht existierte
     *
     * @param name Name des Chats
     */
    public Chat(String name) {
        this.name = name;
        this.creationDate = (System.currentTimeMillis() / 1000);
    }

    public Chat() {
    }

    /**
     * Getter des Attributs ID
     * @return id
     */
    public int getId() { return id; }

    /**
     * Getter des Attributs Name
     * @return name
     */
    public String getName(){ return name; }

    /**
     * Getter des Attribut CreationDate
     * @return creationDate
     */
    public long getCreationDate(){ return creationDate; }

    /**
     * Setter des Attribut ID
     *
     * @param id ID des Chats
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Setter des Attribut name
     *
     * @param name Name des Chats
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter des Attribut creationDate
     *
     * @param creationDate creationDate des Chats
     */
    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Getter des Attribut (Relationstabelle) messages
     *
     * @return List mit Messages eines Chats
     */
    public Set<Message> getMessages() {
        return messages;
    }

    /**
     * Getter des Attribut (Relationstabelle) users
     *
     * @return List mit Users eines Chats
     */
    public Set<User> getUsers() {
        return users;
    }
}
