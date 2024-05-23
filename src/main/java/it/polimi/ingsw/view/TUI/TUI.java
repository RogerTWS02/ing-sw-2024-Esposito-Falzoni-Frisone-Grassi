package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.view.TUI.GameElements.Views.Board;
import it.polimi.ingsw.view.TUI.GameElements.Views.HandCards;
import it.polimi.ingsw.view.TUI.GameElements.Views.Objective;
import it.polimi.ingsw.view.TUI.GameElements.Views.TopRow;
import it.polimi.ingsw.view.TUI.GameState.InfoCard;
import it.polimi.ingsw.view.TUI.GameState.LoginUsername;
import it.polimi.ingsw.view.TUI.GameState.StartGame;
import it.polimi.ingsw.view.TUI.GameState.Views;
import org.json.simple.parser.ParseException;

import javax.swing.text.View;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static it.polimi.ingsw.network.message.MessageType.*;

public class TUI extends Thread{
    public static Client cli;
    private static List<String> cardToChooseUUID;
    private ArrayList<String> currentHandUUID;
    private List<String> allGoalsUUID;
    Scanner scanner = new Scanner(System.in);
    StartGame startGame = new StartGame();
    LoginUsername loginUsername = new LoginUsername();
    HandCards handcards = new HandCards();
    Objective goals = new Objective();
    InfoCard infoC = new InfoCard();
    Board board = new Board();
    String startingPlayer;
    List<int[]> available;
    Resource[][] onBoard;
    boolean myTurn;
    TopRow topRow = new TopRow();
    List<Integer> scores;
    List<String> nicknames;
    String currentPlayerNickame;
    List<Resource> playerResources;
    int numHand = 0, positionX = 0, positionY = 0;

    public TUI() throws IOException, ParseException {
    }

    //Faccio l'aggiornamento della tui in base ai messaggi ricevuti
    public void onMessageReceived(Message message) throws IOException, ParseException {
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

                break;

            //quando si raggiunge il numero prefissato di persone nella lobby
            case REPLY_BEGIN_GAME:

                //imposto il gameID nel client
                cli.setGameID(message.getGameID());

                //mano del giocatore
                currentHandUUID = new ArrayList<>((List<String>) message.getObj()[0]);

                //obbiettivi comuni
                allGoalsUUID = (List<String>) message.getObj()[1];

                //carte da scegliere
                cardToChooseUUID = (List<String>) message.getObj()[2];

                //booleano che controlla il turno
                myTurn = (boolean) message.getObj()[3];

                System.out.println("VALORE DEL TURNO: "+myTurn);

                //vado alla scena di gioco impostando i parametri ricevuti
                //ovvero le carte della mano e le common goal cards
                System.out.println("Let's go! The game is starting...");

                break;

            //in risposta ai comandi di /drawCardFromDeck e /drawCardFromViewable
            case REPLY_HAND_UPDATE:
                String newCardUUID = (String) message.getObj()[0];

                //DOPO AVER PESCATO NON È PIU' IL TUO TURNO
                myTurn = false;

                //aggiungo la nuova carta
                for(int i = 0; i < 3; i++){
                    if(currentHandUUID.get(i).isEmpty()){
                        currentHandUUID.add(i, newCardUUID);
                        break;
                    }
                }

                printFullScreen();

                break;

            //viene chiamato dopo che un giocatore piazza una carta
            case REPLY_UPDATED_SCORE:

                //spazi disponibili
                available = (List<int[]>) message.getObj()[0];

                //aggiorno la visualizzazione con la risorsa permanente
                onBoard[positionY][positionX] = (Resource) message.getObj()[1];

                //scores = (List<Integer>) message.getObj()[2];

                //nicknames = (List<String>) message.getObj()[3];

                //currentPlayerNickame = (String) message.getObj()[4];

                //playerResources = (List<Resource>) message.getObj()[5];

                try {
                    //con l'UUID aggiorno lo stato dello schermo della console
                    goals.showObjective(allGoalsUUID.toArray(new String[0]));
                    //handcards.showHand(currentHandUUID.toArray(new String[0]));

                    //stampo la playerBoard
                    board.drawBoard(onBoard, available);

                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
                break;

            case REPLY_INFO_CARD:
                String infoUUID = (String) message.getObj()[0];
                Boolean isFlipped = (Boolean) message.getObj()[1];

                //stampo l'info della carta
                infoC.showInfoCard(infoUUID, isFlipped);

                break;

            case REPLY_NEW_LOBBY:
                String LobbyName = (String) message.getObj()[0];
                cli.setLobbyName(LobbyName);
                System.out.print((String) message.getObj()[1]);
                //vado nello stato di richiesta nuova lobby
                break;

            case REPLY_STARTING_PLAYER:
                startingPlayer = (String) message.getObj()[0];
                break;

            case REPLY_CHOICES_MADE:

                //spazi disponibili
                available = (List<int[]>) message.getObj()[0];

                //inizializzo la visualizzazione della board
                onBoard = new Resource[80][80];

                //TODO: IMPOSTARE LA RISORSA CHE INDICA LA STARTING CARD!!!
                onBoard[40][40] = Resource.WOLF;

                break;

            case REPLY_YOUR_TURN:
                //System.out.println((String) message.getObj()[0]);
                myTurn = true;
                break;
        }
    }

    /**
     * Makes the player choose a nickname and sets it.
     *
     * @return The nickname chosen by the player.
     */
    public String insertNickname() {
        String[] command = null;
        while(cli.getLobbyName().isEmpty()) {
            do {
                loginUsername.showLogInUsername();
                command = scanner.nextLine().split(" ");
                if(command[0].length() > 16)
                    System.out.println("The nickname must be less than 16 characters!");
            } while(command[0].length() > 16);
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
        return command[0];
    }

    /**
     * Makes the player choosing the lobby size and creates it.
     *
     * @param nameP The nickname of the player who creates the lobby.
     */
    public void createNewLobby(String nameP) {
        while(cli.getLobbySize() == -1){
            int size = scanner.nextInt();
            if(size < 2 || size > 4){
                System.out.println("A game needs a number of players between 2 and 4 included!");
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
    }

    /**
     * Waits for the lobby to be full.
     */
    public void waitForFullLobby() {
        while(cli.getGameID() == -1){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Makes the player choosing how to place the starting card and places it.
     *
     * @return The side chosen by the player to place the starting card.
     */
    public boolean placeStartingCard() {
        String[] command;
        while(true){
            //Print the starting card
            infoC.showInfoCard(cardToChooseUUID.get(0),null);
            //Choose the side to place the starting card
            System.out.print("Select which side to place the starting card (type front or back to choose): ");
            command = scanner.nextLine().split(" ");
            if(command[0].equals("front") || command[0].equals("back")){
                return command[0].equals("back");
            }
            System.out.println("\nInput given '" + command[0] + "' is invalid! Try again.");
        }
    }

    /**
     * Makes the player choose the secret goal card and the starting card.
     */
    public void preliminaryActions() {
        String[] command;
        String selectedUUID;
        boolean side;
        while(true){
            //Print the two secret goal cards for choosing one of them
            infoC.showInfoCard(cardToChooseUUID.get(1),null);
            infoC.showInfoCard(cardToChooseUUID.get(2),null);
            //Choose the secret goal card
            System.out.print("Select your secret goal card (type 1 or 2 to choose): ");
            command = scanner.nextLine().split(" ");

            if(command[0].equals("1") || command[0].equals("2")){
                selectedUUID = (command[0].equals("1"))? cardToChooseUUID.get(1) : cardToChooseUUID.get(2);
                allGoalsUUID.add(selectedUUID);
                System.out.println("\nThe player has chosen the card: " + selectedUUID);
                //after choosing the secret goal card, the player chooses
                //the side to place the starting card on the board
                side = placeStartingCard();
                break;
            }
            System.out.println("\nInput given '" + command[0] + "' is invalid! Try again.");
        }
        //Message to server
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
    }

    /**
     * This method simulates the player's turn (places a card and then draws a new one).
     */

    public void playerTurn(){
        String[] command;
        //Ask for the card the player wants to play
        System.out.print("""
                       Choose the card you want to play(1, 2 or 3) \s
                       or write a different command (type /help to view the list of commands): """);
        command = scanner.nextLine().split(" ");

        //Case in which the player wants to exit the game
        if(command[0].equals("exit")){
            // Chiudo lo scanner
            scanner.close();
            return;
        }

        //decides if the player wants to play a card or use a simple command
        if(!command[0].matches("[0-9]+")){
            commonCommands(command);
            return;
        }

        int cardIndex = Integer.parseInt(command[0]);
        if (cardIndex < 1 || cardIndex > 3) {
            System.out.print("Invalid input, please insert a number between 1 and 3 :");
            return;
        }

        //Ask for the side the player wants to play the card on
        System.out.print("Choose the side you want to play the card on (front or back): ");
        String cardSide = scanner.nextLine();
        while (!cardSide.equals("front") && !cardSide.equals("back")) {
            System.out.print("Invalid input, please insert 'front' or 'back': ");
            cardSide = scanner.nextLine();
        }

        //Ask for the position where the player wants to place the card
        System.out.print("Now choose the position where you want to place the card (ex. 39 39): ");
        String[] position = scanner.nextLine().split(" ");
        while (!position[0].matches("[0-9]+") || !position[1].matches("[0-9]+")) {
            System.out.print("Invalid input, please insert a position (two numbers separated by a space): ");
            position = scanner.nextLine().split(" ");
        }

        //Here I send the request to the server to place the card
        commonCommands(new String[]{
                "/placeCard",
                String.valueOf(cardIndex),
                cardSide,
                position[0],
                position[1]
        });

        //Now it's time to draw a new card
        while (true) {

            System.out.print("""
                    Choose the type of the card you want to draw (gold or resource) \s
                    or write a different command (type /help to view the list of commands): """);
            command = scanner.nextLine().split(" ");
            String deck = command[0];

            //decides if the player wants to play a card or use a simple command
            if(!deck.equals("gold") && !deck.equals("resource")){
                commonCommands(command);
                continue;
            }

            System.out.print("If you want to draw from the deck type 'deck', else type the card number (1 or 2): ");
            String choose = scanner.nextLine();

            System.out.println("CHE CAZZO E' USCITO:"+choose);

            while (!choose.equals("deck") && !choose.equals("1") && !choose.equals("2")) {
                System.out.print("Invalid input, please type one of the following 'deck' / '1' / '2': ");
                choose = scanner.nextLine();
            }

            System.out.println("QUI ARRIVO ALLORA DOPOTUTTO");

            if (choose.equals("deck"))
                commonCommands(new String[]{
                        "/drawCardFromDeck",
                        deck
                });
            else
                commonCommands(new String[]{
                        "/drawCardFromViewable",
                        deck,
                        choose
                });
            return;
        }

    }

    public synchronized void run(){
        //Initialize scanner in order to read user input
        String[] command;
        String message;

        //Show game logo
        startGame.ShowStartGame();

        //Insert nickname
        String nameP = insertNickname();

        //Choose lobby size and create it
        createNewLobby(nameP);

        System.out.println("You just joined the lobby " + cli.getLobbyName());

        //If user is the first to join the lobby, he will be the one to start the game
        if(cli.getGameID() == -1)
            System.out.println("Waiting for other players to join the game...");

        //Wait for the lobby to be full
        waitForFullLobby();

        //Choose secret goal card and starting card
        preliminaryActions();
        System.out.println("Starting player is: " + startingPlayer);

        //REFACTOR DONE FINO A QUI     

        //TODO: AGGIORNO LO STATO DELLA TUI IN BASE ALLA SCELTA FATTA
        try {
            //print the full screen
            printFullScreen();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        //VERA FASE DI GIOCO
        while(true){

            //TODO: Usando myTurn gestire il gameFlow del giocatore

            if(myTurn){
                playerTurn();
            }else{
                //chiedo all'utente di inserire un comando comune
                System.out.print("Type '/help' to view the commands list: ");

                //Leggi il messaggio inserito dall'utente
                command = scanner.nextLine().split(" ");

                if(command[0].equals("exit")){
                    // Chiudo lo scanner
                    scanner.close();
                    break;
                }

                commonCommands(command);
            }
        }
    }

    public synchronized boolean checkFull(){
        int num = 0;
        for(String s: currentHandUUID){
            if(!s.isEmpty()) num++;
        }
        return num == 3;
    }

    public void printFullScreen() throws IOException, ParseException {
        //Views.clearScreen();

        //Still can't be used since nicknames is still unknown
        //topRow.showTopRow(currentPlayerNickame, (ArrayList<String>) nicknames, (ArrayList<Integer>) scores, (ArrayList<Resource>) playerResources);

        //stampo la playerBoard
        board.drawBoard(onBoard, available);
        goals.showObjective(allGoalsUUID.toArray(new String[0]));
        handcards.showHand(currentHandUUID.toArray(new String[0]));
    }

    /**
     * Handles common commands
     * @param command the command to be executed
     */
    public void commonCommands(String[] command){

        String message;

        switch(command[0]){
            case "/help":
                message = """
                            Commands List:              (Template: /COMMAND Param1 Param 2)\s
                            
                            /infoCard posX posY - Returns infos about a card on the player's board
                            /openChat - Opens the chat tab, where you can read and send messages to other players
                            /closeChat - Closes the chat tab and returns to the game interface
                            """;

                System.out.println(message);
                break;

            //chiedo l'UUID della carta al server e genero i dati dal JSON
            // messaggio del tipo: /infoCard posX posY
            case "/infoCard":
                if(command.length < 3) {
                    System.out.println("Command not valid, try '/help' to view syntax");
                    break;
                }
                int posX, posY;
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
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "/placeCard":

                try {
                    numHand = Integer.parseInt(command[1]);
                    positionX = Integer.parseInt(command[3]);
                    positionY = Integer.parseInt(command[4]);

                }catch(NumberFormatException e){
                    System.out.println(e);
                    break;
                }


                String sidE = command[2].toLowerCase();

                //riporto all'indice dell'array
                numHand--;

                cli.sendMessage(
                        new Message(
                                REQUEST_PLAYER_MOVE,
                                cli.getSocketPort(),
                                cli.getGameID(),
                                //Manca la carta da piazzare oltre alla posizione dove piazzarla
                                new Object[]{
                                        numHand,
                                        sidE.equals("back"),
                                        positionX,
                                        positionY
                                })
                );
                //rimuovo l'elemento dalla mano
                currentHandUUID.set(numHand, "");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "/drawCardFromViewable":
                if(checkFull()) {
                    System.out.println("Command not valid, first you need to place a card");
                    break;
                }
                if(command.length < 3) {
                    System.out.println("Command not valid, try '/help' to view syntax");
                    break;
                }
                String type = command[1].toLowerCase();
                int pos;
                try {
                    pos = Integer.parseInt(command[2]);
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
                else if(type.equals("resource")){
                    cli.sendMessage(
                            new Message(
                                    REQUEST_CARD,
                                    cli.getSocketPort(),
                                    cli.getGameID(),
                                    new Object[]{false, pos - 1})
                    );
                }else{
                    System.out.println("Command not valid, try '/help' to view syntax");
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;


            case "/drawCardFromDeck":
                if(checkFull()) {
                    System.out.println("Command not valid, you need to place a card first");
                    break;
                }
                if(command.length < 2) {
                    System.out.println("Command not valid, try '/help' to view syntax");
                    break;
                }

                String ty = command[1].toLowerCase();
                if(ty.equals("golden")){
                    cli.sendMessage(
                            new Message(
                                    REQUEST_CARD,
                                    cli.getSocketPort(),
                                    cli.getGameID(),
                                    new Object[]{true, 2})
                    );
                }else if(ty.equals("resource")){
                    cli.sendMessage(
                            new Message(
                                    REQUEST_CARD,
                                    cli.getSocketPort(),
                                    cli.getGameID(),
                                    new Object[]{false, 2})
                    );
                }else{
                    System.out.println("Command not valid, try '/help' to view syntax");
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;

            default:
                System.out.println("Command not valid, try '/help' to view syntax");
        }
    }
}
