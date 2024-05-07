package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageListener;

import java.util.Scanner;

import static it.polimi.ingsw.network.message.MessageType.*;

public class TUI implements MessageListener {
    public static Client cli;

    //Faccio l'update della tui in base ai messaggi ricevuti
    @Override
    public Message onMessageReceived(Message message) {

        return message;
    }
    private  String printTui(){
        return "tui";
    }
    public static void main(String[] args) {
        // Inizializza lo scanner per leggere da console
        Scanner scanner = new Scanner(System.in);
        String[] command;
        String message;

        while(true){
            //stampo il layout
            //System.out.println(printTui());





            //chiedo all'utente di inserire un comando
            System.out.println("Type '/help' to view the Commands List):");

            //Leggi il messaggio inserito dall'utente
            command = scanner.nextLine().split(" ");
            switch(command[0]){
                case "/help":
                    message = """
                            Commands List:\s
                            /infoCard posX posY - return info on a card on the playerBoard
                            /placeCard posX posY - try to place a card on the playerBoard
                            /drawCardFromDeck Golden/Resource - draws a card form Deck according to te specified type
                            /drawCardFromViewable Golden/Resource 1/2 - draws from the viewable cards according to the specified index
                            /TEMPLATE PARAM_1, PARAM_2
                            """;

                    cli.sendMessage(
                            new Message(
                                    TEST_MESSAGE,
                                    cli.getSocketPort(),
                                    cli.getGameID(),
                                    message)
                    );

                //chiedo l'UUID della carta al server e genero i dati dal JSON
                // messaggio del tipo: /infoCard posX posY
                case "/infoCard":
                    if(command.length < 3) break;
                    int posX = 0, posY = 0;
                    try {
                        posX = Integer.parseInt(command[1]);
                        posY = Integer.parseInt(command[2]);

                    }catch(NumberFormatException e){
                        System.out.println(e);
                        break;
                    }

                    //mando la richiesta di info
                    cli.sendMessage(
                            new Message(
                                    REQUEST_INFO_CARD,
                                    cli.getSocketPort(),
                                    cli.getGameID(),
                                    new Object[]{posX, posY})
                    );
                    break;

                case "/placeCard":
                    if(command.length < 3) break;

                    int positionX= 0, positionY = 0;
                    try {
                        positionX = Integer.parseInt(command[1]);
                        positionY = Integer.parseInt(command[2]);

                    }catch(NumberFormatException e){
                        System.out.println(e);
                        break;
                    }

                    cli.sendMessage(
                            new Message(
                                    REQUEST_CARD,
                                    cli.getSocketPort(),
                                    cli.getGameID(),
                                    new Object[]{positionX, positionY})
                    );
                    break;

                case "/drawCardFromDeck":
                    if(command.length < 2) break;
                    String type = command[1].toLowerCase();
                    int pos = 0;
                    try {
                        pos = Integer.parseInt(command[2]);
                        if(pos > 2) break;
                    }catch(NumberFormatException e){
                        System.out.println(e);
                        break;
                    }
                    if(type.equals("golden")){
                        cli.sendMessage(
                                new Message(
                                        REQUEST_DRAW_FROM_DECK,
                                        cli.getSocketPort(),
                                        cli.getGameID(),
                                        new Object[]{true, pos - 1})
                        );
                    }
                    if(type.equals("resource")){
                        cli.sendMessage(
                                new Message(
                                        REQUEST_DRAW_FROM_DECK,
                                        cli.getSocketPort(),
                                        cli.getGameID(),
                                        new Object[]{false, pos - 1})
                        );
                    }
                    break;

                case "/drawCardFromViewable":
                    if(command.length < 2) break;
                    String ty = command[1].toLowerCase();

                    if(ty.equals("golden")){
                        cli.sendMessage(
                                new Message(
                                        REQUEST_DRAW_FROM_VIEWABLE,
                                        cli.getSocketPort(),
                                        cli.getGameID(),
                                        new Object[]{true, 2})
                        );
                    }
                    if(ty.equals("resource")){
                        cli.sendMessage(
                                new Message(
                                        REQUEST_DRAW_FROM_VIEWABLE,
                                        cli.getSocketPort(),
                                        cli.getGameID(),
                                        new Object[]{false, 2})
                        );
                    }
                    break;

                default:
                    System.out.println("Command not valid (try '/help')");
            }


            if(command.equals("exit")){
                // Chiudo lo scanner
                scanner.close();
                break;
            }
        }
    }
}
