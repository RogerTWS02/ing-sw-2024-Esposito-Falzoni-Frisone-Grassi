package it.polimi.ingsw.view.TUI.GameState;

public class chat implements Views{

        public static void main(String[] args) {
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
                        .append("│")
                        .append(" ".repeat(106))
                        .append("│")
                        .append("\n");
            }
            sb
                    .append("└")
                    .append("─".repeat(106))
                    .append("┘");
            System.out.println(sb);
        }
public void showChat(String[] msg){

                //clear the screen before printing something
                Views.clearScreen();


                //Lorenzo Method
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
