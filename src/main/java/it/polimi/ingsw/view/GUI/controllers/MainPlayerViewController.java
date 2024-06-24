package it.polimi.ingsw.view.GUI.controllers;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPlayerViewController implements Initializable {

    /**
     * Initializes the GridPane with buttons.
     *
     * @param url Ignored.
     * @param resourceBundle Ignored.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GridPane gridPane = new GridPane();

        // Loop to create and add buttons to the GridPane
        for (int rowIndex = 0; rowIndex < 81; rowIndex++) {
            for (int colIndex = 0; colIndex < 81; colIndex++) {
                if((rowIndex+colIndex) % 2 != 0) continue;
                Button button = new Button();
                button.setMinSize(50, 50); // Set button size
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Make button fill cell

                // Set button action
                int finalRowIndex = rowIndex;
                int finalColIndex = colIndex;
                button.setOnAction(event -> onButtonClicked(finalRowIndex, finalColIndex));

                // Add button to GridPane
                gridPane.add(button, colIndex, rowIndex);
            }
        }
    }

    /**
     * Handles the button click event.
     *
     * @param rowIndex The row index of the clicked button.
     * @param colIndex The column index of the clicked button.
     * @return An array containing the row and column indexes of the clicked button.
     */
    public Integer[] onButtonClicked(Integer rowIndex, Integer colIndex){
        return new Integer[]{rowIndex, colIndex};
    }
}
