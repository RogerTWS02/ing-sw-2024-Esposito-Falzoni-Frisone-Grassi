package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.view.GUI.GuiApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainPlayerViewController implements Initializable {
    public GridPane startingCard;
    public ImageView handCard0;
    public ImageView handCard1;
    public ImageView handCard2;
    public Button placeButton;
    public Button sideButton;
    public Label turnLabel;
    public Button chatButton;
    private int selectedCardIndex = 100;
    private boolean isFlipped;
    private int[] coordinates = new int[2];
    private Button selectedButton;
    private boolean myturn;

    /**
     * Initializes the GridPane with buttons.
     *
     * @param url Ignored.
     * @param resourceBundle Ignored.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Shows an error message.
     *
     * @param error The error message to show.
     */
    public void showError(String error){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error!");
        alert.setContentText(error);
        alert.showAndWait();
    }

    /**
     * Sets the turn label.
     *
     * @param turn The turn to set.
     */
    public void setTurnLabel(String turn, boolean myTurn){
        this.myturn = myTurn;
        Platform.runLater(() -> turnLabel.setText(turn));

        if(myTurn){
            Platform.runLater(() -> {
                placeButton.setDisable(false);
                placeButton.setVisible(true);
                sideButton.setDisable(false);
                sideButton.setVisible(true);
            });
        } else {
            Platform.runLater(() -> {
                placeButton.setDisable(true);
                placeButton.setVisible(false);
                sideButton.setDisable(true);
                sideButton.setVisible(false);
            });
        }
    }

    /**
     * Starts the player board, showing the starting card.
     *
     * @param UUID The UUID of the starting card.
     */
    public void startPlayerBoard(String UUID){
        Platform.runLater(() -> {
            startingCard.setVisible(true);
            startingCard.setDisable(true);

            //set the right background for the starting card
            String imagePath = "/graphics/startingDeck/" + UUID + ".png";
            String style = String.format("-fx-background-image: url('%s');", imagePath);
            startingCard.setStyle(style);
        });
    }

    /**
     * Selects the first hand card.
     *
     * @param mouseEvent Ignored.
     */
    public void selectHandCard1(MouseEvent mouseEvent) {
        selectedCardIndex = 0;
        showSelection();
    }

    /**
     * Selects the second hand card.
     *
     * @param mouseEvent Ignored.
     */
    public void selectHandCard2(MouseEvent mouseEvent) {
        selectedCardIndex = 1;
        showSelection();
    }

    /**
     * Selects the third hand card.
     *
     * @param mouseEvent Ignored.
     */
    public void selectHandCard3(MouseEvent mouseEvent) {
        selectedCardIndex = 2;
        showSelection();
    }

    /**
     * Displays the selections of the player.
     */
    public void showSelection() {
        Platform.runLater(() -> {
            handCard0.setOpacity(1);
            handCard1.setOpacity(1);
            handCard2.setOpacity(1);
        });

        switch (selectedCardIndex) {
            case 0:
                Platform.runLater(() -> handCard0.setOpacity(0.5));
                break;
            case 1:
                Platform.runLater(() -> handCard1.setOpacity(0.5));
                break;
            case 2:
                Platform.runLater(() -> handCard2.setOpacity(0.5));
                break;
        }
    }

    /**
     * Handles the "Place" button click event.
     *
     * @param actionEvent Ignored.
     */
    public void placeButtonPressed(ActionEvent actionEvent) {
        //update coordinates
        getNewCoords();

        if(coordinates[0] == -1) {
            showError("You must select a cell to place a card!");
            return;
        } else if(selectedCardIndex == 100) {
            showError("You must select a card to place!");
            return;
        }

        GuiApp.getGui().placeCard(selectedCardIndex, coordinates[0], coordinates[1], isFlipped);
        isFlipped = false;
        Platform.runLater(() -> sideButton.setOpacity(1));
        selectedCardIndex = 100;
    }

    public void getNewCoords(){
        coordinates = GuiApp.getPlayerBoardController().getCoords();
    }

    /**
     * Handles the "Side" button click event.
     *
     * @param actionEvent Ignored.
     */
    public void sideButtonPressed(ActionEvent actionEvent) {
        isFlipped = !isFlipped;
        if(isFlipped)
            Platform.runLater(() -> sideButton.setOpacity(0.5));
    }

    /**
     * Handles the "Chat" button click event.
     *
     * @param actionEvent Ignored.
     */
    public void chatButtonPressed(ActionEvent actionEvent) {
        GuiApp.changeScene(GuiApp.getChatViewRoot());
    }
}
