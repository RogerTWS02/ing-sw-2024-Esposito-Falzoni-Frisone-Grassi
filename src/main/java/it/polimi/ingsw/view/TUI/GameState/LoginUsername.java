package it.polimi.ingsw.view.TUI.GameState;

public class LoginUsername {



    public void showLogInUsername() {
        //clear the screen before printing something
        //Views.clearScreen();
        StringBuilder sb = new StringBuilder();
        sb
                //.append("┌")
                //.append("─".repeat(46))
                //.append("┐")
                //.append("\n")
                //.append("│")
                //.append(" ".repeat(5))
                .append("Insert Username (max 16 characters):");
                //.append(" ".repeat(5))
                //.append("│")
                //.append("\n")
                //.append("└")
                //.append("─".repeat(46))
                //.append("┘")
                //.append("\n");

        System.out.print(sb);
    }
}
