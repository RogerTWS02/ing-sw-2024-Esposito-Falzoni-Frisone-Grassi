package it.polimi.ingsw.view.TUI.GameState;

public class LoginUsername {

    public void showLogInUsername() {
        //clear the screen before printing something
        Views.clearScreen();
        StringBuilder sb = new StringBuilder();
        sb
                .append("┌")
                .append("─".repeat(106))
                .append("┐")
                .append("\n")
                .append("│")
                .append(" ".repeat(36))
                .append("Insert Username (max 16 characters)")
                .append(" ".repeat(35))
                .append("│")
                .append("\n")
                .append("└")
                .append("─".repeat(106))
                .append("┘");


    }
}
