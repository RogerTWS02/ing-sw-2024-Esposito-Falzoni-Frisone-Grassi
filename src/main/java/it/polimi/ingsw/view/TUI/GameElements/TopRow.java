package it.polimi.ingsw.view.TUI.GameElements;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.view.TUI.GameState.Views;

import java.util.*;

public class TopRow implements Views {

    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GRAY_BACKGROUND = "\u001B[32m";

    public void showTopRow(String currentPlayer, Map<String, Integer> nicknamesScores, ArrayList<Resource> playerResources) {

        // Map of resources and their occurrences
        Map<Resource, Integer> resourceOccurrences = new HashMap<>();

        List<String> players = new ArrayList<>(nicknamesScores.keySet());
        // If the player has resources, count the occurrences of each resource
        if (playerResources != null) {
            for (Resource item : playerResources) {
                if (resourceOccurrences.containsKey(item)) {
                    resourceOccurrences.put(item, resourceOccurrences.get(item) + 1);
                } else {
                    resourceOccurrences.put(item, 1);
                }
            }

        }
        // Get the next player
        String nextPlayer = players.get((players.indexOf(currentPlayer) + 1) % players.size());


        StringBuilder topRow = new StringBuilder();
        topRow
                .append("\n")
                .append("┌")
                .append("─".repeat(125))
                .append("┐")
                .append("\n")
                .append(" ")
                .append("Points ");


        // Print the nicknames and the scores of the players
        for (Map.Entry<String, Integer> entry : nicknamesScores.entrySet()) {
            topRow
                    .append(ANSI_GRAY_BACKGROUND)
                    .append(entry.getKey())
                    .append(ANSI_RESET)
                    .append(": ")
                    .append(entry.getValue())
                    .append(" ");
        }
        // Print the current player, the next player
            topRow
                    .append("\n")
                    .append(" Now it's ")
                    .append(ANSI_BLUE)
                    .append(currentPlayer)
                    .append(ANSI_RESET)
                    .append("'s turn")
                    .append(" │ ")
                    .append("Next player: ")
                    .append(ANSI_BLUE)
                    .append(nextPlayer)
                    .append(ANSI_RESET)
                    .append(" │ ")
                    .append(" Resources:");
            // Print the resources of the player
            for (Map.Entry<Resource, Integer> entry : resourceOccurrences.entrySet()) {
                topRow
                        .append(" ")
                        .append(Views.stringToEmoji(entry.getKey().toString()))
                        .append("X")
                        .append(entry.getValue());

            }
            topRow
                    .append("\n")
                    .append("└")
                    .append("─".repeat(125))
                    .append("┘")
                    .append("\n");

            System.out.println(topRow);

        }
}