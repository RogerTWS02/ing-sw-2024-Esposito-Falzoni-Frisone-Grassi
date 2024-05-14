package it.polimi.ingsw.view.TUI.GameState;

public class LogInPort {




    public void showLogInPort() {
        //clear the screen before printing something
        Views.clearScreen();
        StringBuilder sb = new StringBuilder();
        sb
                .append("┌")
                .append("─".repeat(106))
                .append("┐")
                .append("\n")
                .append("│")
                .append(" ".repeat(46))
                .append("Insert game IP")
                .append(" ".repeat(46))
                .append("│")
                .append("\n")
                .append("└")
                .append("─".repeat(106))
                .append("┘");


    }
}
