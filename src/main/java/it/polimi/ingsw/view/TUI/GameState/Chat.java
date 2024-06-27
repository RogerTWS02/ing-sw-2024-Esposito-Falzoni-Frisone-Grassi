package it.polimi.ingsw.view.TUI.GameState;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Chat class is used to print the chat on the screen.
 */
public class Chat implements Views{

    /**
     * Shows the chat on the screen.
     *
     * @param msg The messages to be shown on the chat.
     */
    public void showChat(Queue<String> msg){
    StringBuilder sb = new StringBuilder();
    StringBuilder st = new StringBuilder();
    Views.clearScreen();
        System.out.println("\n".repeat(100));
    sb
            .append("+")
            .append("-".repeat(106))
            .append("+")
            .append("\n")
            .append(" ".repeat(47))
            .append("Chat Room")
            .append(" ".repeat(30))
            .append("\n")
            .append("+")
            .append("-".repeat(106))
            .append("+")
            .append("\n");
        System.out.println(sb);
            msg.forEach(System.out::print);
    st
            .append("+")
            .append("-".repeat(106))
            .append("+")
            .append("\n");
    System.out.println(st);
    // Clear the screen before printing something

    }
}
