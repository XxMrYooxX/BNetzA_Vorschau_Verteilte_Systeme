package de.htwsaar.vs.rmiMessengerClient.controller;

import com.github.plushaze.traynotification.notification.Notifications;
import de.htwsaar.vs.rmiMessengerClient.ChatApp;
import de.htwsaar.vs.rmiMessengerClient.utils.CryptoUtils;
import de.htwsaar.vs.rmiMessengerClient.utils.Utils;
import de.htwsaar.vs.rmiMessengerShared.ClientInterface;
import de.htwsaar.vs.rmiMessengerShared.ServerInterface;
import de.htwsaar.vs.rmiMessengerShared.ServerInterfaceException;
import de.htwsaar.vs.rmiMessengerShared.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Julian Klotz
 * @author Tim Schneider
 * @version 1.1
 * @since 1
 */
public class LoginSceneController implements Initializable {

    @FXML
    public Label label_client;
    @FXML
    public Pane loginAlert;
    @FXML
    public Label label_loginAlert;
    @FXML
    public Button button_login;
    @FXML
    public Button button_register;
    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;

    @FXML
    private TextField textField_username_login;

    @FXML
    private PasswordField textField_password_login;

	private ServerInterface serverInterface;
    private ClientInterface clientInterface;

    private static final String pText = "EschcryptionSalt";



    private static ChatSceneController chatSceneController;

    /**
     * Method initialize()
     * Initializes Controller Attributes on load
     *
     * @param arg0 URL
     * @param arg1 ResourceBundle 
     */
    public void initialize(URL arg0, ResourceBundle arg1) {
        this.serverInterface = ChatApp.getServer_stub();
        this.clientInterface = ChatApp.getClient_stub();
    }

    /**
     * Method switchToSceneClient_ButtonRegister()
     * Switch to chat scene
     * @param event event
     */
	@FXML
    public void switchToSceneClient_ButtonLogin(ActionEvent event) {
		// Check that the text field for the username and password is not empty
		if(!textField_username_login.getText().isEmpty() && !textField_password_login.getText().isEmpty()) {
			try {
			    //Login User
                String encryptedText = CryptoUtils.encrypt(textField_password_login.getText());
                System.out.println("Login: " + encryptedText);
                User userWhoIsLoggingIn = serverInterface.loginUser(textField_username_login.getText(), encryptedText, clientInterface);
                //Sets logged-in User as User for ChatSceneController
                ChatSceneController.setLoggedInUser(userWhoIsLoggingIn);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ClientScene.fxml"));
                // Get Controller of ChatSceneController
                root = loader.load();
                chatSceneController = loader.getController();

                // Pass other Information to ChatSceneController
                chatSceneController.showInfosToChatSceneController(
                        textField_username_login.getText(),
                        encryptedText
                );

				stage = (Stage)((Node)event.getSource()).getScene().getWindow();
				stage.setResizable(false);
				scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
                Utils.notifyUser("Login", "Login was succesfull!", Notifications.SUCCESS);
			} catch (ServerInterfaceException | IOException e) {
                e.printStackTrace();
                Utils.alertUser("Invalid username or password!", AlertType.ERROR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Utils.alertUser("Fill in username and password!",AlertType.WARNING);
		}
	}
	
	 /**
     * Method switchToSceneClient_ButtonRegister()
     * Switch to chat scene
      */
	@FXML
    public void switchToSceneClient_ButtonRegister() {
		// Check that the text field for the username and password is not empty
		if(!textField_username_login.getText().isEmpty() && !textField_password_login.getText().isEmpty()) {
			try {
                String encryptedText = CryptoUtils.encrypt(textField_password_login.getText());
                System.out.println("Register: " + encryptedText);
                serverInterface.registerUser(textField_username_login.getText(), encryptedText, clientInterface);
                Utils.notifyUser("Confirmation", "User was registered successfully!", Notifications.SUCCESS);
			} catch (ServerInterfaceException | IOException e) {
                e.printStackTrace();
                Utils.alertUser("User already exists or unable to create user!",AlertType.WARNING);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Utils.alertUser("Fill in username and password!",AlertType.WARNING);
		}
	}

    /**
     * Change focus by pressing TAB KeyCode
     * @param event event
     */
    @FXML
    void OnKeyPressed_SwitchFocus(KeyEvent event) {
        if(event.getCode() == KeyCode.TAB) {
            if (textField_username_login.isFocused()) {
                textField_password_login.focusedProperty();
            } else if (textField_password_login.isFocused()) {
                button_register.focusedProperty();
            } else if (button_register.isFocused()) {
                button_login.focusedProperty();
            } else if (button_login.isFocused()) {
                textField_username_login.focusedProperty();
            }
        }
    }

    /**
     * Getter getChatSceneController
     * Gets ChatSceneController from LoginScene
     * @return ChatSceneController
     */
    public static ChatSceneController getChatSceneController() {
        return chatSceneController;
    }

    public static String getpText() {return pText;}
}
