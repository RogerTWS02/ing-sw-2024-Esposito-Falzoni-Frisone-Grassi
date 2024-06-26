package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.view.GUI.GuiApp;
import javafx.application.Platform;
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
        StringBuilder winners;
        if(GuiApp.getGui().getWinners().size() == 1) {
            winners = new StringBuilder("Winner: " + GuiApp.getGui().getWinners().get(0));
            winnersLabel.setText(winners.toString());
            return;
        }
        winners = new StringBuilder("Winners:\n\n");
        for(int i = 0; i < GuiApp.getGui().getWinners().size(); i++)
            winners.append((i + 1)).append(": ").append(GuiApp.getGui().getWinners().get(i)).append("\n");
        winnersLabel.setText(winners.toString());
    }

    /**
     * Handles the close button pressed event.
     *
     * @param actionEvent Ignored.
     */
    public void closeButtonPressed(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }
}
