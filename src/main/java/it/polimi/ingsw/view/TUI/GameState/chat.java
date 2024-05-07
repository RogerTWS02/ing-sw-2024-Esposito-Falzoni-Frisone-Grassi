package it.polimi.ingsw.view.TUI.GameState;

public class chat {

        public static void main(String[] args) {
                String[] test =new String[]{ "ciao", "Prova", "test"};


        }
public void chat(String[] msg){
        System.out.println("┌──────────────────────────────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                                                   CHAT                                                   │");
        System.out.println("├──────────────────────────────────────────────────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                                                                          │");
        for(int line=25; line>0; line--){

                System.out.println("│ "+ msg[msg.length-line] +" │");
                System.out.println("│                                                                                                          │");
        }
}

};
