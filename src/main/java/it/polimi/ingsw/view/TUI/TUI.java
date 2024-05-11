package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.model.GoalCard;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageListener;
import it.polimi.ingsw.view.TUI.GameState.InfoCard;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Scanner;

import static it.polimi.ingsw.network.message.MessageType.*;

public class TUI implements MessageListener {
    public static Client cli;
    private static String[] gcs;
    InfoCard infoC = new InfoCard();

    public TUI() throws IOException, ParseException {
    }


    //Faccio l'aggiornamento della tui in base ai messaggi ricevuti
    @Override
    public Message onMessageReceived(Message message) {
        //System.out.println(message.getMessageType() + " sent by " + message.getSenderID());
        String srvRep;
        switch(message.getMessageType()){
            case REPLY_BAD_REQUEST:
                srvRep = (String) message.getObj()[0];
                System.out.println("Bad request: "+srvRep);
                break;

            case REPLY_LOBBY_NAME:
                srvRep = (String) message.getObj()[0];
                cli.setLobbyName(srvRep);
                System.out.println("You joined the lobby "+srvRep+"!");
                break;

            //quando si raggiunge il numero prefissato di persone nella lobby
            case REPLY_BEGIN_GAME:
                //imposto il gameID nel client
                cli.setGameID(message.getGameID());

                //vado alla scena di gioco impostando i parametri ricevuti
                //ovvero le carte della mano e le common goal cards
                System.out.println("Gioco iniziato! Stampo mano, goal comuni e quello privato, mazzi, board e punteggio ecc...");

                break;

            //quando ricevo la risposta della scelta delle secret goal card
            case REPLY_SECRET_GC:
                gcs = (String[]) message.getObj();
                break;

            //in risposta ai comandi di /drawCardFromDeck e /drawCardFromViewable
            case REPLY_HAND_UPDATE:
                String newCardUUID = (String) message.getObj()[0];

                //con l'UUID aggiorno lo stato dello schermo della console

                break;

            case REPLY_UPDATED_SCORE:
                int newScore = (Integer) message.getObj()[0];

                //aggiorno l'interfaccia tui che gestisce il punteggio
                break;

            case REPLY_INFO_CARD:
                String infoUUID = (String) message.getObj()[0];

                //stampo l'info della carta
                System.out.print(infoC.showInfoCard(infoUUID));

                break;
        }
        return message;
    }

    public static void main(String[] args) {
        System.out.println("String di debug: "+args[0]);
        // Inizializza lo scanner per leggere da console
        Scanner scanner = new Scanner(System.in);
        String[] command;
        String message;


        //inizialmente mando i messaggi per far avviare il gioco
        while(cli.getLobbyName().isEmpty()){
            System.out.print("Insert a valid Nickname to start a game:");
            //metodo bloccante che aspetta l'ingresso dell'utente
            command = scanner.nextLine().split(" ");
            cli.sendMessage(
                    new Message(
                            REQUEST_LOGIN,
                            cli.getSocketPort(),
                            -1, //il gameId non viene settato fino all'avvio vero e proprio della partita
                            new Object[]{command}
                    )
            );
        }

        //se l'utente manda messaggi in fase di attesa non faccio nulla
        while(cli.getGameID() == -1){
            scanner.nextLine();
            System.out.println("Waiting for other players to start the game...");
        }

        //richiedo all'utente di scegliere le secretGoalCards
        cli.sendMessage(
                new Message(
                        REQUEST_SECRET_GC,
                        cli.getSocketPort(),
                        -1, //il gameId non viene settato fino all'avvio vero e proprio della partita
                        ""
                )
        );
        //adesso l'array di string gcs è inizializzato con dei valori validi!!!


        boolean valid = false;
        while(true){
            System.out.print("Select your secret goal card between the two (type 1 or 2 to choose):");
            command = scanner.nextLine().split(" ");
            if(command[0].equals("1") || command[0].equals("2")){
                String selectedUUID = (command[0].equals("1"))? gcs[0] : gcs[1];

                //TODO: AGGIORNO LO STATO DELLA TUI IN BASE ALLA SCELTA FATTA

                System.out.println("Il giocatore ha scelto la carta: "+selectedUUID);
                valid = true;

                //notifico il server per la scelta fatta
                cli.sendMessage(
                        new Message(
                                NOTIFY_SECRET_GC,
                                cli.getSocketPort(),
                                cli.getGameID(),
                                new Object[]{selectedUUID}
                        )
                );
            }
            if(valid) break;
            System.out.print("Input given is incorrect! Try again.");
        }

        //vera fase di gioco
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
                            /openChat - opens the chat tab where you can type messages to the lobby and watch the messages from others players
                            /closeChat - closes the chat tab and resume the game interface
                            /TEMPLATE PARAM_1, PARAM_2
                            """;

                    System.out.println(message);
                    break;

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
                                    REQUEST_PLAYER_MOVE,
                                    cli.getSocketPort(),
                                    cli.getGameID(),
                                    new Object[]{positionX, positionY})
                    );
                    break;

                case "/drawCardFromViewable":
                    if(command.length < 2) break;
                    String type = command[0].toLowerCase();
                    int pos = 0;
                    try {
                        pos = Integer.parseInt(command[1]);
                        if(pos > 2 || pos < 1) break;
                    }catch(NumberFormatException e){
                        System.out.println(e);
                        break;
                    }
                    if(type.equals("golden")){
                        cli.sendMessage(
                                new Message(
                                        REQUEST_CARD,
                                        cli.getSocketPort(),
                                        cli.getGameID(),
                                        new Object[]{true, pos - 1})
                        );
                    }
                    if(type.equals("resource")){
                        cli.sendMessage(
                                new Message(
                                        REQUEST_CARD,
                                        cli.getSocketPort(),
                                        cli.getGameID(),
                                        new Object[]{false, pos - 1})
                        );
                    }
                    break;

                case "/drawCardFromDeck":
                    String ty = command[0].toLowerCase();

                    if(ty.equals("golden")){
                        cli.sendMessage(
                                new Message(
                                        REQUEST_CARD,
                                        cli.getSocketPort(),
                                        cli.getGameID(),
                                        new Object[]{true, 2})
                        );
                    }
                    if(ty.equals("resource")){
                        cli.sendMessage(
                                new Message(
                                        REQUEST_CARD,
                                        cli.getSocketPort(),
                                        cli.getGameID(),
                                        new Object[]{false, 2})
                        );
                    }
                    break;

                default:
                    System.out.println("Command not valid (try '/help')");
            }


            if(command[0].equals("exit")){
                // Chiudo lo scanner
                scanner.close();
                break;
            }
        }
    }
}
