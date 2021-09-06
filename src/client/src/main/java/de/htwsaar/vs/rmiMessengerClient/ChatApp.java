package de.htwsaar.vs.rmiMessengerClient;

import de.htwsaar.vs.rmiMessengerClient.model.ClientInterfaceImpl;
import de.htwsaar.vs.rmiMessengerShared.ClientInterface;
import de.htwsaar.vs.rmiMessengerShared.ServerInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;

/**
 * @author Julian Klotz
 * @author Tim Schneider
 * @version 1.1
 * @since 1
 */
public class ChatApp extends Application {

	private static ServerInterface server_stub;
	private static ClientInterface client_stub;
	private static Stage primaryStage;

    /**
     * Method start()
     * Starting Instant Messenger GUI
     * @param primaryStage Enter GUI
     */
    @Override
	public void start(Stage primaryStage) {
		try {
            connectToServer();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginScene.fxml"));
            System.out.println(loader.getResources());
			Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/view/application.css")).toExternalForm());
            primaryStage.getIcons().add(new Image("/assets/whatsapp_48px.png"));
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);

            Platform.setImplicitExit(false);
            Platform.runLater(primaryStage::show);
            setPrimaryStage(primaryStage);
        } catch(Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Method main()
     * @param args launch
     */
	public static void main(String[] args) {
        try{
            ClientInterfaceImpl client = new ClientInterfaceImpl();
            client_stub = (ClientInterface) UnicastRemoteObject.exportObject(client, 0);
        } catch (RemoteException remoteException) {
            remoteException.printStackTrace();
        }
        launch(args);
    }

    /**
     * connectToServer
     * connects to Server Registry
     */
    public static void connectToServer(){
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 42424);
            server_stub = (ServerInterface) registry.lookup("ServerInterface");            // Get Controller of LoginSceneController
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Server unavailable! Try again?", ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                connectToServer();
            } else if (alert.getResult() == ButtonType.CANCEL) {
                Platform.exit();
                System.exit(0);
            }
        }
    }

    /**
     * Method getServer_stub
     * Getter for Attribute server_stub
     * @return ServerInterface
     */
    public static ServerInterface getServer_stub() {
        return server_stub;
    }

    /**
     * Method getClient_stub
     * Getter for Attribute client_stub
     * @return ClientInterface
     */
    public static ClientInterface getClient_stub() {
        return client_stub;
    }

    /**
     * Method getPrimaryStage
     * Getter for Attribute primaryStage
     * @return Stage
     */
	public static Stage getPrimaryStage() {
		return primaryStage;
	}

    /**
     * Method setPrimaryStage
     * Setter for Attribute primaryStage
     * @param primaryStage Stage
     */
	public void setPrimaryStage(Stage primaryStage) {
		ChatApp.primaryStage = primaryStage;
	}
    
    
}
