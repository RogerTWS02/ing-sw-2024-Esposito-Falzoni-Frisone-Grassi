package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.server.RMIServerInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

import static it.polimi.ingsw.network.message.MessageType.REQUEST_CARD;
import static it.polimi.ingsw.network.message.MessageType.REQUEST_PLAYER_MOVE;

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
    public void run() throws IOException {
            Scanner scanner = new Scanner(System.in);
            String[] command;
            System.out.println("WELCOME TO CODEX NATURALIS!!\n");

            command = scanner.nextLine().split(" ");

                int numPlayers;
                System.out.println("Please type the number of players you want in the lobby (from 2 up to 4 players): ");
                while(true){
                    try {
                        numPlayers = scanner.nextInt();
                        if (numPlayers >= 2 && numPlayers <= 4) {
                            break;
                        }
                        System.out.println("Please type a valid number (the size has to be between 2 and 4): ");
                    }catch (Exception e){
                        System.out.println("Please type a valid number (the size has to be between 2 and 4): ");
                    }
                }

                System.out.println("Please type the nickname you want to use: ");
                String nickname;
                while(true){
                    try{
                        nickname = scanner.nextLine();
                        break;
                    }catch (Exception e){
                        System.out.println("Please type a valid nickname: ");
                    }
                }
                //stub.newLobbyRequest(nickname, , numPlayers, client.getClientListener(), client.getClientID());


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
                            stub.requestinfoCard(posX, posY, client.getClientID());
                            break;

                        case "/placeCard":
                            if(command.length < 3) break;

                            int positionX= 0, positionY = 0;
                            try {
                                positionX = Integer.parseInt(command[1]);
                                positionY = Integer.parseInt(command[2]);

                            }catch(NumberFormatException e){
                                System.out.println("Invalid command. Please type '/help' for a list of commands.\n");
                                break;
                            }
                            //Commented because we have to parse the card to pass it to the server method(?)
                            //stub.playerMove(client.getClientID(), card, positionX, positionY);
                            break;

                        case "/drawCardFromDeck":
                            String ty = command[1].toLowerCase();

                            if(ty.equals("golden")){
                               stub.cardRequest(false, 2, client.getClientID());
                            }
                            if(ty.equals("resource")){
                                stub.cardRequest(false, 1, client.getClientID());
                            }
                            break;

                        case "/drawCardFromViewable":
                            if(command.length < 2 || command.length > 3){
                                System.out.println("Invalid command. Please type '/help' for a list of commands.\n");
                                break;
                            }
                            String type = command[0].toLowerCase();
                            int pos = 0;
                            try {
                                pos = Integer.parseInt(command[1]);
                                if(pos > 2 || pos < 1){
                                    System.out.println("Invalid command. Please type '/help' for a list of commands.\n");
                                    break;
                                }
                            }catch(NumberFormatException e){
                                System.out.println("Invalid command. Please type '/help' for a list of commands.\n");
                                break;
                            }

                            if(!type.equals("resource") && !type.equals("golden")){
                                System.out.println("Invalid command. Please type '/help' for a list of commands.\n");
                                break;
                            }
                            if(type.equals("golden")){
                                stub.cardRequest(true, pos - 1, client.getClientID());
                            }
                            if(type.equals("resource")){
                                stub.cardRequest(true, pos - 1, client.getClientID());
                            }
                            break;

                        case "/newLobby":
                            if(command.length < 3 || command.length > 4){
                                System.out.println("Invalid command. Please type '/help' for a list of commands.\n");
                                break;
                            }

                            stub.newLobbyRequest(command[1], command[2],Integer.parseInt(command[3]), client.getClientListener(), client.getClientID());
                            break;

                        case "/joinLobby":
                            if(command.length < 2 || command.length > 2){
                                System.out.println("Invalid command. Please type '/help' for a list of commands.\n");
                                break;
                            }
                            stub.loginRequest(command[1], client.getClientListener(), client.getClientID());
                            break;

                        case "/openChat":
                            //The Chat is opened in the client side

                            break;

                        case "/closeChat":
                            //The Chat is closed in the client side
                            break;

                        default:
                            System.out.println("Invalid command. Please type '/help' for a list of commands.\n");
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

}
