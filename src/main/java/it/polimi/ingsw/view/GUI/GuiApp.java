package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.view.GUI.controllers.*;
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
    private static Parent mainPlayerViewRoot;
    private static MainPlayerViewController mainPlayerViewController;
    private static Parent endGameScreenRoot;
    private static EndGameScreenController endGameScreenController;
    private static Parent preliminaryChoicesViewRoot;
    private static PreliminaryChoicesViewController preliminaryChoicesViewController;
    private static Parent chatViewRoot;
    private static ChatController chatController;
    public static volatile boolean guiStarted = false;

    /**
     * Starts the GUI.
     *
     * @param stage The main stage of the GUI.
     * @throws Exception If an error occurs during the GUI startup.
     */
    public void start(Stage stage) throws Exception {
        mainStage = stage;

        //Load roots and controllers
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath + "WelcomeScreen.fxml"));
        welcomeScreenRoot = loader.load();
        welcomeScreenController = loader.getController();

        loader = new FXMLLoader(getClass().getResource(fxmlPath + "PreliminaryChoicesView.fxml"));
        preliminaryChoicesViewRoot = loader.load();
        preliminaryChoicesViewController = loader.getController();

        loader = new FXMLLoader(getClass().getResource(fxmlPath + "MainPlayerView.fxml"));
        mainPlayerViewRoot = loader.load();
        mainPlayerViewController = loader.getController();

        loader = new FXMLLoader(getClass().getResource(fxmlPath + "EndGameScreen.fxml"));
        endGameScreenRoot = loader.load();
        endGameScreenController = loader.getController();

        loader = new FXMLLoader(getClass().getResource(fxmlPath + "ChatView.fxml"));
        chatViewRoot = loader.load();
        chatController = loader.getController();

        Scene scene = new Scene(welcomeScreenRoot);
        mainStage.setTitle("Codex Naturalis");
        mainStage.setScene(scene);
        mainStage.setMinWidth(1600);
        mainStage.setMinHeight(900);
        mainStage.setResizable(true);
        mainStage.setMaximized(false);
        mainStage.setFullScreenExitHint("");
        mainStage.show();

        //Synchronize the GUI with the GUI thread
        guiStarted = true;

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


    public static ChatController getChatController(){
        return chatController;
    }

    /**
     * Changes the scene of the main stage.
     *
     * @param root The new root.
     */
    public static void changeScene(Parent root) {
        Platform.runLater(() -> mainStage.getScene().setRoot(root));
    }

    /**
     * Returns the welcome screen controller.
     */
    public static WelcomeScreenController getWelcomeScreenController() {
        return welcomeScreenController;
    }

    /**
     * Returns the root of the player's game view.
     *
     * @return The root of the player's game view.
     */
    public static Parent getMainPlayerViewRoot() {
        return mainPlayerViewRoot;
    }

    /**
     * Returns the main player view controller.
     *
     * @return The main player view controller.
     */
    public static MainPlayerViewController getMainPlayerViewController() {
        return mainPlayerViewController;
    }

    /**
     * Returns the root of the preliminary choices view.
     *
     * @return The root of the preliminary choices view.
     */
    public static Parent getPreliminaryChoicesViewRoot() {
        return preliminaryChoicesViewRoot;
    }

    /**
     * Returns the preliminary choices view controller.
     *
     * @return The preliminary choices view controller.
     */
    public static PreliminaryChoicesViewController getPreliminaryChoicesViewController() {
        return preliminaryChoicesViewController;
    }

    /**
     * Returns the root of the end game screen.
     *
     * @return The root of the end game screen.
     */
    public static Parent getEndGameScreenRoot() {
        return endGameScreenRoot;
    }

    /**
     * Returns the end game screen controller.
     *
     * @return The end game screen controller.
     */
    public static EndGameScreenController getEndGameScreenController() {
        return endGameScreenController;
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
