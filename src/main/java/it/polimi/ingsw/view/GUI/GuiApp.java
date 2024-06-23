package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.view.GUI.controllers.WelcomeScreenController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * GUI App main class.
 */
public class GuiApp extends Application {
    private static Gui gui;
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

        Scene scene = new Scene(welcomeScreenRoot);
        mainStage.setTitle("Codex Naturalis");
        mainStage.setScene(scene);
        mainStage.setMinWidth(1600);
        mainStage.setMinHeight(900);
        mainStage.setResizable(true);
        mainStage.setMaximized(false);
        mainStage.setFullScreenExitHint("");
        mainStage.setFullScreen(true);
        mainStage.show();

        //Close the application when the main stage is closed
        mainStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Starts the GUI.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Returns the welcome screen controller.
     */
    public static WelcomeScreenController getWelcomeScreenController() {
        return welcomeScreenController;
    }

    /**
     * Sets the GUI.
     *
     * @param gui The GUI.
     */
    public static void setGui(Gui gui) {
        GuiApp.gui = gui;
    }

    /**
     * Returns the GUI.
     *
     * @return The GUI.
     */
    public static Gui getGui() {
        return gui;
    }
}
