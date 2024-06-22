package it.polimi.ingsw.view.GUI.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the welcome screen.
 */
public class WelcomeScreenController implements Initializable {
    public TextField textField;
    public Button doneButton;
    public GridPane gridPane;
    public Slider playerSlider;
    public Label textLabel;
    public Label textField_ConnectedTo;
    private String playerNickname = "";
    private int lobbySize = 0;
    private boolean insertedNickname = false;

    /**
     * Initializes the background image and manages the viewable entities of the welcome screen once it's loaded.
     *
     * @param url Ignored.
     * @param resourceBundle Ignored.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gridPane.setStyle("-fx-background-image: url('/gui_graphics/WelcomeScreenBackground.png'); -fx-background-size: stretch;");
        playerSlider.setVisible(false);
        playerSlider.setDisable(true);
        textLabel.setVisible(false);
        textLabel.setDisable(true);
        //TODO: new label for the connection status
    }

    /**
     * Handles the event of the user pressing the "Done" button, setting the player's nickname and the lobby size.
     *
     * @param actionEvent Ignored.
     */
    public void ButtonPressed(ActionEvent actionEvent) {
        if(!insertedNickname) {
            if(textField.getText().length() > 16 || textField.getText().isEmpty()) {
                textLabel.setText("Nick length: 1-16!");
                textLabel.setVisible(true);
                return;
            }
            playerNickname = textField.getText();
            insertedNickname = true;
            textField.clear();
            textLabel.setText("Set the lobby size (2-4):");
            textLabel.setVisible(true);
            playerSlider.setVisible(true);
            playerSlider.setDisable(false);
            textField.setVisible(false);
            textField.setDisable(true);
        } else {
            lobbySize = (int) playerSlider.getValue();
            waitForOtherPlayers();
        }
    }

    /**
     * Displays the waiting message.
     */
    public void waitForOtherPlayers() {
        Platform.runLater(() -> {
            doneButton.setDisable(true);
            doneButton.setVisible(false);
            playerSlider.setDisable(true);
            playerSlider.setVisible(false);
            textLabel.setText("Waiting for other players...");
        });
    }
}
