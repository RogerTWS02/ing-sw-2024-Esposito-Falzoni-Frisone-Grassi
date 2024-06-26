package it.polimi.ingsw.view.GUI.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class EndGameScreenController implements Initializable {
    public Label winnersLabel;
    public Button closeButton;
    public GridPane gridPane;

    /**
     * Initializes the end game screen.
     *
     * @param url Ignored.
     * @param resourceBundle Ignored.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gridPane.setStyle("-fx-background-image: url('/gui_graphics/EndScreenBackground.png'); -fx-background-size: stretch;");
    }

    /**
     * Handles the close button pressed event.
     *
     * @param actionEvent Ignored.
     */
    public void closeButtonPressed(ActionEvent actionEvent) {
        //TODO
    }
}
