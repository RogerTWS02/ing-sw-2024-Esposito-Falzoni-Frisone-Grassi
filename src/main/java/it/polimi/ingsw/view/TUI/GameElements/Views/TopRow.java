package it.polimi.ingsw.view.TUI.GameElements.Views;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.view.TUI.GameState.Views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TopRow implements Views {

    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";
    public void showTopRow(String currentPlayer, ArrayList<String> Players, ArrayList<Resource> playerResources) {

        Map<Resource, Integer> resourceOccurrences = new HashMap<>();
        for (Resource item : playerResources) {
            if (resourceOccurrences.containsKey(item)) {
                resourceOccurrences.put(item, resourceOccurrences.get( item) + 1);
            } else {
                resourceOccurrences.put(item, 1);
            }
        }
        String nextPlayer = Players.get(Players.indexOf(currentPlayer) + 1);

        StringBuilder topRow = new StringBuilder();
        topRow
                .append("┌")
                .append("─".repeat(106))
                .append("┐")
                .append("\n")
                .append("│")
                .append("Now it's ")
                .append(ANSI_BLUE)
                .append(currentPlayer)
                .append(ANSI_RESET)
                .append("'s turn")
                .append(" │ ")
                .append(" Next player will be:")
                .append(ANSI_BLUE)
                .append(nextPlayer)
                .append(ANSI_RESET);




    }

}
