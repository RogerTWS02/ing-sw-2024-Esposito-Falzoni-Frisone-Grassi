package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.server.RMIServerImpl;
import it.polimi.ingsw.network.server.RMIServerInterface;

import java.util.Scanner;

public class RMIGameFlow {
    private final RMIServerInterface stub;
    private final Client client;

    public RMIGameFlow(RMIServerInterface stub, Client cli) {
        this.client = cli;
        this.stub = stub;
    }

    /**
     * This method is used to start the game flow.
     */
    public void run() {
        Thread gameThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String[] command;
            System.out.println("WELCOME TO CODEX NATURALIS!!\n");
            System.out.println("Please enter a command  ");

            while(true){
                command = scanner.nextLine().split(" ");
                try {
                    switch(command[0]){

                        case "/help":
                            System.out.println("""
                                    Commands List:\s
                                    /infoCard posX posY - return info on a card on the playerBoard
                                    /placeCard posX posY - try to place a card on the playerBoard
                                    /drawCardFromDeck Golden/Resource - draws a card form Deck according to te specified type
                                    /drawCardFromViewable Golden/Resource 1/2 - draws from the viewable cards according to the specified index
                                    /openChat - opens the chat tab where you can type messages to the lobby and watch the messages from others players
                                    /closeChat - closes the chat tab and resume the game interface
                                    /TEMPLATE PARAM_1, PARAM_2
                            """);
                            break;

                        case "/infoCard":
                            if(command.length < 3) break;
                            int posX, posY;
                            try {
                                posX = Integer.parseInt(command[1]);
                                posY = Integer.parseInt(command[2]);

                            }catch(NumberFormatException e){
                                System.out.println("Invalid command. Please type '/help' for a list of commands.\n");
                                break;
                            }
                            stub.requestinfoCard(posX, posY);

                        default:
                            System.out.println("Invalid command. Please type '/help' for a list of commands.\n");
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
