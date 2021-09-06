package de.htwsaar.vs.rmiMessengerClient.model.textmessage;

import de.htwsaar.vs.rmiMessengerClient.controller.ChatSceneController;
import de.htwsaar.vs.rmiMessengerClient.controller.LoginSceneController;
import de.htwsaar.vs.rmiMessengerClient.utils.Utils;
import de.htwsaar.vs.rmiMessengerShared.Message;
import de.htwsaar.vs.rmiMessengerShared.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * @author Julian Klotz
 * @author Tim Schneider
 * @version 1
 * @since 1
 */
public class    MessageListViewCellTextmessage extends ListCell<Message> {

    @FXML
    private BorderPane BorderPane_OwnUser;

    @FXML
    private BorderPane BorderPane_OtherUsers;

    @FXML
    private AnchorPane anchorPane_OwnUser;

    @FXML
    private AnchorPane anchorPane_OtherUsers;
    
    @FXML
    private javafx.scene.layout.VBox VBox;

    @FXML
    private Label UsernameTimeLabel_OwnUser;
    
    @FXML
    private Label UsernameTimeLabel_OtherUsers;
    
    @FXML
    private Text text_OwnUser;

    @FXML
    private Text text_OtherUsers;

    private FXMLLoader mLLoader;

    /**
     * Überschreiben der updateItem Funktion einer ListCell
     */
    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty || message == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/view/ListCellTextmessage.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            ChatSceneController csc = LoginSceneController.getChatSceneController();
            User user = csc.getLoggedInUser();

            if (message.getAuthor().getId() == user.getId()) {
                BorderPane_OtherUsers.setVisible(false);
                BorderPane_OwnUser.setVisible(true);
                UsernameTimeLabel_OwnUser.setText(
                        message.getAuthor().getUsername()
                                + " ( "
                                + Utils.getDate(message)
                                + " ) ");
                text_OwnUser.setWrappingWidth(440);
                text_OwnUser.setText(message.getPayload());
                setText(null);
                setGraphic(anchorPane_OwnUser);
            } else if (message.getAuthor().getId() != user.getId()) {
                BorderPane_OwnUser.setVisible(false);
                BorderPane_OtherUsers.setVisible(true);
                UsernameTimeLabel_OtherUsers.setText(
                        message.getAuthor().getUsername()
                                + " ( "
                                + Utils.getDate(message)
                                + " ) ");
                text_OtherUsers.setWrappingWidth(440);
                text_OtherUsers.setText(message.getPayload());
                setText(null);
                setGraphic(anchorPane_OtherUsers);
            }
        }
    }
}

