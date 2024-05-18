package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.view.TUI.GameElements.Views.BottomRow;
import it.polimi.ingsw.view.TUI.GameState.InfoCard;
import it.polimi.ingsw.view.TUI.GameState.LoginUsername;
import it.polimi.ingsw.view.TUI.GameState.StartGame;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static it.polimi.ingsw.network.message.MessageType.*;

public class TUI extends Thread{
    public static Client cli;
    private static List<String> cardToChooseUUID;
    private static List<String> currentHandUUID;
    private List<String> allGoalsUUID;
    Scanner scanner = new Scanner(System.in);
    StartGame startGame = new StartGame();
    LoginUsername loginUsername = new LoginUsername();
    BottomRow bottomRow = new BottomRow();
    InfoCard infoC = new InfoCard();

    public TUI() throws IOException, ParseException {
    }

    //Faccio l'aggiornamento della tui in base ai messaggi ricevuti
    public void onMessageReceived(Message message) {
        //System.out.println(message.getMessageType() + " sent by " + message.getSenderID());
        String srvRep;
        switch (message.getMessageType()) {
            case REPLY_BAD_REQUEST:
                srvRep = (String) message.getObj()[0];
                System.out.println("Bad request: " + srvRep);
                break;

            case REPLY_LOBBY_INFO:
                cli.setLobbyName((String) message.getObj()[0]);
                cli.setLobbySize((Integer) message.getObj()[1]);
                /*if(message.getObj().length == 3){
                    srvRep = (String) message.getObj()[2];
                    System.out.println(srvRep);
                }*/
                break;

            //quando si raggiunge il numero prefissato di persone nella lobby
            case REPLY_BEGIN_GAME:
                //PER DEBUGGING STAMPO TUTTO
                /*
                System.out.println("Player's hand: "+message.getObj()[0].toString());
                System.out.println("Player's common goals: "+message.getObj()[1].toString());
                System.out.println("Player's cards to choose: "+message.getObj()[2].toString());
                */

                //imposto il gameID nel client
                cli.setGameID(message.getGameID());

                //mano del giocatore
                currentHandUUID = (List<String>) message.getObj()[0];

                //obbiettivi comuni
                allGoalsUUID = (List<String>) message.getObj()[1];

                //carte da scegliere
                cardToChooseUUID = (List<String>) message.getObj()[2];

                //vado alla scena di gioco impostando i parametri ricevuti
                //ovvero le carte della mano e le common goal cards
                System.out.println("Gioco iniziato! Stampo mano, goal comuni e quello privato, mazzi, board e punteggio ecc...");

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

            case REPLY_NEW_LOBBY:
                String LobbyName = (String) message.getObj()[0];
                cli.setLobbyName(LobbyName);
                System.out.print((String) message.getObj()[1]);
                //vado nello stato di richiesta nuova lobby
        }
    }

    public synchronized void run(){
        // Inizializza lo scanner per leggere da console
        String[] command = null;
        String message;

        while(cli.getLobbyName().isEmpty()) {

            //faccio vedere il logo
            startGame.ShowStartGame();

            //inizialmente mando i messaggi per far avviare il gioco
            //System.out.print("Insert a valid Nickname to start a game:");
            loginUsername.showLogInUsername();

            //metodo bloccante che aspetta l'ingresso dell'utente
            command = scanner.nextLine().split(" ");
            cli.sendMessage(
                    new Message(
                            REQUEST_LOGIN,
                            cli.getSocketPort(),
                            -1, //il gameId non viene settato fino all'avvio vero e proprio della partita
                            command[0])
            );
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        String nameP = command[0];
        //nel caso di una lobby nuova devo inserire il numero di giocatori
        while(cli.getLobbySize() == -1){
            int size = scanner.nextInt();
            if(size < 2 || size > 4){
                System.out.println("A game needs a number of players between 2 and 4 included!!!");
                System.out.print("Insert lobby size (4 players max): ");
                continue;
            }
            cli.sendMessage(
                    new Message(
                            REQUEST_NEW_LOBBY,
                            cli.getSocketPort(),
                            -1, //il gameId non viene settato fino all'avvio vero e proprio della partita
                            new Object[]{
                                    nameP,
                                    cli.getLobbyName(),
                                    size
                            })
            );
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("You just joined the lobby: "+cli.getLobbyName());

        //se l'utente manda messaggi in fase di attesa non faccio nulla
        if(cli.getGameID() == -1) System.out.println("Waiting for other players to start the game...");
        while(cli.getGameID() == -1){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        String selectedUUID = "";
        boolean side;
        boolean fistTime = true;


        while(true){
            //sezione per scegliere la secret goal card
            System.out.print("Select your secret goal card between the two (type 1 or 2 to choose):");
            command = scanner.nextLine().split(" ");

            //In the first player the buffer remains empty, so I have to skip the first time
            if(Objects.equals(command[0], "") && fistTime){
                fistTime = false;
                command = scanner.nextLine().split(" ");
            }

            if(command[0].equals("1") || command[0].equals("2")){
                selectedUUID = (command[0].equals("1"))? cardToChooseUUID.get(1) : cardToChooseUUID.get(2);
                allGoalsUUID.add(selectedUUID);
                System.out.println("\nIl giocatore ha scelto la carta: "+selectedUUID);

                while(true){
                    //sezione per scegliere che lato mettere la starting card
                    System.out.print("Select which side to place the starting card (type 1 for frontside or 2 for backside):");
                    command = scanner.nextLine().split(" ");
                    if(command[0].equals("1") || command[0].equals("2")){
                        side = command[0].equals("2");
                        break;
                    }
                    System.out.println("\nInput given '"+command[0]+"' is incorrect! Try again.");
                }

                break;
            }
            System.out.println("\nInput given '"+command[0]+"' is incorrect! Try again.");
        }

        //notifico il server per la scelta fatta
        cli.sendMessage(
                new Message(
                        NOTIFY_CHOICES_MADE,
                        cli.getSocketPort(),
                        cli.getGameID(),
                        new Object[]{
                                cardToChooseUUID.get(0), //UUID della starting card
                                side,                    //lato scelto
                                selectedUUID             //secret goal card scelta
                        }
                )
        );
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //stampo la mano e gli obbiettivi comuni
        try {
            bottomRow.showBottomRow(currentHandUUID.toArray(new String[0]), allGoalsUUID.toArray(new String[0]));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        //TODO: AGGIORNO LO STATO DELLA TUI IN BASE ALLA SCELTA FATTA

        //vera fase di gioco
        while(true){
            //stampo il layout
            //System.out.println(printTui());

            //chiedo all'utente di inserire un comando
            System.out.print("Type '/help' to view the Commands List):");

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
                                    //Manca la carta da piazzare oltre alla posizione dove piazzarla
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
                    System.out.println("Command not valid, try '/help'");
            }


            if(command[0].equals("exit")){
                // Chiudo lo scanner
                scanner.close();
                break;
            }
        }
    }
}
