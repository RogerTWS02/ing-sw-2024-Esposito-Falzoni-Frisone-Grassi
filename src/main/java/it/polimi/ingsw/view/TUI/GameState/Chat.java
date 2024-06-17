package it.polimi.ingsw.view.TUI.GameState;

/**
 * Chat class is used to print the chat on the screen.
 */
public class Chat implements Views{

    /**
     * Shows the chat on the screen.
     *
     * @param msg The messages to be shown on the chat.
     */
    public void showChat(String[] msg){
    StringBuilder sb = new StringBuilder();
    sb
            .append("┌")
            .append("─".repeat(106))
            .append("┐")
            .append("\n")
            .append("│")
            .append(" ".repeat(51))
            .append("CHAT")
            .append(" ".repeat(51))
            .append("│")
            .append("\n")
            .append("├")
            .append("─".repeat(106))
            .append("┤")
            .append("\n");
             for(int line = 25; line >0; line --){
                 sb
                      .append("│ ")
                      .append(msg[msg.length-line])
                      .append(" │")
                      .append("\n");
             }
    sb
            .append("└")
            .append("─".repeat(106))
            .append("┘")
            .append("\n");
    // Clear the screen before printing something
    Views.clearScreen();
    // print the Chat
    System.out.println(sb);}
}
