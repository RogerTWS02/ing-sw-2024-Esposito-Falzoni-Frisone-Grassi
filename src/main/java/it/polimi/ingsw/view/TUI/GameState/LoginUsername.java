package it.polimi.ingsw.view.TUI.GameState;

/**
 * This class is used to print the message asking the user to insert the username */
public class LoginUsername {

    /**
     * Shows the message asking the user to insert the username.
     */
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
                .append("Insert username (max 16 characters): ");
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
