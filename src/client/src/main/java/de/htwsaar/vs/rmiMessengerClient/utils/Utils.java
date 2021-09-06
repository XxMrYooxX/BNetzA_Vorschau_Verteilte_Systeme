package de.htwsaar.vs.rmiMessengerClient.utils;

import com.github.plushaze.traynotification.notification.Notification;
import com.github.plushaze.traynotification.notification.TrayNotification;
import de.htwsaar.vs.rmiMessengerShared.Chat;
import de.htwsaar.vs.rmiMessengerShared.Message;
import de.htwsaar.vs.rmiMessengerShared.User;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import com.github.plushaze.traynotification.animations.Animations;


import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utils
 *
 * @author Julian Klotz
 * @author Tim Schneider
 * @version 1
 * @since 1
 */
public class Utils {

    /**
     * Converts the creation time in seconds into a date (string)
     * @param message Corresponds to the message that was created at this creation time.
     * @return Delivers the date as a string
     */
    public static String getDate(Message message) {
        Date date = new Date(message.getCreationTime() * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("d.M.yyyy H:mm", Locale.GERMANY);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+02:00"));
        return sdf.format(date);
    }

    /**
     * Gets the image of the chat
     * TODO: Muss gefixt werden!
     * @param chat Corresponds to the chat that receives the corresponding image
     */
    public static String getImage(Chat chat) {
        // Path to images (user-to-user or user-to-group)
        String path_to_user_image = "/assets/customer_52px.png";
        String path_to_group_image = "/assets/people_52px.png";
        String setImage;

        if(chat.getUsers().size() >= 3) {
            setImage = path_to_group_image;
        } else {
            setImage = path_to_user_image;
        }

        return setImage;
    }

    /**
     * Method sortMessagesFromCurrentChat()
     * Sorts the messages from the current chat in ascending order
     * @param currentMessagesFromCurrentChat currentMessagesFromCurrentChat
     * @return sorted list of messages from the current chat
     */
    public static List<Message> sortMessagesFromCurrentChat(List<Message> currentMessagesFromCurrentChat) {
        // ascending sort
        currentMessagesFromCurrentChat.sort(Comparator.comparingLong(Message::getCreationTime));
        return currentMessagesFromCurrentChat;

    }

    /**
     * Method sortServerUsers()
     * Sorts the users from the server in ascending order
     * @param existing_Users_on_Server existing_Users_on_Server
     * @return sorted list of users from the server
     */
    public static List<User> sortServerUsers(List<User> existing_Users_on_Server) {
        // ascending sort
        existing_Users_on_Server.sort((u1, u2) -> String.valueOf(u1.getUsername())
                .compareTo(u2.getUsername()));
        return existing_Users_on_Server;
    }
    /**
     * Method sortChatsFromCurrentUser()
     * Sorts the messages from the current chat in ascending order
     * @param loggedInUser_Chats loggedInUser_Chats
     * @return sorted list of chats of the current user
     */
    public static List<Chat> sortChatsFromCurrentUser(List<Chat> loggedInUser_Chats) {
        // ascending sort
        loggedInUser_Chats.sort(Comparator.comparingLong(Chat::getCreationDate));
        return loggedInUser_Chats;
    }

    /**
     * Method notifyUser()
     * Displays some notifications.
     * @param title title
     * @param notifyMessage notifyMessage
     * @param notification notification
     */
    public static  void notifyUser(String title, String notifyMessage, Notification notification ) {
        TrayNotification tray = new TrayNotification(title, notifyMessage, notification);
        tray.setAnimation(Animations.POPUP);
        tray.showAndDismiss(Duration.seconds(2));
    }

    /**
     * Method notifyUserNewMessage()
     * Displays notification when Chatapp is minimized.
     */
    public static  void notifyUserNewMessage() {
        TrayNotification tray = new TrayNotification();
        tray.setTitle("Information");
        tray.setMessage("New Messages!");
        tray.setRectangleFill(Paint.valueOf("#2A9A84"));
        Image image = new Image("/assets/whatsapp_48px.png");
        tray.setImage(image);
        tray.showAndDismiss(Duration.seconds(2));
    }

    /**
     * Method alertUser
     * Displays notification for Login Functions
     * @param alertMessage alertMessage
     * @param alertType alertType
     */
    public static void alertUser(String alertMessage, Alert.AlertType alertType){
        Alert alert = new Alert(alertType, alertMessage);
        alert.showAndWait();
    }

}
