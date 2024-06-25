package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.view.GUI.GuiApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller for the welcome screen.
 */
public class WelcomeScreenController implements Initializable {
    private WelComeScreenStateEnum screenState;
    public Button createButton;
    public TextField textField;
    public Button doneButton;
    public GridPane gridPane;
    public Slider playerSlider;
    public Label textLabel;
    public Label textField_ConnectedTo;
    public Button refreshButton;

    /**
     * Initializes the background image and manages the viewable entities of the welcome screen once it's loaded.
     *
     * @param url Ignored.
     * @param resourceBundle Ignored.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        screenState = WelComeScreenStateEnum.PRELIMINARY_WAITING;
        gridPane.setStyle("-fx-background-image: url('/gui_graphics/WelcomeScreenBackground.png'); -fx-background-size: stretch;");
        playerSlider.setVisible(false);
        playerSlider.setDisable(true);
        textField.setVisible(false);
        textField.setDisable(true);
        doneButton.setVisible(false);
        doneButton.setDisable(true);
        textLabel.setText("Please, wait...");
        textLabel.setVisible(true);
        textLabel.setDisable(false);
        refreshButton.setVisible(false);
        refreshButton.setDisable(true);
        createButton.setVisible(false);
        createButton.setDisable(true);
        try {
            if(GuiApp.getGui().cli.getIpServ().equals(InetAddress.getLocalHost().getHostAddress()))
                textField_ConnectedTo.setText("Connected to: localhost");
            else
                textField_ConnectedTo.setText("Connected to: " + GuiApp.getGui().cli.getIpServ());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        textField_ConnectedTo.setVisible(true);
        textField_ConnectedTo.setDisable(false);
    }

    /**
     * Shows the available lobbies.
     */
    public void showAvailableLobbies(ArrayList<String> availableLobbies) {
        screenState = WelComeScreenStateEnum.CHOOSING_LOBBY;
        StringBuilder toShow = new StringBuilder("Available lobbies: ");
        for(String lobby : availableLobbies)
            toShow.append(lobby).append(" ");
        String finalToShow = toShow.toString();
        Platform.runLater(() -> {
            textLabel.setText(finalToShow);
            textLabel.setVisible(true);
            textLabel.setDisable(false);
            refreshButton.setVisible(true);
            refreshButton.setDisable(false);
            doneButton.setText("Join");
            doneButton.setVisible(true);
            doneButton.setDisable(false);
            textField.setVisible(true);
            textField.setDisable(false);
            textField.setPromptText("Insert lobby");
            createButton.setVisible(true);
            createButton.setDisable(false);
        });
    }

    /**
     * Sets up for nickname and lobby size insertion.
     */
    public void setUpNicknameInsertion(WelComeScreenStateEnum state) {
        if(screenState == WelComeScreenStateEnum.INSERTING_NICKNAME || screenState == WelComeScreenStateEnum.INSERTING_JUST_NICKNAME) {
            Platform.runLater(() -> {
                textLabel.setText("Nickname must be 1 word, 1-16!");
                textLabel.setVisible(true);
                textLabel.setDisable(false);
            });
            return;
        }
        if(state != null)
            screenState = state;

        Platform.runLater(() -> {
            textField.setPromptText("Insert nickname");
            textField.setVisible(true);
            textField.setDisable(false);
            doneButton.setText("Done");
            doneButton.setVisible(true);
            doneButton.setDisable(false);
            textLabel.setText("Insert your nickname:");
            textLabel.setVisible(true);
            textLabel.setDisable(false);
        });
    }

    /**
     * Sets up for the lobby size selection.
     */
    public void setUpLobbySizeSelection() {
        screenState = WelComeScreenStateEnum.INSERTING_LOBBY_SIZE;
        Platform.runLater(() -> {
            textField.setVisible(false);
            textField.setDisable(true);
            textLabel.setText("Select the lobby size:");
            textLabel.setVisible(true);
            textLabel.setDisable(false);
            playerSlider.setVisible(true);
            playerSlider.setDisable(false);
            doneButton.setVisible(true);
            doneButton.setDisable(false);
        });
    }

    /**
     * Handles the event of the user pressing the "Done" button.
     *
     * @param actionEvent Ignored.
     */
    public void doneButtonPressed(ActionEvent actionEvent) {
        if(screenState == WelComeScreenStateEnum.INSERTING_NICKNAME)
            GuiApp.getGui().setNickname(textField.getText());
        else if(screenState == WelComeScreenStateEnum.INSERTING_LOBBY_SIZE)
            GuiApp.getGui().setLobbySize((int) playerSlider.getValue());
        else if(screenState == WelComeScreenStateEnum.CHOOSING_LOBBY)
            GuiApp.getGui().handleLobbyChoice(textField.getText());
        else if(screenState == WelComeScreenStateEnum.INSERTING_JUST_NICKNAME)
            GuiApp.getGui().setNickname(textField.getText());
    }

    /**
     * Shows the error message for full lobby.
     */
    public void showFullLobbyError() {
        Platform.runLater(() -> {
            doneButton.setVisible(false);
            doneButton.setDisable(true);
            textField.setVisible(false);
            textField.setDisable(true);
            createButton.setVisible(false);
            createButton.setDisable(true);
            refreshButton.setVisible(false);
            refreshButton.setDisable(true);
            textLabel.setText("The chosen lobby is full!");
            textLabel.setVisible(true);
            textLabel.setDisable(false);
        });
    }

    /**
     * Shows the error message for invalid lobby name.
     */
    public void showInvalidLobbyNameError() {
        Platform.runLater(() -> {
            doneButton.setVisible(false);
            doneButton.setDisable(true);
            textField.setVisible(false);
            textField.setDisable(true);
            createButton.setVisible(false);
            createButton.setDisable(true);
            refreshButton.setVisible(false);
            refreshButton.setDisable(true);
            textLabel.setText("No lobby with that name!");
            textLabel.setVisible(true);
            textLabel.setDisable(false);
        });
    }

    /**
     * Displays the waiting message.
     */
    public void waitForOtherPlayers() {
        screenState = WelComeScreenStateEnum.WAITING_FOR_OTHER_PLAYERS;
        Platform.runLater(() -> {
            doneButton.setDisable(true);
            doneButton.setVisible(false);
            playerSlider.setDisable(true);
            playerSlider.setVisible(false);
            refreshButton.setVisible(false);
            refreshButton.setDisable(true);
            textLabel.setVisible(true);
            textLabel.setDisable(false);
            textLabel.setText("Waiting for other players...");
        });
    }

    /**
     * Handles the event of the user pressing the "Refresh" button.
     *
     * @param actionEvent Ignored.
     */
    public void refreshButtonPressed(ActionEvent actionEvent) {
        GuiApp.getGui().refreshAvailableLobbies();
    }

    /**
     * Handles the event of the user pressing the "Join" button.
     *
     * @param actionEvent Ignored.
     */
    public void createButtonPressed(ActionEvent actionEvent) {
        createButton.setVisible(false);
        createButton.setDisable(true);
        refreshButton.setVisible(false);
        refreshButton.setDisable(true);
        GuiApp.getGui().createNewLobbyOnRequest();
    }

    /**
     * Returns the state of the welcome screen.
     *
     * @return The state of the welcome screen.
     */
    public WelComeScreenStateEnum getScreenState() {
        return screenState;
    }

    /**
     * Enumerates the possible states of the welcome screen.
     */
    public enum WelComeScreenStateEnum {
        PRELIMINARY_WAITING,
        INSERTING_NICKNAME,
        INSERTING_JUST_NICKNAME,
        INSERTING_LOBBY_SIZE,
        WAITING_FOR_OTHER_PLAYERS,
        CHOOSING_LOBBY
    }
}
