package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.model.PlayableCard;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.view.TUI.GameElements.Views.Board;
import it.polimi.ingsw.view.TUI.GameElements.Views.HandCards;
import it.polimi.ingsw.view.TUI.GameElements.Views.Objective;
import it.polimi.ingsw.view.TUI.GameElements.Views.TopRow;
import it.polimi.ingsw.view.TUI.GameState.*;
import org.json.simple.parser.ParseException;

import javax.swing.text.View;
import java.io.IOException;
import java.util.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
    Draw draw = new Draw();
    String[] gUUID = new String[3];
    String[] rUUID = new String[3];
    String startingPlayer;
    List<int[]> available;
    Resource[][] onBoard;
    volatile boolean myTurn;
    TopRow topRow = new TopRow();
    private static Map<String, Integer> nicknames;
    private static String currentPlayerNickame;
    private List<Resource> playerResources= null;
    boolean cardPlaced = false;
    private static final BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
    int numHand = 0, positionX = 0, positionY = 0;
    int turnLeft = 3;

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

            case REPLY_VIEWABLE_CARDS:
                gUUID = (String[]) message.getObj()[0];
                rUUID = (String[]) message.getObj()[1];
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

                // update list of players
                nicknames = (Map<String, Integer>) message.getObj()[4];

                // update current player
                currentPlayerNickame = (String) message.getObj()[5];



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
                        System.out.println("FINISCI QUI? "+newCardUUID);
                        currentHandUUID.set(i, newCardUUID);
                        break;
                    }
                }

                //mostro l'update fatto
                /*
                try {
                    printFullScreen();
                    System.out.print("Type '/help' to view the commands list: ");
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
                */

                break;

            //viene chiamato dopo che un giocatore piazza una carta
            case REPLY_UPDATED_SCORE:

                //RIMUOVO L'ELEMENTO DALLA MANO SOLO QUANDO SONO
                //SICURO CHE LA MOSSA SIA ANDATA A BUON FINE!!!
                currentHandUUID.set(numHand, "");

                //spazi disponibili
                available = (List<int[]>) message.getObj()[0];

                //aggiorno la visualizzazione con la risorsa permanente
                onBoard[positionY][positionX] = (Resource) message.getObj()[1];
                cardPlaced = true;

                String nick = (String) message.getObj()[2];
                int score = (int) message.getObj()[3];

                nicknames.put(nick, score);

                playerResources = (List<Resource>) message.getObj()[4];


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
               // resourceCardViawable = (String[]) message.getObj()[1];
               // goldenCardViawable = (String[]) message.getObj()[2];
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
                myTurn = true;
                currentPlayerNickame = (String) message.getObj()[0];
                break;

            case NOTIFY_END_GAME:
                System.out.println("Turns left: "+message.getObj()[0]);

                //update number of remaining turns
                turnLeft = (Integer) message.getObj()[0];
                break;

            case REPLY_INTERRUPT_GAME:
                System.out.println((String) message.getObj()[0]);
                cli.closeSocket();
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
    public synchronized void waitForFullLobby(){
        String[] command;
        while(cli.getGameID() == -1){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if(cli.getGameID() != -1) continue;

            command = getCommandFromQueue();
            if(command[0].equals("/quitGame")) commonCommands(command);
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
            //This nextLine is needed to flush out the previous "\n" (IT'S A JAVA PROBLEM)
            command = getCommandFromQueue();
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
        int num;
        String selectedUUID;
        boolean side;
        while(true){
            //Print the two secret goal cards for choosing one of them
            infoC.showInfoCard(cardToChooseUUID.get(1),null);
            infoC.showInfoCard(cardToChooseUUID.get(2),null);
            //Choose the secret goal card
            System.out.print("Select your secret goal card (type 1 or 2 to choose): ");
            try{
                num = Integer.parseInt(getCommandFromQueue()[0]);
            }catch (NumberFormatException e){
                System.out.println("\nInput given is invalid! Try again.");
                continue;
            }

            if(num == 1 || num == 2){
                selectedUUID = cardToChooseUUID.get(num);
                allGoalsUUID.add(selectedUUID);
                System.out.println("\nThe player has chosen the card: " + selectedUUID);
                //after choosing the secret goal card, the player chooses
                //the side to place the starting card on the board
                side = placeStartingCard();
                break;
            }
            System.out.println("\nInput given '" + num +"' is invalid! Try again.");
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
     * This method checks if the player input position is correct or no.
     */
    private static boolean isValidPosition(String[] position) {

        if (position.length < 2) {
            return false;
        }

        if (!position[0].matches("[0-9]+") || !position[1].matches("[0-9]+")) {
            return false;
        }

        return true;
    }

    /**
     * This method simulates the player's turn (places a card and then draws a new one).
     */

    public void playerTurn(){
        try {
            printFullScreen();

            //update the current viewable cards
            cli.sendMessage(
                    new Message(
                            REQUEST_VIEWABLE_CARDS,
                            cli.getSocketPort(),
                            cli.getGameID())
            );

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        while(true) {
            String[] command;
            //Ask for the card the player wants to play
            System.out.println("\nChoose the card you want to play(1, 2 or 3)");
            System.out.print("or write a different command (type /help to view the list of commands): ");
            command = getCommandFromQueue();

            //Case in which the player wants to exit the game
            if (command[0].equals("exit")) {
                // Chiudo lo scanner
                scanner.close();
                return;
            }

            //decides if the player wants to play a card or use a simple command
            if (!command[0].matches("[0-9]+")) {
                commonCommands(command);
                continue;
            }

            int cardIndex = Integer.parseInt(command[0]);
            if (cardIndex < 1 || cardIndex > 3) {
                System.out.print("Invalid input, please insert a number between 1 and 3: ");
                continue;
            }

            //Ask for the side the player wants to play the card on
            System.out.print("Choose the side you want to play the card on (front or back): ");
            String cardSide = getCommandFromQueue()[0];
            while (!cardSide.equals("front") && !cardSide.equals("back")) {
                System.out.print("Invalid input, please insert 'front' or 'back': ");
                cardSide = getCommandFromQueue()[0];
            }

            //Ask for the position where the player wants to place the card
            System.out.print("Now choose the position where you want to place the card (ex. 39 39): ");
            String[] position = getCommandFromQueue();
            while (!isValidPosition(position)){
                System.out.print("Invalid input, please insert a position (two numbers separated by a space): ");
                position = getCommandFromQueue();
            }

            //Here I send the request to the server to place the card
            commonCommands(new String[]{
                    "/placeCard",
                    String.valueOf(cardIndex),
                    cardSide,
                    position[0],
                    position[1]
            });

            //If the card has not been placed
            if (!cardPlaced) continue;
            cardPlaced = false; // set it back to false for next time

            //Now it's time to draw a new card
            while (true) {
                draw.showDrawable(gUUID, rUUID);
                System.out.print("""
                        Choose the type of the card you want to draw (golden or resource) \s
                        or write a different command (type /help to view the list of commands): """);
                command = getCommandFromQueue();
                String deck = command[0];

                //decides if the player wants to play a card or use a simple command
                if (!deck.equals("golden") && !deck.equals("resource")) {
                    commonCommands(command);
                    continue;
                }

                System.out.print("If you want to draw from the deck type 'deck', else type the card number (1 or 2): ");
                String choose = getCommandFromQueue()[0];


                while (!choose.equals("deck") && !choose.equals("1") && !choose.equals("2")) {
                    System.out.print("Invalid input, please type one of the following 'deck' / '1' / '2': ");
                    choose = getCommandFromQueue()[0];
                }

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

                try {
                    printFullScreen();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

    }

    public void run(){
        //Initialize scanner in order to read user input
        String[] command;
        //Show game logo
        startGame.ShowStartGame();

        //Insert nickname
        String nameP = insertNickname();

        //Choose lobby size and create it
        createNewLobby(nameP);

        System.out.println("You just joined the lobby " + cli.getLobbyName());

        /*
        This thread reads the input from the user and puts it in the inputQueue, so that the main process doesn't have to wait for the input
         */
        Thread inputThread = new Thread(() -> {
            while (true) {
                try{
                    if(scanner.hasNextLine()){
                        String input = scanner.nextLine();
                        inputQueue.put(input);
                    }
                } catch (Exception e) {
                    System.out.println("Error in reading input: " + e);
                }
            }
        });

        inputThread.setDaemon(true);
        inputThread.start();

        //If user is the first to join the lobby, he will be the one to start the game
        if(cli.getGameID() == -1) {
            System.out.println("Waiting for other players to join the game...");
            System.out.print("If you want to leave the lobby type '/quitGame': ");
        }

        //Wait for the lobby to be full
        waitForFullLobby();

        //Choose secret goal card and starting card
        preliminaryActions();
        System.out.println("Starting player is: " + startingPlayer);

        //TODO: code refactoring

        boolean first = true;
        //Game flow implementation
        while(turnLeft > 0){

            //TODO: Usando myTurn gestire il gameFlow del giocatore
            if(myTurn){
                if(turnLeft != 3) turnLeft--;
                playerTurn();
            }else{
                //TODO: AGGIORNO LO STATO DELLA TUI IN BASE ALLA SCELTA FATTA
                if(first) {
                    first = false;
                    try {
                        //print the full screen
                        printFullScreen();
                    } catch (IOException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                }

                //chiedo all'utente di inserire un comando comune
                System.out.print("Type '/help' to view the commands list: ");
                while(!myTurn && inputQueue.isEmpty()){
                    Thread.onSpinWait();
                }

                if(myTurn){
                    continue;
                }

                /*
                Here we read the message from the player, but it doesn't block the main thread
                because we use a storing queue to store the input
                 */
                command = getCommandFromQueue();
                commonCommands(command);

                try {
                    Thread.sleep(100); // Pausa breve per evitare il busy-waiting
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }
        }

        //facciamo vedere la schermata di fine gioco

        //TODO: SCHERMATA DI FINE GIOCO CON NOME GIOCATORE VINCENTE E PUNTEGGIO DI TUTTI
        //TIPO CLASSIFICA IN ORDINE DECRESCENTE
        System.out.println("IL GIOCO E' FINITO");
    }

    /**
     * Checks if the player's hand is full.
     *
     * @return true if the hand is full, false otherwise.
     */

    public synchronized boolean checkFull(){
        int num = 0;
        for(String s: currentHandUUID){
            if(!s.isEmpty()) num++;
        }
        return num == 3;
    }

    /**
     * Prints the full screen of the game.
     */

    public void printFullScreen() throws IOException, ParseException {
        Views.clearScreen();

        //Still can't be used since nicknames is still unknown
        topRow.showTopRow(currentPlayerNickame, nicknames, (ArrayList<Resource>) playerResources);
        //Print player's board
        int lines=board.drawBoard(onBoard, available)*3;
        if(lines<20){System.out.println("\n".repeat(20-lines));}
        goals.showObjective(allGoalsUUID.toArray(new String[0]));
        handcards.showHand(currentHandUUID.toArray(new String[0]));
    }

    /**
     * Handles the /help command.
     */
    public void commonCommands(String[] command){

        String message;

        switch(command[0].toLowerCase()){
            case "/help":
                message = """
                            Commands List:              (Template: /COMMAND Param1 Param 2)\s
                            
                            /infoCard posX posY - Returns infos about a card on the player's board
                            /showCommonCards - Shows the common resource and golden cards
                            /openChat - Opens the chat tab, where you can read and send messages to other players
                            /closeChat - Closes the chat tab and returns to the game interface
                            /quitGame - Quits the current session and ends the game for all the other players
                            
                            Those commands are not case-sensitive
                            """;

                System.out.println(message);
                break;

            case "/quitgame":
                cli.sendMessage(
                        new Message(
                                REQUEST_INTERRUPT_GAME,
                                cli.getSocketPort(),
                                cli.getGameID()
                        )
                );
                return;

            //chiedo l'UUID della carta al server e genero i dati dal JSON
            // messaggio del tipo: /infoCard posX posY
            case "/infocard":
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

            case "/showcommoncards":
                cli.sendMessage(
                        new Message(
                                REQUEST_VIEWABLE_CARDS,
                                cli.getSocketPort(),
                                cli.getGameID())
                );
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                draw.showDrawable(gUUID, rUUID);
                break;

            case "/placecard":

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

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "/drawcardcromviewable":

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
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;


            case "/drawcardfromdeck":
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
    /**
     * Gets the command from the queue, if the queue is empty it waits for a new command
     * @return the command
     */

    private static String[] getCommandFromQueue() {
        String command = null;
        try {
            command = inputQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return command.split(" ");
    }
}
