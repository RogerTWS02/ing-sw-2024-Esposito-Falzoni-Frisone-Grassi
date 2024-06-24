package it.polimi.ingsw.view.TUI.GameState;

import java.util.ArrayList;

public class Endgame implements Views {
    public static void main(String[] args) {
        Endgame endgame = new Endgame();
        ArrayList<String> nicknames = new ArrayList<>();
        nicknames.add("Player1");
        nicknames.add("Player2");
        endgame.showEndgame(nicknames);
    }

    public void showEndgame(ArrayList<String> nicknames) {
        StringBuilder endgame = new StringBuilder();
        endgame
                .append("████████╗██╗  ██╗███████╗     ██████╗  █████╗ ███╗   ███╗███████╗    ██╗  ██╗ █████╗ ███████╗    ███████╗███╗   ██╗██████╗ ███████╗██████╗ ██╗\n")
                .append("╚══██╔══╝██║  ██║██╔════╝    ██╔════╝ ██╔══██╗████╗ ████║██╔════╝    ██║  ██║██╔══██╗██╔════╝    ██╔════╝████╗  ██║██╔══██╗██╔════╝██╔══██╗██║\n")
                .append("   ██║   ███████║█████╗      ██║  ███╗███████║██╔████╔██║█████╗      ███████║███████║███████╗    █████╗  ██╔██╗ ██║██║  ██║█████╗  ██║  ██║██║\n")
                .append("   ██║   ██╔══██║██╔══╝      ██║   ██║██╔══██║██║╚██╔╝██║██╔══╝      ██╔══██║██╔══██║╚════██║    ██╔══╝  ██║╚██╗██║██║  ██║██╔══╝  ██║  ██║╚═╝\n")
                .append("   ██║   ██║  ██║███████╗    ╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗    ██║  ██║██║  ██║███████║    ███████╗██║ ╚████║██████╔╝███████╗██████╔╝██╗\n")
                .append("   ╚═╝   ╚═╝  ╚═╝╚══════╝     ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝    ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝    ╚══════╝╚═╝  ╚═══╝╚═════╝ ╚══════╝╚═════╝ ╚═╝\n")
                .append("-".repeat(142))
                .append("\n");


        if(nicknames.size()>1)endgame.append("The winners are...\n");
        else endgame.append("The winner is...\n");
        for (int i = 0; i < nicknames.size(); i++) {
            endgame.append("-"+nicknames.get(i)).append("\n");
        }
        endgame
                .append("-".repeat(142))
                .append("\n")
                .append("Congratulations!\n")
                .append("thank you for playing\n");

        Views.clearScreen();
        System.out.println(endgame);
    }

}
