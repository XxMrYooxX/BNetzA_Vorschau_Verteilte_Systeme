package de.htwsaar.vs.rmiMessengerClient.model.userServer;

import de.htwsaar.vs.rmiMessengerShared.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Julian Klotz
 * @author Tim Schneider
 * @version 1
 * @since 1
 */
public class UserListViewCell extends ListCell<User> {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Label usernameLabel;
	
    private FXMLLoader mLLoader;

    /**
     * Überschreiben der updateItem Funktion einer ListCell
     */
    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if (empty || user == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/view/ListCellUserServer.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            usernameLabel.setText(user.getUsername());
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/customer_52px.png")));
            profileImageView.setImage(image);

            setText(null);
            setGraphic(anchorPane);
        }
    }
}
