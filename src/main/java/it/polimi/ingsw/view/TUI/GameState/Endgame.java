package it.polimi.ingsw.view.TUI.GameState;

import java.util.ArrayList;
import java.util.Map;

public class Endgame implements Views {


    public void showEndgame(ArrayList<String> nicknames, Map<String,Integer> nickScore) {
        StringBuilder endgame = new StringBuilder();
        endgame
                .append("\n".repeat(2))
                .append("████████╗██╗  ██╗███████╗     ██████╗  █████╗ ███╗   ███╗███████╗    ██╗  ██╗ █████╗ ███████╗    ███████╗███╗   ██╗██████╗ ███████╗██████╗ ██╗\n")
                .append("╚══██╔══╝██║  ██║██╔════╝    ██╔════╝ ██╔══██╗████╗ ████║██╔════╝    ██║  ██║██╔══██╗██╔════╝    ██╔════╝████╗  ██║██╔══██╗██╔════╝██╔══██╗██║\n")
                .append("   ██║   ███████║█████╗      ██║  ███╗███████║██╔████╔██║█████╗      ███████║███████║███████╗    █████╗  ██╔██╗ ██║██║  ██║█████╗  ██║  ██║██║\n")
                .append("   ██║   ██╔══██║██╔══╝      ██║   ██║██╔══██║██║╚██╔╝██║██╔══╝      ██╔══██║██╔══██║╚════██║    ██╔══╝  ██║╚██╗██║██║  ██║██╔══╝  ██║  ██║╚═╝\n")
                .append("   ██║   ██║  ██║███████╗    ╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗    ██║  ██║██║  ██║███████║    ███████╗██║ ╚████║██████╔╝███████╗██████╔╝██╗\n")
                .append("   ╚═╝   ╚═╝  ╚═╝╚══════╝     ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝    ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝    ╚══════╝╚═╝  ╚═══╝╚═════╝ ╚══════╝╚═════╝ ╚═╝\n")
                .append("-".repeat(142))
                .append("\n".repeat(2));

        endgame.append(" ".repeat(62));
        if(nicknames.size()>1)endgame.append("\u001B[1mThe winners are...\n\n");
        else endgame.append("\u001B[1mThe winner is...\n\n");
        for (int i = 0; i < nicknames.size(); i++) {
            int allign = (142 - nicknames.get(i).length()-16)/2 ;
            endgame .append(" ".repeat(allign))
                    .append(nicknames.get(i))
                    .append(" with ")
                    .append(nickScore.get(nicknames.get(i)))
                    .append(" points!")
                    .append("\n");
        }

        endgame
                .append("\n")
                .append(" ".repeat(61))
                .append("Other players scores:")
                .append("\n".repeat(2));

        for(Map.Entry<String,Integer> entry: nickScore.entrySet()){
            if(!nicknames.contains(entry.getKey())) {
                int allign = (142 - entry.getKey().length()-16)/2 ;
                endgame .append(" ".repeat(allign))
                        .append(entry.getKey())
                        .append(" with ")
                        .append(entry.getValue())
                        .append(" points!")
                        .append("\n");
            }
        }

        endgame
                .append("\n")
                .append("-".repeat(142))
                .append("\n")
                .append(" ".repeat(63))
                .append("Congratulations!\n")
                .append(" ".repeat(60))
                .append("Thank you for playing\n");

        Views.clearScreen();
        System.out.println(endgame);
    }

}
