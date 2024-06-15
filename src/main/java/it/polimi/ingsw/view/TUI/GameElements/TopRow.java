package it.polimi.ingsw.view.TUI.GameElements;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.view.TUI.GameState.Views;

import java.util.*;

public class TopRow implements Views {

    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GRAY_BACKGROUND = "\u001B[32m";

    public void showTopRow(String currentPlayer, Map<String, Integer> nicknamesScores, ArrayList<Resource> playerResources) {

        Map<Resource, Integer> resourceOccurrences = new HashMap<>();
        TreeMap<String, Integer> treeMap = new TreeMap<>(Collections.reverseOrder());

        List<String> players = new ArrayList<>(nicknamesScores.keySet());

        treeMap.putAll(nicknamesScores);
        if (playerResources != null) {
            for (Resource item : playerResources) {
                if (resourceOccurrences.containsKey(item)) {
                    resourceOccurrences.put(item, resourceOccurrences.get(item) + 1);
                } else {
                    resourceOccurrences.put(item, 1);
                }
            }

        }
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

        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            topRow
                    .append(ANSI_GRAY_BACKGROUND)
                    .append(entry.getKey())
                    .append(ANSI_RESET)
                    .append(": ")
                    .append(entry.getValue())
                    .append(" ");
        }
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