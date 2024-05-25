package it.polimi.ingsw.view.TUI.GameElements.Views;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.view.TUI.GameState.Views;

import java.util.*;

public class TopRow implements Views {

    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GRAY_BACKGROUND = "\u001B[32m";

    public void showTopRow(String currentPlayer, ArrayList<String> players, ArrayList<Integer> scores, ArrayList<Resource> playerResources) {

        Map<Resource, Integer> resourceOccurrences = new HashMap<>();
        Map<Integer, String> scoreMap = new HashMap<>();
        TreeMap<Integer, String> treeMap = new TreeMap<>(Collections.reverseOrder());
        if (scores != null) {
            for (int i = 0; i < scores.size(); i++) {
                scoreMap.put(scores.get(i), players.get(i));
            }
            treeMap.putAll(scoreMap);
        } else {
            scores = new ArrayList<>(Collections.nCopies(players.size(), 0));}
            if (playerResources != null) {
                for (Resource item : playerResources) {
                    if (resourceOccurrences.containsKey(item)) {
                        resourceOccurrences.put(item, resourceOccurrences.get(item) + 1);
                    } else {
                        resourceOccurrences.put(item, 1);
                    }
                }

            }
            String nextPlayer = players.get((players.indexOf(currentPlayer) + 1)%players.size());


            StringBuilder topRow = new StringBuilder();
            topRow
                    .append("\n")
                    .append("┌")
                    .append("─".repeat(125))
                    .append("┐")
                    .append("\n")
                    .append(" ")
                    .append("Points ");
            for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
                topRow
                        .append(ANSI_GRAY_BACKGROUND)
                        .append(entry.getValue())
                        .append(ANSI_RESET)
                        .append(": ")
                        .append(entry.getKey())
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