package it.polimi.ingsw.view.TUI.GameState;

/**
 * LogInPort class is used to show the LogInPort view.
 */
public class LogInPort {

    /**
     * This method is used to show the LogInPort view.
     */
    public void showLogInPort() {
        //clear the screen before printing something
        Views.clearScreen();
        StringBuilder sb = new StringBuilder();
        sb
                //.append("┌")
                //.append("─".repeat(46))
                //.append("┐")
                //.append("\n")
                //.append("│")
                //.append(" ".repeat(15))
                .append("Insert server IP: ");
                //.append(" ".repeat(14))
                //.append("│")
                //.append("\n")
                //.append("└")
                //.append("─".repeat(46))
                //.append("┘")
                //.append("\n");
        System.out.print(sb);

    }
}
