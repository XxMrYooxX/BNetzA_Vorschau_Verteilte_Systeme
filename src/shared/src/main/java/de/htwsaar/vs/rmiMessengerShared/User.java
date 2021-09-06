package de.htwsaar.vs.rmiMessengerShared;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 *
 * @author Amir Savari (kib.amir.savari)
 * @version 0.1
 * @since 0.1
 */
@Entity(name = "user")
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true)
    private int id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "registrationDate", nullable = false)
    private long registrationDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "author")
    private Set<Message> messageList = new HashSet<>();


    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Chat> chats = new HashSet<>();


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.registrationDate = (System.currentTimeMillis() / 1000);
    }

    public User() {
        this.registrationDate = (System.currentTimeMillis() / 1000);
    }

    @Override
    public String toString() {
        return ("Username: " + username + "\tregistrationDate: " + registrationDate);
    }

    /**
     * Getter des Attributs ID
     *
     * @return ID des Users
     */
    public int getId() {
        return this.id;
    }

    /**
     * Setter des Attribut ID
     *
     * @param id ID des Users
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter des Attributs username
     *
     * @return username des Users
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter des Attributs username
     *
     * @param username username des Nutzers
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter des Attributs password
     *
     * @return password des Users
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Setter des Attributs password
     *
     * @param password password des Users
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter des Attributs registrationDate
     *
     * @return registrationDate des Users
     */
    public long getRegistrationDate() {
        return this.registrationDate;
    }

    /**
     * Setter des Attribut registrationDate
     *
     * @param registrationDate registration date des Users
     */
    public void setRegistrationDate(long registrationDate) {
        this.registrationDate = registrationDate;
    }

    /**
     * Getter des Attributs chats
     *
     * @return chats des jeweiligen Users
     */
    public Set<Chat> getChats() {
        return chats;
    }

    /**
     * Setter des Attributs chats
     *
     * @param chats chats Set des jeweiligen Users
     */
    public void setChats(Set<Chat> chats) {
        this.chats = chats;
    }

    /**
     * Getter des Attributs messageList
     *
     * @return Liste mit allen Messages eines Users
     */
    public Set<Message> getMessageList() {
        return messageList;
    }

    /**
     * Setter des Attributs messageList
     *
     * @param messageList Liste mit Messages eines Users
     */
    public void setMessageList(Set<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, registrationDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return id != 0 && id == other.getId();
    }
}

