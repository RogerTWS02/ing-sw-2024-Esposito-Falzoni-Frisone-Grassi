package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.view.GUI.GuiApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
    public GridPane playerBoard;
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
    private int selectedCardIndex = 100;
    private boolean isFlipped;
    private int[] coordinates = new int[2];
    private Button selectedButton;
    private volatile boolean myturn;
    private Image[] handCardsImg = new Image[3], goalCardsImg = new Image[2], deckCards = new Image[2], commonCards = new Image[4];
    //GC: 1 and 2 are common, 3 is secret; Deck: 1 re, 2 go; Common: 1 and 2 re, 3 and 4 go
    private boolean firstTurn = true;

    /**
     * Initializes the GridPane with buttons.
     *
     * @param url Ignored.
     * @param resourceBundle Ignored.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (int i = 0; i < 81; i++) {
            ColumnConstraints column = new ColumnConstraints(80); // Set column width
            RowConstraints row = new RowConstraints(60); // Set row height
            playerBoard.getColumnConstraints().add(column);
            playerBoard.getRowConstraints().add(row);
        }

        // Loop to create and add buttons to the GridPane
        for (int rowIndex = 0; rowIndex < 81; rowIndex++) {
            for (int colIndex = 0; colIndex < 81; colIndex++) {
                if((rowIndex+colIndex) % 2 != 0) continue;
                Button button = new Button();
                button.setVisible(false);
                GridPane.setHgrow(button, Priority.ALWAYS);
                GridPane.setVgrow(button, Priority.ALWAYS);
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Make button fill cell

                // Set button action
                int finalRowIndex = rowIndex;
                int finalColIndex = colIndex;
                button.setOnAction(event -> onButtonClicked(finalRowIndex, finalColIndex));

                // Add button to GridPane
                playerBoard.add(button, colIndex, rowIndex);
            }
        }
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

        //Hand cards
        for(int i = 0; i < 3; i++)
            handCardsImg[i] = new Image(getClass().getResourceAsStream(pathBuilder(GuiApp.getGui().getCurrentHandUUID().get(i))));

        Platform.runLater(() -> {
            handCard0.setImage(handCardsImg[0]);
            handCard1.setImage(handCardsImg[1]);
            handCard2.setImage(handCardsImg[2]);
        });

        //Common cards
        for(int i = 0; i < 2; i++)
            commonCards[i] = new Image(getClass().getResourceAsStream(pathBuilder(GuiApp.getGui().getResourceViewableCards()[i])));
        for(int i = 0; i < 2; i++)
            commonCards[i + 2] = new Image(getClass().getResourceAsStream(pathBuilder(GuiApp.getGui().getGoldenViewableCards()[i])));

        Platform.runLater(() -> {
            commonResource1.setImage(commonCards[0]);
            commonResource2.setImage(commonCards[1]);
            commonGolden1.setImage(commonCards[2]);
            commonGolden2.setImage(commonCards[3]);
        });

        //Deck cards
        deckCards[0] = new Image(getClass().getResourceAsStream(pathBuilder(GuiApp.getGui().getResourceViewableCards()[2])));
        deckCards[1] = new Image(getClass().getResourceAsStream(pathBuilder(GuiApp.getGui().getGoldenViewableCards()[2])));

        Platform.runLater(() -> {
            resourceDeck.setImage(deckCards[0]);
            goldenDeck.setImage(deckCards[1]);
        });
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
        for(int i = 0; i < 2; i++)
            commonCards[i] = new Image(getClass().getResourceAsStream(pathBuilder(GuiApp.getGui().getResourceViewableCards()[i])));
        for(int i = 0; i < 2; i++)
            commonCards[i + 2] = new Image(getClass().getResourceAsStream(pathBuilder(GuiApp.getGui().getGoldenViewableCards()[i])));

        Platform.runLater(() -> {
            commonResource1.setImage(commonCards[0]);
            commonResource2.setImage(commonCards[1]);
            commonGolden1.setImage(commonCards[2]);
            commonGolden2.setImage(commonCards[3]);
        });

        //Deck cards
        deckCards[0] = new Image(getClass().getResourceAsStream(pathBuilder(GuiApp.getGui().getResourceViewableCards()[2])));
        deckCards[1] = new Image(getClass().getResourceAsStream(pathBuilder(GuiApp.getGui().getGoldenViewableCards()[2])));

        Platform.runLater(() -> {
            resourceDeck.setImage(deckCards[0]);
            goldenDeck.setImage(deckCards[1]);
        });
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

        GuiApp.getGui().requestViewableCards();

        if(!firstTurn)
            update_view();
        else
            firstTurn = false;
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
     * Handles the button click event.
     *
     * @param rowIndex The row index of the clicked button.
     * @param colIndex The column index of the clicked button.
     */
    public void onButtonClicked(Integer rowIndex, Integer colIndex){
        coordinates[1] = rowIndex;
        coordinates[0] = colIndex;

        Button button = null;
        for (Node node : playerBoard.getChildren()) {
            if (Objects.equals(GridPane.getRowIndex(node), rowIndex) && Objects.equals(GridPane.getColumnIndex(node), colIndex)) {
                button = (Button) node;
                break;
            }
        }

        //Set new button pressed
        button.setText("");
        button.setVisible(true);

        //Reset the old button pressed
        if(selectedButton != null)
            selectedButton.setVisible(false);
        selectedButton = button;
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
        if(selectedButton == null) {
            showError("You must select a cell to place a card!");
            return;
        } else if(selectedCardIndex == 100) {
            showError("You must select a card to place!");
            return;
        }

        GuiApp.getGui().placeCard(selectedCardIndex, coordinates[0], coordinates[1], isFlipped);
        isFlipped = false;
        Platform.runLater(() -> sideButton.setOpacity(1));
        selectedButton = null;
        selectedCardIndex = 100;
        handCardsImg[selectedCardIndex] = null;
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

    /**
     * Handles the resource cards deck click event.
     *
     * @param mouseEvent Ignored.
     */
    public void resourceDeckPressed(MouseEvent mouseEvent) {
        //TODO
    }

    /**
     * Handles the golden cards deck click event.
     *
     * @param mouseEvent Ignored.
     */
    public void goldenDeckPressed(MouseEvent mouseEvent) {
        //TODO
    }

    /**
     * Handles the common resource card 1 deck click event.
     *
     * @param mouseEvent Ignored.
     */
    public void commonResourcePressed1(MouseEvent mouseEvent) {
        //TODO
    }

    /**
     * Handles the common resource card 2 deck click event.
     *
     * @param mouseEvent Ignored.
     */
    public void commonResourcePressed2(MouseEvent mouseEvent) {
        //TODO
    }

    /**
     * Handles the common golden card 2 deck click event.
     *
     * @param mouseEvent Ignored.
     */
    public void commonGoldenPressed2(MouseEvent mouseEvent) {
        //TODO
    }

    /**
     * Handles the common golden card 1 deck click event.
     *
     * @param mouseEvent Ignored.
     */
    public void commonGoldenPressed1(MouseEvent mouseEvent) {
        //TODO
    }
}
