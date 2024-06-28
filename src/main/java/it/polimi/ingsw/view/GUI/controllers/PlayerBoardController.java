package it.polimi.ingsw.view.GUI.controllers;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import it.polimi.ingsw.model.Resource;

public class PlayerBoardController implements Initializable {
    public AnchorPane anchorPane;
    public ImageView image;
    private Button prevSelect;
    int prevX = 0, prevY = 0;

    /**
     * Initializes the player board.
     *
     * @param url Ignored.
     * @param resourceBundle Ignored.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GridPane gridPane = new GridPane();
        anchorPane.getChildren().add(gridPane);
        anchorPane.setTopAnchor(gridPane, 0.0);
        anchorPane.setBottomAnchor(gridPane, 0.0);
        anchorPane.setLeftAnchor(gridPane, 0.0);
        anchorPane.setRightAnchor(gridPane, 0.0);

        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                Button button = new Button();
                int finalJ = j;
                int finalI = i;
                button.setOnMouseClicked(event -> onButtonClicked(finalI, finalJ, button));
                gridPane.add(button, i, j);

                /*if(i == 0 && j == 0)
                    prevSelect = button;*/
            }
        }



        // Loop to create and add buttons to the GridPane
        /*for (int rowIndex = 0; rowIndex < 81; rowIndex++) {
            for (int colIndex = 0; colIndex < 81; colIndex++) {
                ImageView imageView = new ImageView();
                imageView.setVisible(false);
                imageView.setDisable(true);
                imageView.setId(String.format("img%d%d",rowIndex, colIndex));
                GridPane.setHgrow(imageView, Priority.ALWAYS);
                GridPane.setVgrow(imageView, Priority.ALWAYS);
                //imageView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Make button fill cell

                // Set button action
                int finalRowIndex = rowIndex;
                int finalColIndex = colIndex;
                imageView.setOnMouseClicked(event -> onButtonClicked(finalRowIndex, finalColIndex));

                // Add button to GridPane
                playerBoard.getChildren().add(imageView);
            }
        }*/
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
    public void onButtonClicked(Integer rowIndex, Integer colIndex, Button button){
        if(prevSelect == null)
            prevSelect = button;
        else {
            prevSelect.setStyle("-fx-border-color: initial;" +
                                "-fx-border-width: initial;");
        }

        //button.setStyle("-fx-background-image: url('/graphics/startingDeck/SC_1.png');");

        //revert the changes of the previous button
        /*prevSelect.setStyle("-fx-background-color: initial; " +
                            "-fx-border-color: initial;" +
                            "-fx-border-width: initial;");*/

        /*for (Node node : playerBoard.getChildren()) {
            //checks the right cell containing the node
            if (Objects.equals(GridPane.getRowIndex(node), rowIndex) && Objects.equals(GridPane.getColumnIndex(node), colIndex)) {
                //change the stile to the selected node
                button.setStyle("-fx-background-color: transparent; " +
                              "-fx-border-color: red;" +
                              "-fx-border-width: 3;");

                prevSelect = (Button) node;
                prevX = rowIndex;
                prevY = colIndex;
                break;
            }
        }*/

        //change the stile to the selected node
        button.setStyle("-fx-background-color: transparent; " + "-fx-border-color: red;" + "-fx-border-width: 3;");
        prevSelect = button;
        prevX = rowIndex;
        prevY = colIndex;

        /*prevSelect = (ImageView) playerBoard.getChildren().get(rowIndex+81*colIndex);
        prevSelect.setStyle("-fx-background-color: transparent; " +
                "-fx-border-color: red;" +
                "-fx-border-width: 3;");

        prevX = rowIndex;
        prevY = colIndex;*/
    }

    /**
     * Update the player board, showing the starting card.
     *
     * @param prevUUID The UUID of the starting card.
     */
    public void updatePlayerBoard(String prevUUID, boolean side, Resource res, List<int[]> available){
            String UUID;
            if(res == null) {
                //Insert the starting card
                if(side)
                    UUID = prevUUID + "_B";
                else
                    UUID = prevUUID;

                //String style = "url('/graphics/startingDeck/" + UUID + ".png');";
                //prevSelect.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/graphics/startingDeck/" + UUID + ".png"))));
                //prevSelect.setDisable(true);
                Image image = new Image(getClass().getResourceAsStream("/graphics/startingDeck/" + UUID + ".png"));
                //Platform.runLater(() -> prevSelect.setGraphic(new ImageView(image)));
                Platform.runLater(() -> this.image.setImage(image));

                //for (Node node : playerBoard.getChildren()){

                    //if (Objects.equals(GridPane.getRowIndex(node), 40) && Objects.equals(GridPane.getColumnIndex(node), 40)) {

                //prevSelect = (ImageView) playerBoard.lookup("#img4040");
                //prevSelect.setVisible(true);
                //prevSelect.setDisable(false);

                //Image image = new Image(getClass().getResourceAsStream("/graphics/startingDeck/" + UUID + ".png"));
                //prevSelect.setImage(image);

                /*
                        //update the new available cells
                        node.setVisible(true);
                        String imagePath = "/graphics/startingDeck/" + UUID + ".png";
                        String style = String.format("-fx-background-image: url('%s');", imagePath);
                        node.setStyle(style+
                                "-fx-border-color: initial;" +
                                "-fx-border-width: initial;");*/

                   // }
                //}

                /*//update the board with the new available cards
                for (Node node : playerBoard.getChildren()) {
                    for(int[] pos : available) {
                        if (Objects.equals(GridPane.getRowIndex(node), pos[0]) && Objects.equals(GridPane.getColumnIndex(node), pos[1])) {
                            //update the new available cells
                            ((Button) node).setVisible(true);
                            break;
                        }
                    }
                }*/


                return;
            }


            //update the cell with the new card on the board
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
            /*for (Node node : playerBoard.getChildren()) {
                for(int[] pos : available) {
                    if (playerBoard.getRowIndex(node) == pos[0] && playerBoard.getColumnIndex(node) == pos[1]) {
                        //update the new available cells
                        ((Button) node).setVisible(true);
                        break;
                    }
                }
            }*/

    }
}
