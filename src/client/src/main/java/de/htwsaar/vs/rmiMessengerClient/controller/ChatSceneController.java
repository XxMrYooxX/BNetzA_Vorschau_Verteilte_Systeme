package de.htwsaar.vs.rmiMessengerClient.controller;

import com.github.plushaze.traynotification.notification.Notifications;
import de.htwsaar.vs.rmiMessengerClient.ChatApp;
import de.htwsaar.vs.rmiMessengerClient.model.chat.ChatListViewCell;
import de.htwsaar.vs.rmiMessengerClient.model.textmessage.MessageListViewCellTextmessage;
import de.htwsaar.vs.rmiMessengerClient.model.userServer.UserListViewCell;
import de.htwsaar.vs.rmiMessengerClient.utils.CryptoUtils;
import de.htwsaar.vs.rmiMessengerClient.utils.Utils;
import de.htwsaar.vs.rmiMessengerShared.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;


/**
 * ChatSceneController
 *
 * @author Julian Klotz
 * @author Tim Schneider
 * @version 1.4
 * @since 1
 */
public class ChatSceneController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private HBox AddUserGroupView;
    @FXML
    private HBox MainView;
    @FXML
    private HBox EditProfileView;

    @FXML
    private TextField textfieldSendMessage;
    @FXML
    private TextField txtEditUsername;
    @FXML
    private TextField txtEditPassword;
    @FXML
    private TextField textfieldAddGroupname;    // inital not visable

    @FXML
    private ListView<Chat> chatsListView;
    private ObservableList<Chat> chatsObservableList;
    @FXML
    private ListView<Message> messagesListView;
    private ObservableList<Message> messagesObservableList;
    @FXML
    private ListView<User> userListView;
    private ObservableList<User> usersObservableList;

    @FXML
    private Label labelNameUser;
    @FXML
    private Label labelUserChat;
    @FXML
    private Label labelLastDate;
    @FXML
    private Label labelSelectUsersFromServer;
    @FXML
    private Label Label_ChatMembers;

    @FXML
    private RadioButton rbAddUsers;
    @FXML
    private RadioButton rbAddGroup;
    @FXML
    private ToggleGroup addUsersOrGroup;

    private String txtEditUsername_Save;
    private String txtEditPassword_Save;
    
    private static ServerInterface serverInterface;
    private static List<Chat> loggedInUser_Chats;
    private static List<User> existing_Users_on_Server;
    private static List<Message> currentMessagesFromCurrentChat;
    private static User loggedInUser;
    private static Chat currentChat;
    private static Integer index_currentChat;

    /**
     * Method initialize()
     * Initializes Controller
     *
     * @param arg0 URL
     * @param arg1 ResourceBundle
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        // Get Serverinterface
        serverInterface = ChatApp.getServer_stub();

        // Initialize ObservableLists
        chatsObservableList = FXCollections.observableArrayList();
        messagesObservableList = FXCollections.observableArrayList();
        usersObservableList = FXCollections.observableArrayList();

        chatsListView.setCellFactory(chatListView -> new ChatListViewCell());
        messagesListView.setCellFactory(messagesListView -> new MessageListViewCellTextmessage());
        userListView.setCellFactory(userListView -> new UserListViewCell());
        
        // Save Username & Password
		txtEditUsername_Save = txtEditUsername.getText();
		txtEditPassword_Save = txtEditPassword.getText();

        // Load the chats from the logged-in User to the ListView & update loggedInUser_Chats
        initializeChats();

        // Loads all existing users from the server into the ListView
        initializeServerUsers();
        
        // Logout of a user when the exit button of the GUI was pressed
        ChatApp.getPrimaryStage().setOnCloseRequest(e -> {
    		try {
				serverInterface.disconnectUser(loggedInUser);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		});

        // Listeners der Observable Lists
        chatsObservableList.addListener((ListChangeListener<Chat>) c -> System.out.println("ListView Chats has been changed!"));
        messagesObservableList.addListener((ListChangeListener<Message>) c -> System.out.println("ListView Chat has been changed!"));
        usersObservableList.addListener((ListChangeListener<User>) c -> System.out.println("ListView Users from the server has been changed!"));


        ChatApp.getPrimaryStage().iconifiedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(ChatApp.getPrimaryStage().isIconified()) {
                    Utils.notifyUser("Chat minimiert", "Kommende Nachrichten werden hier angezeigt!", Notifications.INFORMATION );
                }
            }
        });
    }

    /**
     * Method switchToSceneLogin()
     * Logout User from Chat and Returns to Login / Register Page
     *
     * @param event event
     */
	@FXML public void switchToSceneLogin(ActionEvent event) {
		try {
			serverInterface.disconnectUser(loggedInUser);
			root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/LoginScene.fxml")));
			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			stage.setResizable(false);
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }

    /**
     * Method OnMouseClicked_OnRightClickDeleteMessage
     *
     * Asks User for Deletion of its own Message on Right Click
     * @param event MouseEvent
     */
    @FXML
    void OnMouseClicked_OnRightClickDeleteMessage(MouseEvent event) {
        MouseButton button = event.getButton();
        if(button==MouseButton.PRIMARY){
            System.out.println("Linksklick");
            messagesListView.getSelectionModel().clearSelection(messagesListView.getSelectionModel().getSelectedIndex());
        } else if(button==MouseButton.SECONDARY){
            System.out.println("Rechtsklick");

            if(messagesListView.getItems().size() > 0) {
                if(!messagesListView.getSelectionModel().isEmpty()) {
                    //Checking for own Message
                    if(loggedInUser.getId() == messagesListView.getSelectionModel().getSelectedItem().getAuthor().getId()) {
                        Alert alertInformation = new Alert(AlertType.INFORMATION, "Do you want to delete the selected item?", ButtonType.YES, ButtonType.CANCEL);
                        alertInformation.showAndWait();

                        if (alertInformation.getResult() == ButtonType.YES) {
                            try {
                                serverInterface.removeMessageFromChat(messagesListView.getSelectionModel().getSelectedItem());
                                //loadAllExistingChatsFromCurrentUserToListView_Chats(); TODO: Prüfen, ob Client Interface richtig übernimmt
                                Utils.notifyUser("Confirmation", "The message has been successfully deleted!", Notifications.SUCCESS);
                            } catch (RemoteException | ServerInterfaceException e) {
                                e.printStackTrace();
                                Utils.notifyUser("Error", "Error during deletion of Message, Please try again!", Notifications.ERROR);
                            }
                        }
                    } else {
                        Utils.notifyUser("Error", "You only can delete your own Message!", Notifications.ERROR);
                    }
                }
                messagesListView.getSelectionModel().clearSelection(messagesListView.getSelectionModel().getSelectedIndex());
            }
        }
    }
	
    /**
     * Checks which radio button is selected and what should be done if selected
     */
    public void radioButtonChanged() {
        if (addUsersOrGroup.getSelectedToggle().equals(rbAddUsers)) {
            textfieldAddGroupname.setVisible(false);
            textfieldAddGroupname.clear();
            userListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            labelSelectUsersFromServer.setText("Select User from Server :");
        }
        if (addUsersOrGroup.getSelectedToggle().equals(rbAddGroup)) {
            textfieldAddGroupname.setVisible(true);
            userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            labelSelectUsersFromServer.setText("Select Users from Server (Multiselect):");
        }
    }
	
	/**
	 * Method OnMouseClicked_AddUsersOrGroup(MouseEvent event)
	 * Add selected Users to current User Chats OR
     * Add selected Users to a Group and add it to current User Chats
     */
    @FXML
    void OnMouseClicked_AddUsersOrGroup() {
        ArrayList<User> selectedUser = new ArrayList<>();
        Chat referencedChat;
        //Adds loggedInUser to selection for easier AddParticipant Method Call
        selectedUser.add(loggedInUser);
        selectedUser.addAll(userListView.getSelectionModel().getSelectedItems());
        //User to User Chat (loggedInUser + one other)
        if (selectedUser.size() == 2 && rbAddUsers.isSelected()) {
            // Prüfe, ob Chat schon existiert
            for (Chat chat : chatsListView.getItems()) {
                Set<User> chatUsers = chat.getUsers();
                if (chatUsers.containsAll(selectedUser)) {
                    return;
                }
            }
            try {
                referencedChat = serverInterface.addChat("UserToUser: " + selectedUser.get(0).getUsername()
                        + " & " + selectedUser.get(1).getUsername());
            } catch (RemoteException | ServerInterfaceException exception) {
                exception.printStackTrace();
                Utils.notifyUser("Error", "Error during Chat Creation!", Notifications.ERROR);
                return;
            }
        } else if (selectedUser.size() > 2 && rbAddGroup.isSelected()) {
            // Chat existiert noch nicht oder Gruppenchat
            try {
                // Creates new chat for the user
                referencedChat = serverInterface.addChat(textfieldAddGroupname.getText());
                textfieldAddGroupname.clear();
            } catch (RemoteException | ServerInterfaceException exception) {
                exception.printStackTrace();
                Utils.notifyUser("Error", "Error during Chat Creation!", Notifications.ERROR);
                return;
            }
        } else {
            Utils.notifyUser("Warning", "Incorrect selection!", Notifications.WARNING);
            return;
        }
        try {
            // Add Users to Chat
            for (User u : selectedUser) {
                serverInterface.addParticipantToChat(u, referencedChat);
            }
        } catch (RemoteException | ServerInterfaceException exception) {
            exception.printStackTrace();
            Utils.notifyUser("Error", "Error during Chat Creation!", Notifications.ERROR);
            return;
        }
        Utils.notifyUser("Confirmation", "Your changes have been successfully applied!", Notifications.SUCCESS);
        userListView.getSelectionModel().clearSelection();
    }


    /**
     * Method updateUserCredentials()
     * Updates user credentials
     */
    private void updateUserCredentials() {
        try {
            loggedInUser.setUsername(txtEditUsername.getText());
            loggedInUser.setPassword(CryptoUtils.encrypt(txtEditPassword.getText()));
            serverInterface.updateUser(loggedInUser);
            labelNameUser.setText(txtEditUsername.getText());
            Utils.notifyUser("Confirmation", "Your changes have been successfully applied!", Notifications.SUCCESS);
            txtEditUsername_Save = txtEditUsername.getText();
            txtEditPassword_Save = txtEditPassword.getText();
        } catch (RemoteException | ServerInterfaceException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

            loggedInUser.setUsername(txtEditUsername_Save);
            loggedInUser.setPassword(txtEditPassword_Save);
        }
    }


    /**
     * Method OnMouseClicked_EditChanges(MouseEvent event)
     * Save changed data of the user profile in database
     * TODO: braucht dringend optimierung!
     */
    @FXML
    private void OnMouseClicked_EditChanges() throws Exception {
        String encryptedText = CryptoUtils.encrypt(txtEditPassword.getText());
        if(!txtEditUsername_Save.equals(txtEditUsername.getText()) || !txtEditPassword_Save.equals(encryptedText)) {
            if(!txtEditUsername.getText().trim().isEmpty()) {
                boolean user_exist = false;
                loadAllExistingUserOnTheServerToListView_ServerUsers();
                for(User user : userListView.getItems()) {
                    if(txtEditUsername.getText().equals(user.getUsername())) {
                        user_exist = true;
                    }
                }
                if(!txtEditUsername_Save.equals(txtEditUsername.getText())) {
                    if(user_exist) { //If Username Wrong
                        Utils.alertUser("Password or Username wrong!\n Restore Default!", AlertType.ERROR);
                        txtEditUsername.setText(txtEditUsername_Save);
                        txtEditPassword.setText(txtEditPassword_Save);
                    }
                    else {

                        if(!txtEditPassword.getText().trim().isEmpty()) {

                            updateUserCredentials();
                            loadAllExistingUserOnTheServerToListView_ServerUsers();
                        } else {
                            Utils.alertUser("Fill in Username and Password! \n Restored Default!", AlertType.WARNING);

                            txtEditUsername.setText(txtEditUsername_Save);
                            txtEditPassword.setText(txtEditPassword_Save);
                        }
                    }
                } else {
                    if(!txtEditPassword.getText().trim().isEmpty()) {

                        updateUserCredentials();
                        loadAllExistingUserOnTheServerToListView_ServerUsers();
                    } else {
                        Utils.alertUser("Fill in Username and Password! \n Restored Default!", AlertType.WARNING);
                        txtEditUsername.setText(txtEditUsername_Save);
                        txtEditPassword.setText(txtEditPassword_Save);
                    }
                }
            }
            else {
                Utils.alertUser("Fill in Username and Password! \n Restored Default!", AlertType.WARNING);

                txtEditUsername.setText(txtEditUsername_Save);
                txtEditPassword.setText(txtEditPassword_Save);
            }
        }
        else {
            Utils.notifyUser("Information", "Nothing has to be done!", Notifications.INFORMATION);
        }
    }

    /**
	 * Method OnMouseClicked_ListView_Chats(MouseEvent event)
	 * Clear previous Chat and load new selected Chat into ListViewChat
	 * Update info "Last date" message was sent
	 * Update info with whom you are writing (user or group)
     *
	 * @param event MouseEvent
	 */
    @FXML
    void OnMouseClicked_ListView_Chats(MouseEvent event) {
    	MouseButton button = event.getButton();
    	if(button==MouseButton.PRIMARY){
            System.out.println("Linksklick");
            if (!chatsListView.getItems().isEmpty()) {
                currentChat = chatsListView.getSelectionModel().getSelectedItem();
            	updateListView_Chat();
        	 }
        } else if(button==MouseButton.SECONDARY){
        	System.out.println("Rechtsklick");
        	if(chatsListView.getItems().size() > 0) {
        		if(!chatsListView.getSelectionModel().isEmpty() && currentChat != null) {
					Alert alertInformation = new Alert(AlertType.INFORMATION, "Do you want to remove the selected chat / group?", ButtonType.YES, ButtonType.CANCEL);
					alertInformation.showAndWait();
					if (alertInformation.getResult() == ButtonType.YES) {
						try {
							serverInterface.removeParticipantFromChat(loggedInUser, currentChat); //  TODO: removeParticipantFromChat --> soll refreshChat(chat) in ClientInterfaceImpl aufrufen
                            //loadAllExistingChatsFromCurrentUserToListView_Chats();  // TODO: Kann entfernt werden, wenn TODO in Zeile 416 gefixt wurde

                            Utils.notifyUser("Information", "The chat / group have been successfully removed!", Notifications.INFORMATION);

			    			if(chatsObservableList.size() == 0) {
                                messagesObservableList.clear();
                                labelLastDate.setText("Last Message " + "");
                                labelUserChat.setText("User");
                            } else {
                                chatsListView.getSelectionModel().selectFirst();
                                currentChat = chatsListView.getSelectionModel().getSelectedItem();
                                updateListView_Chat();
                            }
						} catch (RemoteException | ServerInterfaceException e) {
							e.printStackTrace();
                            Utils.alertUser("Could not remove Chat " + currentChat.getName(), AlertType.ERROR);
						}
					}
				}        		  
        	}
        }
    }

    /**
     * Method addUserGroup_Button()
     * After clicking on the add User/ UserGroup Button
     * the UserGroup view switch to visible
     *
     */
	@FXML public void addUserGroup_Button(){
		AddUserGroupView.setVisible(true);
		MainView.setVisible(false);
		radioButtonChanged();
		// Updates ListView for existing server user
		loadAllExistingUserOnTheServerToListView_ServerUsers();
	}

    /**
     * Method backFrom_addUserGroup_Button()
     * After clicking on back button scene switch to Chat Scene
     */
	@FXML public void backFrom_addUserGroup_Button(){
		MainView.setVisible(true);
		AddUserGroupView.setVisible(false);
		userListView.getSelectionModel().clearSelection();

        if (chatsObservableList.size() > 0) {
            chatsListView.getSelectionModel().select(0);
            currentChat = chatsListView.getSelectionModel().getSelectedItem();
            updateListView_Chat();
        }
	}

    /**
     * Method editUser_Button()
     * After clicking on user profile button scene switch to edit user scene
     */
	@FXML public void editUser_Button(){
		EditProfileView.setVisible(true);
		MainView.setVisible(false);
		
		// Backup User and Password field
		txtEditUsername_Save = txtEditUsername.getText();
		txtEditPassword_Save = txtEditPassword.getText();
	}

    /**
     * Method backFrom_editUser_Button()
     * After clicking on back button scene switch to chatscene
     */
	@FXML public void backFrom_editUser_Button(){
		MainView.setVisible(true);
		EditProfileView.setVisible(false);		
	}

    /**
     * Method is called by login scene.
     * Used to transfer login information to the chat scene
     * @param username Username
     * @param password  Password
     */
	public void showInfosToChatSceneController(String username, String password) {
		txtEditPassword.setText(password);
		txtEditUsername.setText(username);
		labelNameUser.setText(username);
	}

    /**
     * Method updateListView_Chat()
     * Updated: Loads the messages from the current chat into the ListView, which displays the chat messages
     */
    public void updateListView_Chat() {

        if (currentChat != null) {
            try {
                currentMessagesFromCurrentChat = serverInterface.getChatMessageList(currentChat);
                Utils.sortMessagesFromCurrentChat(currentMessagesFromCurrentChat);
                messagesObservableList.clear();
                messagesObservableList.addAll(
                        currentMessagesFromCurrentChat
                );
                messagesListView.setItems(messagesObservableList);
                messagesListView.setCellFactory(messagesListView -> new MessageListViewCellTextmessage());
                int N = messagesListView.getItems().size();
                if(N > 0) {
                    messagesListView.scrollTo(N-1);
                    String lastDate = Utils.getDate(messagesListView.getItems().get(N-1));
                    labelLastDate.setText("Last Message " + lastDate);
                } else {
                    String lastDate = "";
                    labelLastDate.setText("Last Message " + lastDate);
                }
                labelUserChat.setText(currentChat.getName());

            } catch (RemoteException | ServerInterfaceException e) {
                e.printStackTrace();
            }
        }
        if (chatsListView.getSelectionModel().isEmpty()){
            labelUserChat.setText("User");
            String lastDate = "";
            labelLastDate.setText("Last Message " + lastDate);
            messagesObservableList.clear();
        }
    }

    /**
     * Method loadAllExistingUserOnTheServerToListView_ServerUsers()
     * Updates the ListView for all ServerUsers
     */
    private void loadAllExistingUserOnTheServerToListView_ServerUsers() {
        try {
            existing_Users_on_Server =  serverInterface.getAllUsers();
            existing_Users_on_Server.remove(loggedInUser);
            Utils.sortServerUsers(existing_Users_on_Server);
            usersObservableList.clear();
        	usersObservableList.addAll(
                    existing_Users_on_Server
			);
		} catch (RemoteException | ServerInterfaceException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }      

        userListView.setItems(usersObservableList);
        //userListView.refresh();
        userListView.setCellFactory(usersListView -> new UserListViewCell());
    }

    /**
     * Method initializeServerUsers()
     * Initialize the ListView for all ServerUsers
     */
    private void initializeServerUsers() {
        try {
            existing_Users_on_Server =  serverInterface.getAllUsers();
            existing_Users_on_Server.remove(loggedInUser);
            Utils.sortServerUsers(existing_Users_on_Server);
            usersObservableList.clear();
            usersObservableList.addAll(
                    existing_Users_on_Server
            );
            userListView.setItems(usersObservableList);
            userListView.setCellFactory(listViewUserGroupList -> new UserListViewCell());
        } catch (RemoteException | ServerInterfaceException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
    /**
     * Method initializeChats()
     * Initialize the Chats from the current User
     */
    public void initializeChats() {
        try {
            loggedInUser_Chats = serverInterface.getChatList(loggedInUser);
            Utils.sortChatsFromCurrentUser(loggedInUser_Chats);
            chatsObservableList.clear();
            chatsObservableList.addAll(
                    loggedInUser_Chats
            );
            chatsListView.setItems(chatsObservableList);
            chatsListView.setCellFactory(listViewUserGroupList -> new ChatListViewCell());

            if (loggedInUser_Chats.size() > 0) {
                chatsListView.getSelectionModel().selectFirst();
                currentChat = chatsListView.getSelectionModel().getSelectedItem();
            }
            //------------------------------

            messagesObservableList.clear();
            if(currentChat != null){
                currentMessagesFromCurrentChat = serverInterface.getChatMessageList(currentChat);
                Utils.sortMessagesFromCurrentChat(currentMessagesFromCurrentChat);
                messagesObservableList.addAll(
                        currentMessagesFromCurrentChat
                );
                messagesListView.setItems(messagesObservableList);
                messagesListView.setCellFactory(ListViewChat -> new MessageListViewCellTextmessage());

                int N = messagesListView.getItems().size();
                if(N > 0) {
                    messagesListView.scrollTo(N-1);
                    String lastDate = Utils.getDate(messagesListView.getItems().get(N-1));
                    labelLastDate.setText("Last Message " + lastDate);
                } else {
                    String lastDate = "";
                    labelLastDate.setText("Last Message " + lastDate);
                }
                labelUserChat.setText(currentChat.getName());
            }


        } catch (RemoteException | ServerInterfaceException e) {
            e.printStackTrace();
        }
    }



    /**
     * Method loadAllExistingChatsFromCurrentUserToListView_Chats()
     * Updates the ListView for all ServerUsers
     */
    public void loadAllExistingChatsFromCurrentUserToListView_Chats() {
        try {
            loggedInUser_Chats = serverInterface.getChatList(loggedInUser);
            Utils.sortChatsFromCurrentUser(loggedInUser_Chats);
            if (currentChat != null) {
                index_currentChat = chatsListView.getSelectionModel().getSelectedIndex();
            }
            chatsObservableList.clear();
            chatsObservableList.addAll(
                    loggedInUser_Chats
            );
            if (currentChat != null) {
                chatsListView.getSelectionModel().select(index_currentChat);
                currentChat = chatsListView.getSelectionModel().getSelectedItem();
            }
            updateListView_Chat();
        } catch (RemoteException | ServerInterfaceException e) {
            e.printStackTrace();
        }

        chatsListView.setItems(chatsObservableList);
        chatsListView.refresh();
        chatsListView.setCellFactory(chatsListView -> new ChatListViewCell());
    }
	
	
    /**
     * Method to send the text in the current chat
     */
	@FXML public void sendMessage () {
    	// Check if text field is not empty
    	if(!textfieldSendMessage.getText().trim().isEmpty() && !chatsListView.getItems().isEmpty() && !chatsListView.getSelectionModel().isEmpty() && currentChat != null) {
    		try {
                //System.out.println("Message: " + textfieldSendMessage.getText() + "\t Chat: " + currentChat.getName() + "\t User: " + loggedInUser.getUsername());
				serverInterface.addMessageToChat(textfieldSendMessage.getText(), currentChat, loggedInUser);
				//updateListView_Chat();
                loadAllExistingChatsFromCurrentUserToListView_Chats();
			} catch (RemoteException | ServerInterfaceException e) {
				e.printStackTrace();
			}

            textfieldSendMessage.clear();
        }
    	else {
            Utils.notifyUser("Warning", "Textfield is empty or chat not selected!",Notifications.WARNING);
    	}
    }

    /**
     *  Method OnMouseEntered_ShowMembers()
     *  Shows the label to show members of a chat
     */
    @FXML void OnMouseEntered_ShowMembers() {
        if (currentChat != null && !currentChat.getName().contains("UserToUser")) {
            Label_ChatMembers.setMinHeight(30);
            String members = "Members: ";
            for (User  user : currentChat.getUsers()) {
                members += user.getUsername() + ", ";
            }

            Label_ChatMembers.setText(members.substring(0, members.length()-2));
        }
    }

    /**
     *  Method OnMouseExited_HideMembers()
     *  Hides the label to show members of a chat
     */
    @FXML void OnMouseExited_HideMembers() {
        Label_ChatMembers.setMinHeight(0);

        Label_ChatMembers.setText("");
    }

    /**
     * Setter for Attribute loggedInUser
     * Sets Attribute for logged In User
     * @param loggedInUser Logged In User
     */
    public static void setLoggedInUser(User loggedInUser) {
        ChatSceneController.loggedInUser = loggedInUser;
    }

    /**
     * Method Get the user who is currently logged in
     * @return Currently logged in User
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }
}
