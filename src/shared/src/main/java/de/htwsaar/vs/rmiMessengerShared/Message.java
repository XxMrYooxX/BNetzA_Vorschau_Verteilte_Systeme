package de.htwsaar.vs.rmiMessengerShared;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @author Amir Savari (kib.amir.savari)
 * @version 0.1
 * @since 0.1
 */
@Entity(name = "message")
@Table(name = "messages", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class Message implements Serializable {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "payload", nullable = false)
    private String payload;
    @Column(name = "creationDate", nullable = false)
    private long creationTime;

    @ManyToOne
    private User author;

    @ManyToMany(mappedBy = "messages", fetch = FetchType.EAGER)
    private final Set<Chat> chats = new HashSet<>();

    public Message(String payload) {
        this.payload = payload;
        this.creationTime = System.currentTimeMillis() / 1000;
    }

    public Message() {
        this.creationTime = System.currentTimeMillis() / 1000;
    }


    /**
     * Gets id.
     *
     * @return the id
     */
    public int getID() {
        return this.id;
    }

    /**
     * Gets payload.
     *
     * @return the payload
     */
    public String getPayload() {
        return this.payload;
    }

    /**
     * Gets creation time.
     *
     * @return the creation time
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * Sets ID of Message
     *
     * @param id ID of Message
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets Payload of Message
     *
     * @param payload Payload of Message
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }

    /**
     * Sets Creation Time of Message
     *
     * @param creationTime CreationTime of Message in MillSecs
     */
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * Gets Author User Object
     *
     * @return User Object Author
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Sets Author User Object
     *
     * @param author Author User Object
     */
    public void setAuthor(User author) {
        this.author = author;
    }

    /**
     * Gets Chat of Message
     *
     * @return Set with Chat and Message Relation
     */
    public Set<Chat> getChats() {
        return chats;
    }
}