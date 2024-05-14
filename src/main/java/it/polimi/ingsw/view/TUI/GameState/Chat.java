package it.polimi.ingsw.view.TUI.GameState;

public class Chat implements Views{

        public static void main(String[] args) {

        }
public void showChat(String[] msg){

                //clear the screen before printing something
                Views.clearScreen();



                // print the Chat
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
            .append("┘");
    System.out.println(sb);}


};
