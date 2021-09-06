package de.htwsaar.vs.rmiMessengerClient.model.chat;

import de.htwsaar.vs.rmiMessengerClient.utils.Utils;
import de.htwsaar.vs.rmiMessengerShared.Chat;
import de.htwsaar.vs.rmiMessengerShared.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.*;

/**
 * @author Julian Klotz
 * @author Tim Schneider
 * @version 1
 * @since 1
 */
public class ChatListViewCell extends ListCell<Chat> {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Label nameUserOrGroupLabel;

    @FXML
    private Label lastMessageLabel;

    @FXML
    private Label lastDateLabel;   
    
    private FXMLLoader mLLoader;
    
    /**
     * Überschreiben der updateItem Funktion einer ListCell
     */
    @Override
    protected void updateItem(Chat chat, boolean empty) {
        super.updateItem(chat, empty);

        if (empty || chat== null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/view/ListCellChat.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }           
           
            nameUserOrGroupLabel.setText(chat.getName());
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Utils.getImage(chat))));
            profileImageView.setImage(image);
            
            Message message = new Message("");
            if(chat.getMessages().size() > 0) {
                //TODO: böse Line! muss gefixt werden :D Set to List Casts sind gefährlich :D
                List<Message> messages = new ArrayList<>(chat.getMessages());
                message = messages.get(chat.getMessages().size() - 1);
            	lastMessageLabel.setText(message.getPayload());
                lastDateLabel.setText(Utils.getDate(message));
            } else {
            	lastMessageLabel.setText(message.getPayload());
                lastDateLabel.setText("");
            }

            setText(null);
            setGraphic(anchorPane);
        }
    }

}
