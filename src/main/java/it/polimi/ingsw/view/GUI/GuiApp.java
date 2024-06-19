package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.view.GUI.controllers.WelcomeScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * GUI App main class.
 */
public class GuiApp extends Application {
    private static final String fxmlPath = "/fxml/";
    private static Stage mainStage;
    private static Parent welcomeScreenRoot;
    private static WelcomeScreenController welcomeScreenController;

    /**
     * Starts the GUI.
     *
     * @param stage The main stage of the GUI.
     * @throws Exception If an error occurs during the GUI startup.
     */
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        //Load roots
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath + "WelcomeScreen.fxml"));
        welcomeScreenRoot = loader.load();
        welcomeScreenController = loader.getController();
        //TODO: all other roots
    }

    /**
     * Starts the GUI.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
