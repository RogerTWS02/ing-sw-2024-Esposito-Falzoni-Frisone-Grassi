package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.view.GUI.GuiApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class PreliminaryChoicesViewController implements Initializable {
    public ImageView commonGoal_1;
    public ImageView commonGoal_2;
    public ImageView secretGoal_1;
    public ImageView secretGoal_2;
    public ImageView startingFront;
    public Label choicesMadeLabel;
    public ImageView startingBack;
    public Button confirmButton;
    public Button secretGoal1Button;
    public Button secretGoal2Button;
    public Button startingFrontButton;
    public Button startingBackButton;
    Image[] commonGoalCards, secretGoalCards, startingCard;
    private Boolean choicesMade[] = new Boolean[]{null, null}; //1 for secret goal, 2 for starting card; true/false: 1 or 2, front or back

    /**
     * Initializes the preliminary choices view.
     *
     * @param url Ignored.
     * @param resourceBundle Ignored.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Retrieve common goal cards
        for(int i = 0; i < 2; i++) {
            if(GuiApp.getGui().getAllGoalsUUID().get(i).contains("RGC"))
                commonGoalCards[i] = new Image(getClass().getResourceAsStream("/graphics/resourcesGoalDeck/" + GuiApp.getGui().getAllGoalsUUID().get(i) + ".png"));
            else
                commonGoalCards[i] = new Image(getClass().getResourceAsStream("/graphics/patternGoalDeck/" + GuiApp.getGui().getAllGoalsUUID().get(i) + ".png"));
        }

        //Retrieve secret goal cards
        for(int i = 1; i < 3; i++) {
            if(GuiApp.getGui().getCardToChooseUUID().get(i).contains("RGC"))
                secretGoalCards[i - 1] = new Image(getClass().getResourceAsStream("/graphics/resourcesGoalDeck/" + GuiApp.getGui().getCardToChooseUUID().get(i) + ".png"));
            else
                secretGoalCards[i - 1] = new Image(getClass().getResourceAsStream("/graphics/patternGoalDeck/" + GuiApp.getGui().getCardToChooseUUID().get(i) + ".png"));
        }

        //Retrieve starting card
        startingCard[0] = new Image(getClass().getResourceAsStream("/graphics/startingDeck/" + GuiApp.getGui().getCardToChooseUUID().get(0) + ".png"));
        startingCard[1] = new Image(getClass().getResourceAsStream("/graphics/startingDeck/" + GuiApp.getGui().getCardToChooseUUID().get(0) + "_B.png"));

        //Set images
        commonGoal_1.setImage(commonGoalCards[0]);
        commonGoal_2.setImage(commonGoalCards[1]);
        secretGoal_1.setImage(secretGoalCards[0]);
        secretGoal_2.setImage(secretGoalCards[1]);
        startingFront.setImage(startingCard[0]);
        startingBack.setImage(startingCard[1]);
    }

    /**
     * Handles the secret goal 1 button press.
     *
     * @param actionEvent Ignored.
     */
    public void secretGoal1ButtonPressed(ActionEvent actionEvent) {
        choicesMade[0] = true;
        updateChoicesMadeLabel();
    }

    /**
     * Handles the secret goal 2 button press.
     *
     * @param actionEvent Ignored.
     */
    public void secretGoal2ButtonPressed(ActionEvent actionEvent) {
        choicesMade[0] = false;
        updateChoicesMadeLabel();
    }

    /**
     * Handles the starting front button press.
     *
     * @param actionEvent Ignored.
     */
    public void startingFrontButtonPressed(ActionEvent actionEvent) {
        choicesMade[1] = true;
        updateChoicesMadeLabel();
    }

    /**
     * Handles the starting back button press.
     *
     * @param actionEvent Ignored.
     */
    public void startingBackButtonPressed(ActionEvent actionEvent) {
        choicesMade[1] = false;
        updateChoicesMadeLabel();
    }

    /**
     * Handles the confirm button press.
     *
     * @param actionEvent Ignored.
     */
    public void confirmButtonPressed(ActionEvent actionEvent) {
        confirmButton.setVisible(false);
        confirmButton.setDisable(true);
        secretGoal1Button.setDisable(true);
        secretGoal2Button.setDisable(true);
        startingFrontButton.setDisable(true);
        startingBackButton.setDisable(true);
        choicesMadeLabel.setText(choicesMadeLabel.getText() + "\n\nWaiting for all the players to\nmake their preliminary choices...");
        GuiApp.getGui().preliminaryChoicesMade(choicesMade);
    }

    /**
     * Updates the choices made label.
     */
    private void updateChoicesMadeLabel() {
        String text = "Choices made:\n\n";

        if(choicesMade[0] != null) {
            if(choicesMade[0])
                text += "First secret goal card\n";
            else
                text += "Second secret goal card\n";
        }
        if(choicesMade[1] != null) {
            if(choicesMade[1])
                text += "Starting card front";
            else
                text += "Starting card back";
        }

        String finalText = text;
        Platform.runLater(() -> choicesMadeLabel.setText(finalText));
    }
}
