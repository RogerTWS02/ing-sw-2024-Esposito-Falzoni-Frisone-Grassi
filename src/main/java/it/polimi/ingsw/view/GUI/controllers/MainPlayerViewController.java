package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.view.GUI.GuiApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainPlayerViewController implements Initializable {
    public ImageView handCard0;
    public ImageView handCard1;
    public ImageView handCard2;
    public Button placeButton;
    public Button sideButton;
    public Label turnLabel;
    public Button chatButton;
    public ImageView resourceDeck;
    public ImageView goldenDeck;
    public ImageView commonResource1;
    public ImageView commonGolden1;
    public ImageView commonResource2;
    public ImageView commonGolden2;
    public ImageView commonGoal1;
    public ImageView commonGoal2;
    public ImageView secretGoal;
    public Label topRowLabel;
    public ScrollPane scrollPane;
    public GridPane gridPane;
    private int selectedCardIndex = 100;
    private boolean isFlipped;
    private int[] coordinates = new int[2];
    private volatile boolean myturn;
    private Image[] handCardsImg = new Image[]{null, null, null}, goalCardsImg = new Image[3], commonCards = new Image[6];
    //GC: 1 and 2 are common, 3 is secret; Common: 1 and 2 re, 3 and 4 go
    private boolean firstTurn = true;
    private boolean drawPhase = true;
    private Button prevSelect;
    private int prevX = 40, prevY = 40;
    private int zLevel = 100;

    /**
     * Initializes the GridPane with buttons.
     *
     * @param url Ignored.
     * @param resourceBundle Ignored.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gridPane = new GridPane();
        gridPane.setHgap(-30);
        gridPane.setVgap(-40);
        scrollPane.setContent(gridPane);

        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setHvalue(0.5);
        scrollPane.setVvalue(0.5);

        for(int i = 0; i < 81; i++) {
            for(int j = 0; j < 81; j++) {
                Button button = new Button();
                button.setPrefSize(130, 100);
                int finalJ = j;
                int finalI = i;
                button.setOnMouseClicked(event -> onButtonClicked(finalI, finalJ, button));
                button.setVisible(false);
                button.setDisable(true);
                gridPane.add(button, i, j);

                if(i == 40 && j == 40)
                    prevSelect = button;
            }
        }

        resourceDeck.setDisable(true);
        goldenDeck.setDisable(true);
        commonResource1.setDisable(true);
        commonResource2.setDisable(true);
        commonGolden1.setDisable(true);
        commonGolden2.setDisable(true);

        disableChoosingCards();
    }

    /**
     * Handles the button click event.
     *
     * @param rowIndex The row index of the clicked button.
     * @param colIndex The column index of the clicked button.
     */
    public void onButtonClicked(Integer rowIndex, Integer colIndex, Button button){
        if(prevSelect == null)
            prevSelect = button;
        else {
            if(!prevSelect.isDisabled())
                prevSelect.setStyle("-fx-border-color: initial;" +
                    "-fx-border-width: initial;" + "-fx-opacity: 0.3");
        }

        //change the stile to the selected node
        button.setStyle("-fx-border-color: red;" + "-fx-border-width: 3px;" + "-fx-opacity: 0.3");
        prevSelect = button;
        prevX = rowIndex;
        prevY = colIndex;
    }

    /**
     * Update the player board, showing the starting card.
     *
     * @param prevUUID The UUID of the starting card.
     */
    public void updatePlayerBoard(String prevUUID, boolean side, Resource res, List<int[]> available) {
        String UUID;
        if (res == null) {
            //Insert the starting card
            if (!side)
                UUID = prevUUID + "_B";
            else
                UUID = prevUUID;

            String style = "url('/graphics/startingDeck/" + UUID + ".png');";
            prevSelect.setStyle("-fx-background-image: " + style + "-fx-background-position: center center; " + "-fx-background-size: 100% 100%;" + "-fx-opacity: 1");
            prevSelect.setVisible(true);

            prevSelect.setViewOrder(zLevel);
            zLevel++;

            //update the board with the new available cards
            for (Node node : gridPane.getChildren()) {
                if(node.isVisible() && !node.isDisabled()) {
                    node.setVisible(false);
                    node.setDisable(true);
                }
                for (int[] pos : available) {
                    if (Objects.equals(GridPane.getRowIndex(node), pos[0]) && Objects.equals(GridPane.getColumnIndex(node), pos[1])) {
                        //update the new available cells
                        node.setVisible(true);
                        node.setDisable(false);
                        node.setViewOrder(zLevel);
                        node.setStyle("-fx-opacity: 0.3");
                        break;
                    }
                }
            }
            zLevel -= 2;
            return;
        }

        //update the cell with the new card on the board
        if (side) {
            UUID = "RC_" + res + "_B";
        } else {
            UUID = prevUUID;
        }

        String style;
        //set the right background for the starting card
        if(UUID.contains("RC"))
            style = "url('/graphics/resourceDeck/" + UUID + ".png');";
        else
            style = "url('/graphics/goldenDeck/" + UUID + ".png');";

        prevSelect.setStyle("-fx-background-image: " + style + "-fx-background-position: center center; " + "-fx-background-size: 100% 100%;" + "-fx-opacity: 1");
        prevSelect.setVisible(true);
        prevSelect.setDisable(true);
        prevSelect.setViewOrder(zLevel);
        zLevel++;
        //update the board with the new available cards
        for (Node node : gridPane.getChildren()) {
            if(node.isVisible() && !node.isDisabled()) {
                node.setVisible(false);
                node.setDisable(true);
            }
            for (int[] pos : available) {
                if (Objects.equals(GridPane.getRowIndex(node), pos[0]) && Objects.equals(GridPane.getColumnIndex(node), pos[1])) {
                    //update the new available cells
                    node.setVisible(true);
                    node.setDisable(false);
                    node.setViewOrder(zLevel);
                    node.setStyle("-fx-opacity: 0.3");
                    break;
                }
            }
        }
        /*zLevel++;
        prevSelect.setStyle("-fx-background-image: " + style + "-fx-background-position: center center; " + "-fx-background-size: 100% 100%;" + "-fx-opacity: 1");
        prevSelect.setVisible(true);
        prevSelect.setViewOrder(zLevel);*/
        zLevel -= 2;
    }

    /**
     * Initializes the view.
     */
    public void initialize_2() {
        //Goal cards
        for(int i = 0; i < 3; i++)
            goalCardsImg[i] = new Image(getClass().getResourceAsStream(pathBuilder(GuiApp.getGui().getAllGoalsUUID().get(i))));

        Platform.runLater(() -> {
            commonGoal1.setImage(goalCardsImg[0]);
            commonGoal2.setImage(goalCardsImg[1]);
            secretGoal.setImage(goalCardsImg[2]);
        });

        //Remaining cards
        update_view();
    }

    /**
     * Updates the view.
     */
    public void update_view() {
        //Hand cards
        for(int i = 0; i < 3; i++)
            if(handCardsImg[i] == null)
                handCardsImg[i] = new Image(getClass().getResourceAsStream(pathBuilder(GuiApp.getGui().getCurrentHandUUID().get(i))));

        Platform.runLater(() -> {
            handCard0.setImage(handCardsImg[0]);
            handCard1.setImage(handCardsImg[1]);
            handCard2.setImage(handCardsImg[2]);
        });

        //Common cards
        for(int i = 0; i < 3; i++)
            commonCards[i] = new Image(getClass().getResourceAsStream(pathBuilder(GuiApp.getGui().getResourceViewableCards()[i])));
        for(int i = 0; i < 3; i++)
            commonCards[i + 3] = new Image(getClass().getResourceAsStream(pathBuilder(GuiApp.getGui().getGoldenViewableCards()[i])));

        Platform.runLater(() -> {
            commonResource1.setImage(commonCards[0]);
            commonResource2.setImage(commonCards[1]);
            commonGolden1.setImage(commonCards[3]);
            commonGolden2.setImage(commonCards[4]);
        });

        GuiApp.getGui().updateScores();
    }

    /**
     * Returns the card's view path.
     *
     * @return The card's view path.
     */
    public String pathBuilder(String uuid) {
        if(uuid.contains("RGC"))
            return "/graphics/resourcesGoalDeck/" + uuid + ".png";
        if(uuid.contains("PGC"))
            return "/graphics/patternGoalDeck/" + uuid + ".png";
        if(uuid.contains("RC"))
            return "/graphics/resourceDeck/" + uuid + ".png";
        if(uuid.contains("GC"))
            return "/graphics/goldenDeck/" + uuid + ".png";
        if(uuid.contains("SC"))
            return "/graphics/startingDeck/" + uuid + ".png";
        return null;
    }

    /**
     * Shows an error message.
     *
     * @param error The error message to show.
     */
    public void showError(String error){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(error);
            alert.showAndWait();

            sideButton.setVisible(true);
            sideButton.setDisable(false);
            placeButton.setVisible(true);
            placeButton.setDisable(false);
            handCard0.setOpacity(1);
            handCard1.setOpacity(1);
            handCard2.setOpacity(1);
        });
    }

    /**
     * Shows the number of turns left.
     *
     * @param turns The number of turns left.
     */
    public void showTurnsLeft(int turns){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("End game phase");
            alert.setContentText("You have " + turns + " turns left!");
            alert.showAndWait();

            sideButton.setVisible(true);
            sideButton.setDisable(false);
            placeButton.setVisible(true);
            placeButton.setDisable(false);
        });
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

        GuiApp.getGui().requestViewableCards();

        if(!firstTurn)
            update_view();
        else
            firstTurn = false;
    }

    /**
     * Selects the first hand card.
     *
     * @param mouseEvent Ignored.
     */
    public void selectHandCard0(MouseEvent mouseEvent) {
        selectedCardIndex = 0;
        showSelection();
    }

    /**
     * Selects the second hand card.
     *
     * @param mouseEvent Ignored.
     */
    public void selectHandCard1(MouseEvent mouseEvent) {
        selectedCardIndex = 1;
        showSelection();
    }

    /**
     * Selects the third hand card.
     *
     * @param mouseEvent Ignored.
     */
    public void selectHandCard2(MouseEvent mouseEvent) {
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

        GuiApp.getGui().setPositions(coordinates[0], coordinates[1]);
        GuiApp.getGui().placeCard(selectedCardIndex, coordinates[0], coordinates[1], isFlipped);
        isFlipped = false;
        Platform.runLater(() -> {
            sideButton.setOpacity(1);
            sideButton.setVisible(false);
            sideButton.setDisable(true);
            placeButton.setDisable(true);
            placeButton.setVisible(false);

            resourceDeck.setDisable(false);
            goldenDeck.setDisable(false);
            commonResource1.setDisable(false);
            commonResource2.setDisable(false);
            commonGolden1.setDisable(false);
            commonGolden2.setDisable(false);
        });
        handCardsImg[selectedCardIndex] = null;
        selectedCardIndex = 100;
        drawPhase = true;
    }

    /**
     * Disables the "draw" buttons.
     */
    public void lastTurn() {
        Platform.runLater(() -> {
            resourceDeck.setDisable(true);
            goldenDeck.setDisable(true);
            commonResource1.setDisable(true);
            commonResource2.setDisable(true);
            commonGolden1.setDisable(true);
            commonGolden2.setDisable(true);
        });
    }

    /**
     * Returns the new coordinates.
     *
     * @return The new coordinates.
     */
    public int[] getNewCoords(){
        coordinates[0] = prevX;
        coordinates[1] = prevY;
        return coordinates;
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
        else
            Platform.runLater(() -> sideButton.setOpacity(1));
    }

    /**
     * Updates the top row label.
     *
     * @param text The text to set.
     */
    public void updateTopRowLabel(String text) {
        Platform.runLater(() -> topRowLabel.setText(text));
    }

    /**
     * Handles the "Chat" button click event.
     *
     * @param actionEvent Ignored.
     */
    public void chatButtonPressed(ActionEvent actionEvent) {
        GuiApp.changeScene(GuiApp.getChatViewRoot());
    }

    /**
     * Disables the common card choosing buttons.
     */
    public void disableChoosingCards() {
        Platform.runLater(() -> {
            resourceDeck.setDisable(true);
            goldenDeck.setDisable(true);
            commonResource1.setDisable(true);
            commonResource2.setDisable(true);
            commonGolden1.setDisable(true);
            commonGolden2.setDisable(true);
        });
    }

    /**
     * Handles the resource cards deck click event.
     *
     * @param mouseEvent Ignored.
     */
    public void resourceDeckPressed(MouseEvent mouseEvent) {
        GuiApp.getGui().drawCard(false, 2);
        handCard0.setOpacity(1);
        handCard1.setOpacity(1);
        handCard2.setOpacity(1);
        disableChoosingCards();
    }

    /**
     * Handles the golden cards deck click event.
     *
     * @param mouseEvent Ignored.
     */
    public void goldenDeckPressed(MouseEvent mouseEvent) {
        GuiApp.getGui().drawCard(true, 2);
        handCard0.setOpacity(1);
        handCard1.setOpacity(1);
        handCard2.setOpacity(1);
        disableChoosingCards();
    }

    /**
     * Handles the common resource card 1 deck click event.
     *
     * @param mouseEvent Ignored.
     */
    public void commonResourcePressed1(MouseEvent mouseEvent) {
        GuiApp.getGui().drawCard(false, 0);
        handCard0.setOpacity(1);
        handCard1.setOpacity(1);
        handCard2.setOpacity(1);
        disableChoosingCards();
    }

    /**
     * Handles the common resource card 2 deck click event.
     *
     * @param mouseEvent Ignored.
     */
    public void commonResourcePressed2(MouseEvent mouseEvent) {
        GuiApp.getGui().drawCard(false, 1);
        handCard0.setOpacity(1);
        handCard1.setOpacity(1);
        handCard2.setOpacity(1);
        disableChoosingCards();
    }

    /**
     * Handles the common golden card 1 deck click event.
     *
     * @param mouseEvent Ignored.
     */
    public void commonGoldenPressed1(MouseEvent mouseEvent) {
        GuiApp.getGui().drawCard(true, 0);
        handCard0.setOpacity(1);
        handCard1.setOpacity(1);
        handCard2.setOpacity(1);
        disableChoosingCards();
    }

    /**
     * Handles the common golden card 2 deck click event.
     *
     * @param mouseEvent Ignored.
     */
    public void commonGoldenPressed2(MouseEvent mouseEvent) {
        GuiApp.getGui().drawCard(true, 1);
        handCard0.setOpacity(1);
        handCard1.setOpacity(1);
        handCard2.setOpacity(1);
        disableChoosingCards();
    }
}
