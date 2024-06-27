package it.polimi.ingsw.view.GUI.controllers;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import it.polimi.ingsw.model.Resource;

public class PlayerBoardController implements Initializable {
    public GridPane startingCard;
    public GridPane playerBoard;
    private Button prevSelect;
    int prevX = -1, prevY = -1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
                button.setOnMouseReleased(event -> onButtonClicked(finalRowIndex, finalColIndex));

                // Add button to GridPane
                playerBoard.add(button, colIndex, rowIndex);
            }
        }
    }

    public int[] getCoords(){
        return new int[]{prevX, prevY};
    }

    /**
     * Handles the button click event.
     *
     * @param rowIndex The row index of the clicked button.
     * @param colIndex The column index of the clicked button.
     */
    public void onButtonClicked(Integer rowIndex, Integer colIndex){
        //revert the changes of the previous button
        prevSelect.setStyle("-fx-background-color: initial; " +
                            "-fx-border-color: initial;" +
                            "-fx-border-width: initial;");

        for (Node node : playerBoard.getChildren()) {
            //checks the right cell containing the node
            if (Objects.equals(GridPane.getRowIndex(node), rowIndex) && Objects.equals(GridPane.getColumnIndex(node), colIndex)) {
                //change the stile to the selected node
                ((Button) node).setText("");
                node.setStyle("-fx-background-color: transparent; " +
                              "-fx-border-color: red;" +
                              "-fx-border-width: 3;");

                prevSelect = (Button) node;
                prevX = rowIndex;
                prevY = colIndex;
                break;
            }
        }

    }

    /**
     * Update the player board, showing the starting card.
     *
     * @param prevUUID The UUID of the starting card.
     */
    public void updatePlayerBoard(String prevUUID, boolean side, Resource res, List<int[]> available){
        Platform.runLater(() -> {
            //update the cell with the new card on the board
            String UUID;
            if(side){
                UUID = "RC_"+res.toString()+"_B";
            }else{
                UUID = prevUUID;
            }
            //set the right background for the starting card
            String imagePath = "/graphics/startingDeck/" + UUID + ".png";
            String style = String.format("-fx-background-image: url('%s');", imagePath);
            prevSelect.setStyle(style+
                                "-fx-border-color: initial;" +
                                "-fx-border-width: initial;");

            //update the board with the new available cards
            for (Node node : playerBoard.getChildren()) {
                for(int[] pos : available) {
                    if (playerBoard.getRowIndex(node) == pos[0] && playerBoard.getColumnIndex(node) == pos[1]) {
                        //update the new available cells
                        ((Button) node).setVisible(true);
                        break;
                    }
                }
            }

        });

    }
}
