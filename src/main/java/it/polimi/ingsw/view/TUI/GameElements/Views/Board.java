package it.polimi.ingsw.view.TUI.GameElements.Views;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.view.TUI.GameState.Views;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Board {

    private static final String mushroomColor = "\u001B[41m";
    private static final String leafColor = "\u001B[42m";
    private static final String wolfColor = "\u001B[44m";
    private static final String butterflyColor = "\u001B[45m";
    private static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    private static final String resetColor = "\u001B[0m";
    private static final String border = "\u001B[37m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    private static final String ANSI_LIGHT_YELLOW= "\u001B[93m";

    public Board(){
    }

    //The isFlipped parameter is used only to determine whether the starting card is flipped or not
    public void drawBoard(Resource[][] boardResources, int[][] availablePositions) {

        String background;
        String border = "";
        StringBuilder sb = new StringBuilder();
        Object[][] mergedBoard = mergeBoards(boardResources, availablePositions);

        for(int i = 0; i < boardResources.length; i++) {
            boolean nullRow = true;
            for(int j = 0; j < boardResources.length; j++) {
                if(mergedBoard[i][j] != null) {
                    nullRow = false;
                    break;
                }
            }

            //Skip the row if it is empty
            if(nullRow) {
                continue;
            }

            //Print the top border of each card of the row
            for (int j = 0; j < boardResources.length; j++){
                if (mergedBoard[i][j] == null) {
                    sb.append(" ".repeat(10));
                    continue;
                }

                if(mergedBoard[i][j] == "available"){
                    border = ANSI_LIGHT_YELLOW;
                }else{
                    border = resetColor;
                }

                sb.append(border)
                        .append("┌")
                        .append("─".repeat(7))
                        .append("┐")
                        .append(" ")
                        .append(resetColor);
            }

            sb.append("\n");

            for (int j = 0; j < boardResources.length; j++) {

                if (mergedBoard[i][j] == null) {
                    sb.append(" ".repeat(10));
                    continue;
                }

                switch (mergedBoard[i][j].toString()) {
                    case "MUSHROOM" -> {
                        background = mushroomColor;
                        border = resetColor;
                    }
                    case "LEAF" -> {
                        background = leafColor;
                        border = resetColor;
                    }
                    case "WOLF" -> {
                        background = wolfColor;
                        border = resetColor;
                    }
                    case "BUTTERFLY" -> {
                        background = butterflyColor;
                        border = resetColor;
                    }
                    case "available" -> {
                        border = ANSI_LIGHT_YELLOW;
                        background = "";
                    }
                    default -> background = "";
                }

                if(i == 40 && j == 40){
                    background = ANSI_WHITE_BACKGROUND;
                }

                if (i >= 10 && j >= 10) {
                    sb
                            .append(border)
                            .append("│")
                            .append(background)
                            .append(" ")
                            .append(i)
                            .append(" ")
                            .append(j)
                            .append(" ")
                            .append(resetColor)
                            .append(border)
                            .append("│")
                            .append(" ");
                }
                if (i < 10 && j < 10) {
                    sb
                            .append(border)
                            .append("│")
                            .append(background)
                            .append(" ")
                            .append(i)
                            .append(" ".repeat(3))
                            .append(j)
                            .append(" ")
                            .append(resetColor)
                            .append(border)
                            .append("│")
                            .append(" ");

                }
                if (i >= 10 && j < 10 || i < 10 && j >= 10) {
                    sb
                            .append(border)
                            .append("│")
                            .append(background)
                            .append(" ")
                            .append(i)
                            .append("  ")
                            .append(j)
                            .append(" ")
                            .append(resetColor)
                            .append(border)
                            .append("│")
                            .append(" ");

                }
            }

            sb.append("\n");

            for (int j = 0; j < boardResources.length; j++){
                if (mergedBoard[i][j] == null) {
                    sb.append(" ".repeat(10));
                    continue;
                }

                if(mergedBoard[i][j] == "available"){
                    border = ANSI_LIGHT_YELLOW;
                }else{
                    border = resetColor;
                }

                sb.append(border)
                        .append("└")
                        .append("─".repeat(7))
                        .append("┘")
                        .append(" ")
                        .append(resetColor);
            }

            sb.append("\n");
        }
        System.out.println(sb);
    }

    public Object[][] mergeBoards(Resource[][] boardResources, int[][] availablePositions) {

        Object[][] mergedBoard = new Object[boardResources.length][boardResources.length];

        for(int i = 0; i < boardResources.length; i++) {
            for(int j = 0; j < boardResources.length; j++) {
                if(boardResources[i][j] != null) {

                    mergedBoard[i][j] = boardResources[i][j].toString();

                }else if(availablePositions[i][j] == 1){

                    mergedBoard[i][j] = "available";

                }else{
                    mergedBoard[i][j] = null;
                }
            }
        }
        return mergedBoard;
    }

    public static void main(String[] args) {

        //Test the drawBoard method
        Board board = new Board();
        Resource[][] boardResources = new Resource[20][20];
        int[][] availablePositions = new int[20][20];

        for(int i = 0; i < 20; i++) {
            for(int j = 0; j < 20; j++) {
                if(i == 10 && j%2 == 0){
                    boardResources[i][j] = Resource.WOLF;
                }else if (i==10){
                    boardResources[i][j] = null;
                }else if ((i == 9 || i == 11) && j%2 != 0){
                    availablePositions[i][j] = 1;
                }
            }
        }

        board.drawBoard(boardResources, availablePositions);
    }

}

