package it.polimi.ingsw.view.TUI.GameState;

public class chat {

        public static void main(String[] args) {

        }
public void chat(String[] msg){
                System.out.println("┌──────────────────────────────────────────────────────────────────────────────────────────────────────────┐");
                System.out.println("│                                                   CHAT                                                   │");
                System.out.println("├──────────────────────────────────────────────────────────────────────────────────────────────────────────┤");
                System.out.println("│                                                                                                          │");
                // msg array of String of 105 characters
        for(int line=25; line>0; line--){
                System.out.println("│ "+ msg[msg.length-line] +" │");
                System.out.println("│                                                                                                          │");}
                System.out.println("└──────────────────────────────────────────────────────────────────────────────────────────────────────────┘");
}

};
