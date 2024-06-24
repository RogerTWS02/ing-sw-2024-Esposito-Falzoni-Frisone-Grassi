package it.polimi.ingsw.view.GUI.controllers;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainPlayerViewController extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
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
        Scene scene = new Scene(gridPane, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chessboard Buttons");
        primaryStage.show();
    }

    public Integer[] onButtonClicked(Integer rowIndex, Integer colIndex){
        return new Integer[]{rowIndex, colIndex};
    }

    public static void main(String[] args) {
        launch(args);
    }
}
