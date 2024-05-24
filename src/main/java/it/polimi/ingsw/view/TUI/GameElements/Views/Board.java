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
import java.util.ArrayList;
import java.util.List;

public class Board {

    private static final String mushroomColor = "\u001B[48;5;88m";
    private static final String leafColor = "\u001B[48;5;22m";
    private static final String wolfColor = "\u001B[48;5;26m";
    private static final String butterflyColor = "\u001B[48;5;91m";
    private static final String resetColor = "\u001B[0m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    private static final String ANSI_LIGHT_YELLOW = "\u001B[93m";

    public Board() {
    }

    //The isFlipped parameter is used only to determine whether the starting card is flipped or not
    public int drawBoard(Resource[][] boardResources, List<int[]> availablePositions) {

        String background;
        String border = "";
        StringBuilder sb = new StringBuilder();
        Object[][] mergedBoard = mergeBoards(boardResources, availablePositions);

        int sRow=0, sCol=0, eRow=81, eCol=81;


        //find the first useful column
        for (int i = 0; i < boardResources.length; i++) {
            boolean nullRow = false;
            for (int j = 0; j < boardResources.length; j++) {
                if (mergedBoard[i][j] != null) {
                    sRow = i;
                    nullRow = true;
                    break;
                }
            }
            //Exit the outer loop
            if (nullRow) {
                break;
            }
        }


        //find the first useful row
        for (int i = 0; i < boardResources.length; i++) {
            boolean nullRow = false;
            for (int j = 0; j < boardResources.length; j++) {
                if (mergedBoard[j][i] != null) {
                    sCol = i;
                    nullRow = true;
                    break;
                }
            }
            //Exit the outer loop
            if (nullRow) {
                break;
            }
        }


        //find the last useful column
        for (int i = boardResources.length - 1; i >= 0; i--) {
            boolean nullRow = false;
            for (int j = 0; j < boardResources.length; j++) {
                if (mergedBoard[i][j] != null) {
                    eRow = i + 1;
                    nullRow = true;
                    break;
                }
            }
            //Exit the outer loop
            if (nullRow) {
                break;
            }
        }

        //find the last useful row
        for (int i = boardResources.length - 1; i >= 0; i--) {
            boolean nullRow = false;
            for (int j = 0; j < boardResources.length; j++) {
                if (mergedBoard[j][i] != null) {
                    eCol = i + 1;
                    nullRow = true;
                    break;
                }
            }
            //Exit the outer loop
            if (nullRow) {
                break;
            }
        }
        for(int i= sRow; i < eRow; i++) {

            //Print the top border of each card of the row
            for (int j = sCol; j < eCol; j++) {
                if (mergedBoard[i][j] == null) {
                    sb.append(" ".repeat(10));
                    continue;
                }

                if (mergedBoard[i][j] == "available") {
                    border = ANSI_LIGHT_YELLOW;
                } else {
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

            for (int j = sCol; j < eCol; j++) {

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

                if (i == 40 && j == 40) {
                    background = ANSI_WHITE_BACKGROUND;
                }

                if (i >= 10 && j >= 10) {
                    sb
                            .append(border)
                            .append("│")
                            .append(background)
                            .append(" ")
                            .append(j)
                            .append("-")
                            .append(i)
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

            for (int j = sCol; j < eCol; j++) {
                if (mergedBoard[i][j] == null) {
                    sb.append(" ".repeat(10));
                    continue;
                }

                if (mergedBoard[i][j] == "available") {
                    border = ANSI_LIGHT_YELLOW;
                } else {
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
        return (eRow-sRow)*3;
    }

    public Object[][] mergeBoards(Resource[][] boardResources, List<int[]> availablePositions) {

        Object[][] mergedBoard = new Object[boardResources.length][boardResources.length];

        for (int i = 0; i < boardResources.length; i++) {
            for (int j = 0; j < boardResources.length; j++) {
                if (boardResources[i][j] != null) {

                    mergedBoard[i][j] = boardResources[i][j].toString();

                } else {
                    mergedBoard[i][j] = null;
                }
            }
        }
        for (int[] availablePosition : availablePositions) {
            mergedBoard[availablePosition[0]][availablePosition[1]] = "available";
        }
        return mergedBoard;
    }


    /*
    public static void main(String[] args) {

        //Test the drawBoard method
        Board board = new Board();
        Resource[][] boardResources = new Resource[20][20];
        List<int[]> availablePositions = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                if (i == 10 && j % 2 == 0) {
                    boardResources[i][j] = Resource.WOLF;
                } else if (i == 10) {
                    boardResources[i][j] = null;
                } else if ((i == 9 || i == 11) && j % 2 != 0) {
                    availablePositions.add(new int[]{i, j});
                }
            }
        }
        board.drawBoard(boardResources, availablePositions);
    }
    */

}

