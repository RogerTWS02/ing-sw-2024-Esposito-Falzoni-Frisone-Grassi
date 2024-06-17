package it.polimi.ingsw.view.TUI.GameState;

/**
 * StartGame class is used to print the start game message
 */
public class StartGame {

    /**
     * ShowStartGame method is used to print the start game message
     */
    public void ShowStartGame(){

        //clear the screen before printing something
        Views.clearScreen();
        // print StartGame

        System.out.println("┌──────────────────────────────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                                                                                                          │");
        System.out.println("│                                                                                                          │");
        System.out.println("│     ██████╗ ██████╗ ██████╗ ███████╗██╗  ██╗                                                             │");
        System.out.println("│    ██╔════╝██╔═══██╗██╔══██╗██╔════╝╚██╗██╔╝                                                             │");
        System.out.println("│    ██║     ██║   ██║██║  ██║█████╗   ╚███╔╝                                                              │");
        System.out.println("│    ██║     ██║   ██║██║  ██║██╔══╝   ██╔██╗                                                              │");
        System.out.println("│    ╚██████╗╚██████╔╝██████╔╝███████╗██╔╝ ██╗                                                             │");
        System.out.println("│     ╚═════╝ ╚═════╝ ╚═════╝ ╚══════╝╚═╝  ╚═╝                                                             │");
        System.out.println("│                                                                                                          │");
        System.out.println("│                                                                                                          │");
        System.out.println("│           ███╗   ██╗ █████╗ ████████╗██╗   ██╗██████╗  █████╗ ██╗     ██╗███████╗                        │");
        System.out.println("│           ████╗  ██║██╔══██╗╚══██╔══╝██║   ██║██╔══██╗██╔══██╗██║     ██║██╔════╝                        │");
        System.out.println("│           ██╔██╗ ██║███████║   ██║   ██║   ██║██████╔╝███████║██║     ██║███████╗                        │");
        System.out.println("│           ██║╚██╗██║██╔══██║   ██║   ██║   ██║██╔══██╗██╔══██║██║     ██║╚════██║                        │");
        System.out.println("│           ██║ ╚████║██║  ██║   ██║   ╚██████╔╝██║  ██║██║  ██║███████╗██║███████║                        │");
        System.out.println("│           ╚═╝  ╚═══╝╚═╝  ╚═╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝╚══════╝                        │");
        System.out.println("│                                                                                                          │");
        System.out.println("│                                                                                                          │");
        System.out.println("└──────────────────────────────────────────────────────────────────────────────────────────────────────────┘");
        System.out.println(" ");

    }
}
