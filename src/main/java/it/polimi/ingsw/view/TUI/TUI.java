package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.view.TUI.GameElements.Board;
import it.polimi.ingsw.view.TUI.GameElements.HandCards;
import it.polimi.ingsw.view.TUI.GameElements.Objective;
import it.polimi.ingsw.view.TUI.GameElements.TopRow;
import it.polimi.ingsw.view.TUI.GameState.*;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import static it.polimi.ingsw.network.message.MessageType.*;

public class TUI extends Thread{
    public static Client cli;
    private String nameP;
    private static List<String> cardToChooseUUID;
    private ArrayList<String> currentHandUUID;
    private List<String> allGoalsUUID;
    private Scanner scanner = new Scanner(System.in);
    private StartGame startGame = new StartGame();
    private LoginUsername loginUsername = new LoginUsername();
    private Chat chat = new Chat();
    private HandCards handcards = new HandCards();
    private Objective goals = new Objective();
    private InfoCard infoC = new InfoCard();
    private Board board = new Board();
    private Draw draw = new Draw();
    private String[] gUUID = new String[3];
    private String[] rUUID = new String[3];
    private String startingPlayer;
    private List<int[]> available;
    private Resource[][] onBoard;
    private volatile boolean myTurn;
    private volatile boolean preliminaryChoicesMade = false;
    private TopRow topRow = new TopRow();
    private Endgame endgame = new Endgame();
    private static Map<String, Integer> nicknames;
    private static String currentPlayerNickname;
    private List<Resource> playerResources= null;
    private boolean cardPlaced = false, updateChat = false, successfulDraw = true;
    private static final BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
    private int numHand = 0, positionX = 0, positionY = 0;
    private int turnLeft = 3;
    private Queue<String> chatMessages = new LinkedList<>();
    private volatile static boolean gameover = false;
    private ArrayList<String> winners;
    private volatile Boolean areThereAvailableLobbies = null;
    private List<String> availableLobbies = new ArrayList<>();
    private boolean alreadyTriedToChooseLobby = false;

    public TUI() throws IOException, ParseException {
    }

    /**
     * Handles arriving message from the server and updates the TUI.
     *
     * @param message The message received.
     * @throws IOException If an I/O error occurs.
     * @throws ParseException If an error occurs during parsing.
     */
    public void onMessageReceived(Message message) throws IOException, ParseException {
        String srvRep;
        switch (message.getMessageType()) {
            case REPLY_CHAT_MESSAGE:
                chatMessages.add((String) message.getObj()[0]);

                if(chatMessages.size() > 10){
                    chatMessages.poll();
                }

                updateChat = true;
                break;

            case REPLY_BAD_REQUEST:
                srvRep = (String) message.getObj()[0];
                if(srvRep.equals("The chosen lobby is full! Creating a new one..."))
                    alreadyTriedToChooseLobby = true;
                System.out.println("Bad request: " + srvRep);
                break;

            case REPLY_LOBBY_INFO:
                cli.setLobbyName((String) message.getObj()[0]);
                cli.setLobbySize((Integer) message.getObj()[1]);
                break;

            case REPLY_VIEWABLE_CARDS:
                rUUID = (String[]) message.getObj()[0];
                gUUID = (String[]) message.getObj()[1];
                break;

            //When the lobby is full
            case REPLY_BEGIN_GAME:
                //Set the gameID in the client
                cli.setGameID(message.getGameID());

                //Hand of the player
                currentHandUUID = new ArrayList<>((List<String>) message.getObj()[0]);

                //Common goal cards
                allGoalsUUID = (List<String>) message.getObj()[1];

                //Cards to choose
                cardToChooseUUID = (List<String>) message.getObj()[2];

                //Turn checking boolean
                myTurn = (boolean) message.getObj()[3];

                // update list of players
                nicknames = (Map<String, Integer>) message.getObj()[4];

                // update current player
                currentPlayerNickname = (String) message.getObj()[5];
                startingPlayer = (String) message.getObj()[5];

                // update player resources
                playerResources = (List<Resource>) message.getObj()[6];

                System.out.println("\nLet's go! The game is starting...");
                break;

            //Replying to the commands /drawCardFromDeck and /drawCardFromViewable
            case REPLY_HAND_UPDATE:
                String newCardUUID = (String) message.getObj()[0];
                //Adds the new card to the hand
                for(int i = 0; i < 3; i++){
                    if(currentHandUUID.get(i).isEmpty()){
                        currentHandUUID.set(i, newCardUUID);
                        break;
                    }
                }
                break;

            //After a player places a card
            case REPLY_UPDATED_SCORE:
                currentHandUUID.set(numHand, "");
                //Available places
                available = (List<int[]>) message.getObj()[0];

                //Update the board with the placed card
                onBoard[positionY][positionX] = (Resource) message.getObj()[1];
                cardPlaced = true;
                String nick = (String) message.getObj()[2];
                int score = (int) message.getObj()[3];
                nicknames.put(nick, score);
                playerResources = (List<Resource>) message.getObj()[4];

                try {

                    topRow.showTopRow(currentPlayerNickname, nicknames, (ArrayList<Resource>) playerResources);
                    //Refresh the screen
                    goals.showObjective(allGoalsUUID.toArray(new String[0]));
                    //Prints the player's board
                    board.drawBoard(onBoard, available);
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
                break;

            case REPLY_INFO_CARD:
                String infoUUID = (String) message.getObj()[0];
                Boolean isFlipped = (Boolean) message.getObj()[1];
                Boolean[] coveredCorners = (Boolean[]) message.getObj()[2];
                infoC.showInfoCard(infoUUID, isFlipped, coveredCorners);
                break;

            case REPLY_NEW_LOBBY:
                String LobbyName = (String) message.getObj()[0];
                cli.setLobbyName(LobbyName);
                System.out.print((String) message.getObj()[1]);
                break;

            case REPLY_CHOICES_MADE:
                //View free spaces
                available = (List<int[]>) message.getObj()[0];
                onBoard = new Resource[80][80];
                onBoard[40][40] = Resource.WOLF;
                break;

            case REPLY_YOUR_TURN:
                currentPlayerNickname = (String) message.getObj()[0];
                printFullScreen();
                myTurn = (boolean) message.getObj()[1]; //Update turn
                break;

            case NOTIFY_END_GAME:
                System.out.println("Turns left: "+message.getObj()[0]);
                //Update number of remaining turns
                turnLeft = (Integer) message.getObj()[0];
                break;

            case REPLY_INTERRUPT_GAME:
                System.out.println((String) message.getObj()[0]);
                //close the connection client-side according to the client network protocol
                cli.closeConnection();
                break;

            case REPLY_POINTS_UPDATE:
                String name = (String) message.getObj()[0];
                int points = (int) message.getObj()[1];
                nicknames.put(name, points);
                break;

            case REPLY_LAST_TURN:
                gameover = true;
                break;

            case REPLY_EMPTY_DECK:
                successfulDraw = false;
                break;

            case REPLY_END_GAME:
                winners = (ArrayList<String>) message.getObj()[0];
                break;

            case NOTIFY_GAME_STARTING:
                preliminaryChoicesMade = true;
                break;

            case HEARTBEAT:
                replyHeartbeat();
                break;

            case REPLY_AVAILABLE_LOBBIES:
                handleReplyAvailableLobbies(message);
                break;

            case REQUEST_PLAYER_BOARD_INFOS:
                requestPlayerBoardInfosHandler(message);
                break;

            case REPLY_PLAYER_BOARD:
                replyPlayerBoardHandler(message);
                break;

            case REPLY_PLAYER_CARD:
                replyPlayerCardHandler(message);
                break;
        }
    }

    /**
     * Handles the reply about a certain card's infos of a certain player.
     *
     * @param message The message received.
     */
    public void replyPlayerCardHandler(Message message) {
        String infoUUID = (String) message.getObj()[0];
        Boolean isFlipped = (Boolean) message.getObj()[1];
        Boolean[] coveredCorners = (Boolean[]) message.getObj()[2];
        System.out.println("\n" + message.getObj()[3] + "'s card in requested position is: \n");
        infoC.showInfoCard(infoUUID, isFlipped, coveredCorners);
        System.out.println("\n");
    }

    /**
     * Handles the receiving of infos about a player's player board.
     *
     * @param message The message received.
     */
    public void replyPlayerBoardHandler(Message message) {
        System.out.println("\n" + message.getObj()[0] + "'s board: \n");
        Board tempBoard = new Board();
        tempBoard.drawBoard((Resource[][]) message.getObj()[1], (List<int[]>) message.getObj()[2]);
        System.out.println("\n");
    }

    /**
     * Handles the request for the player board infos.
     *
     * @param message The message received.
     */
    public void requestPlayerBoardInfosHandler(Message message) {
        cli.sendMessage(
                new Message(
                        REPLY_PLAYER_BOARD_INFOS,
                        cli.getClientID(),
                        message.getGameID(),
                        new Object[]{message.getObj()[0], onBoard, available, nameP})
        );
    }

    /**
     * Handles the reply containing the available lobbies.
     *
     * @param message The message received.
     */
    public void handleReplyAvailableLobbies(Message message) {
        String[] availableLobbies = (String[]) message.getObj()[0];
        if(availableLobbies.length == 0){
            System.out.println("\nNo lobbies available at the moment. Creating a new one...\n");
            areThereAvailableLobbies = false;
            return;
        }
        areThereAvailableLobbies = true;
        this.availableLobbies = new ArrayList<>(Arrays.asList(availableLobbies));
    }

    /**
     * Makes the player choose a nickname and sets it.
     *
     * @return The nickname chosen by the player.
     */
    public String insertNickname(String lobby) {
        String[] command = null;
        while(cli.getLobbyName().isEmpty()) {
            if(alreadyTriedToChooseLobby) {
                cli.sendMessage(
                        new Message(
                                REQUEST_LOGIN,
                                cli.getClientID(),
                                -1, //gameID is not set until the game actually starts
                                new Object[]{command[0], "create"})
                );
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return command[0];
            }
            do {
                loginUsername.showLogInUsername();
                command = getCommandFromQueue();
                if(command[0].length() > 16)
                    System.out.println("The nickname must be less than 16 characters!");
                if(command.length > 1 || command[0].isEmpty())
                    System.out.println("The nickname must be a single word!");
            } while(command[0].length() > 16 || command.length != 1 || command[0].isEmpty());
            cli.sendMessage(
                    new Message(
                            REQUEST_LOGIN,
                            cli.getClientID(),
                            -1, //gameID is not set until the game actually starts
                            new Object[]{command[0], lobby})
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
        int size;
        while(cli.getLobbySize() == -1){

            try{
                 size = Integer.parseInt(getCommandFromQueue()[0]);
            }catch (NumberFormatException e){
                System.out.println("Invalid input, please insert a number between 2 and 4");
                System.out.print("Insert lobby size (4 players max): ");
                continue;
            }

            if(size < 2 || size > 4){
                System.out.println("A game needs a number of players between 2 and 4 included!");
                System.out.print("Insert lobby size (4 players max): ");
                continue;
            }
            cli.sendMessage(
                    new Message(
                            REQUEST_NEW_LOBBY,
                            cli.getClientID(),
                            -1, //gameID is not set until the game actually starts
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
    public void waitForFullLobby(){
        String[] command;
        while(cli.getGameID() == -1){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if(cli.getGameID() != -1) continue;

            if(!inputQueue.isEmpty()){
                command = getCommandFromQueue();
                if(command[0].equals("/quitGame") || command[0].equals("/quitgame")) {
                    commonCommands(command);
                }else{
                    System.out.print("Wait for the lobby to fill up or type '/quitGame' to leave the lobby: ");
                }
            }
        }
    }

    /**
     * This method waits for all the players to make the preliminary actions,
     * if a player wants to quit the game, he can type '/quitGame'.
     */

    public void waitForPreliminaryActions(){
        String[] command;
        while(true){
            System.out.println("Waiting for all players to make their preliminary choices...\n");
            System.out.print("If you want to leave the game type '/quitGame': ");
            while(!preliminaryChoicesMade && inputQueue.isEmpty()) {
                Thread.onSpinWait();
            }

            if(preliminaryChoicesMade){
                break;
            }else{
                command = getCommandFromQueue();
                commonCommands(command);
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
            infoC.showInfoCard(cardToChooseUUID.get(0),null, null);
            //Choose the side to place the starting card
            System.out.print("Select which side to place the starting card (type front or back to choose): ");
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
            infoC.showInfoCard(cardToChooseUUID.get(1),null, null);
            infoC.showInfoCard(cardToChooseUUID.get(2),null, null);
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
                        cli.getClientID(),
                        cli.getGameID(),
                        new Object[]{
                                cardToChooseUUID.get(0), //Starting card uuid
                                side,                    //chosen side
                                selectedUUID             //secret goal card chosen
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
        //update the current viewable cards
        cli.sendMessage(
                new Message(
                        REQUEST_VIEWABLE_CARDS,
                        cli.getClientID(),
                        cli.getGameID())
        );

        while(true) {
            String[] command;
            //Ask for the card the player wants to play
            System.out.println("\nChoose the card you want to play(1, 2 or 3)");
            System.out.print("or write a different command (type /help to view the list of commands): ");
            command = getCommandFromQueue();

            //decides if the player wants to play a card or use a simple command
            if (!command[0].matches("[0-9]+")) {
                commonCommands(command);
                continue;
            }

            int cardIndex;
            try {
                cardIndex = Integer.parseInt(command[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please insert a number between 1 and 3");
                continue;
            }
            if (cardIndex < 1 || cardIndex > 3) {
                System.out.println("Invalid input, please insert a number between 1 and 3");
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

            if(turnLeft != 0) { //If it is the last turn, the player doesn't draw a new card
                //Now it's time to draw a new card
                while (true) {

                    successfulDraw = true; //reset the flag
                    draw.showDrawable(gUUID, rUUID);
                    System.out.print("""
                            Choose the type of the card you want to draw (golden or resource) \s
                            or write a different command (type /help to view the list of commands):""" + " ");
                    command = getCommandFromQueue();
                    String deck = command[0];

                    //decides if the player wants to play a card or use a simple command
                    if ((!deck.equals("golden") && !deck.equals("resource")) || command.length > 1) {
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
                                String.valueOf(3 - Integer.parseInt(choose))
                        });

                    if(successfulDraw) return;
                }
            }else{
                //if it is the last turn, the player doesn't draw a new card but we have to notify the server
                cli.sendMessage(
                        new Message(
                                NOTIFY_LAST_TURN,
                                cli.getClientID(),
                                cli.getGameID(),
                                new Object[]{}
                        )
                );
                return;
            }
        }
    }

    /**
     * This method is used to request the lobbies available.
     */
    public void requestLobbies() {
        cli.sendMessage(
                new Message(
                        REQUEST_AVAILABLE_LOBBIES,
                        cli.getClientID(),
                        cli.getGameID(),
                        new Object[]{}
                )
        );
    }

    /**
     * This method is the main thread of the TUI.
     */
    public void run(){
        String[] command;

        /*
        This thread reads the input from the user and puts it in the inputQueue,
        so that the main process doesn't have to wait for the input
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

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Show game logo
        startGame.ShowStartGame();

        //Request available lobbies
        requestLobbies();
        while(areThereAvailableLobbies == null)
            Thread.onSpinWait();
        if(areThereAvailableLobbies) {
            do {
                areThereAvailableLobbies = null;
                requestLobbies();
                while(areThereAvailableLobbies == null)
                    Thread.onSpinWait();
                if(!areThereAvailableLobbies) {
                    command = new String[]{"create"};
                    break;
                }
                System.out.println("\nAvailable lobbies:");
                for (String lobby : availableLobbies)
                        System.out.println(lobby);
                System.out.print("\nInsert the name of the lobby you want to join, or type 'create' (press 'enter' to refresh): ");
                command = getCommandFromQueue();

                if(command.length == 1 && command[0].equals("create")) break; //If the user wants to create a new lobby

            } while(command.length != 1 || !availableLobbies.contains(command[0]));
        } else {
            command = new String[]{"create"};
        }

        //Insert nickname
        nameP = insertNickname(command[0]);

        //Choose lobby size and create it
        createNewLobby(nameP);

        System.out.println("You just joined " + cli.getLobbyName() + "\n");


        //If user is the first to join the lobby, he will be the one to start the game
        if(cli.getGameID() == -1) {
            System.out.println("Waiting for other players to join the game...");
            System.out.print("If you want to leave the lobby type '/quitGame': ");
        }

        //Wait for the lobby to be full
        waitForFullLobby();

        try {
            goals.showObjective(allGoalsUUID.toArray(new String[0]));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        //Choose secret goal card and starting card
        preliminaryActions();
        System.out.println("Starting player is: " + startingPlayer);

        boolean first = true;

        waitForPreliminaryActions(); //We wait for every player to finish choices


        //Game flow implementation
        while(turnLeft > 0){

            if(myTurn){
                if(first) {
                    try {
                        printFullScreen();
                    } catch (IOException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                    first = false;
                }
                if(turnLeft != 3) turnLeft--;
                playerTurn();
            }else{

                if(first) {
                    first = false;
                    try {
                        //print the full screen
                        printFullScreen();
                    } catch (IOException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                }

                //Ask to insert a command
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

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.print("Your game is over! Wait for the other players to finish or type a command: ");
        while (!gameover){
            try{
                String input = inputQueue.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS);

                if(gameover) break;

                if(input != null){
                    command = input.split(" ");
                    commonCommands(command);
                    System.out.print("Your game is over! Wait for the other players to finish or type a command: ");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }

        System.out.println("\n");
        Views.clearScreen();
        //Display the end game screen
        endgame.showEndgame(winners, nicknames);
        System.exit(0);
    }

    /**
     * Prints the full screen of the game.
     */
    public void printFullScreen() throws IOException, ParseException {
        Views.clearScreen();

        topRow.showTopRow(currentPlayerNickname, nicknames, (ArrayList<Resource>) playerResources);
        //Print player's board
        int lines=board.drawBoard(onBoard, available)*3;
        if(lines<20){System.out.println("\n".repeat(20-lines));}

        ArrayList<String> printGoals = goals.showObjective(allGoalsUUID.toArray(new String[0]));
        ArrayList<String> printHand = handcards.showHand(currentHandUUID.toArray(new String[0]));

        for (int i = 0; i < 11; i++) {
            System.out.println(printHand.get(i) + printGoals.get(i));
        }
    }

    /**
     * Handles the commands received.
     *
     * @param command The command received.
     */
    public void commonCommands(String[] command){
        command[0] = command[0].toLowerCase();
        String message = """
                            Commands List:              (Template: /COMMAND Param1 Param 2)\s
                            
                            /infoCard posX posY - Returns infos about a card on the player's board
                            /showCommonCards - Shows the common resource and golden cards
                            /showBoard PlayerNickname - Shows the board of the given player
                            /infoCardOfPlayer PlayerNickname posX posY - Shows the given card's details of the given player
                            /openChat - Opens the chat tab, where you can read and send messages to other players
                            /quitGame - Quits the current session and ends the game for all the other players
                            
                            Those commands are not case-sensitive
                            """;
        switch(command[0]){
            case "/help":
                System.out.println(message);
                break;

            case "/openchat":
                chat();
                break;

            case "/quitgame":
                cli.sendMessage(
                        new Message(
                                REQUEST_INTERRUPT_GAME,
                                cli.getClientID(),
                                cli.getGameID()
                        )
                );
                return;

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

                cli.sendMessage(
                        new Message(
                                REQUEST_INFO_CARD,
                                cli.getClientID(),
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
                                cli.getClientID(),
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

                numHand--;

                cli.sendMessage(
                        new Message(
                                REQUEST_PLAYER_MOVE,
                                cli.getClientID(),
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

            case "/drawcardfromviewable":

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
                                    cli.getClientID(),
                                    cli.getGameID(),
                                    new Object[]{true, pos - 1})
                    );
                }
                else if(type.equals("resource")){
                    cli.sendMessage(
                            new Message(
                                    REQUEST_CARD,
                                    cli.getClientID(),
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
                String ty = command[1].toLowerCase();
                if(ty.equals("golden")){
                    cli.sendMessage(
                            new Message(
                                    REQUEST_CARD,
                                    cli.getClientID(),
                                    cli.getGameID(),
                                    new Object[]{true, 2})
                    );
                }else if(ty.equals("resource")){
                    cli.sendMessage(
                            new Message(
                                    REQUEST_CARD,
                                    cli.getClientID(),
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

            case "/testendgame":
                cli.sendMessage(
                        new Message(
                                TEST_END_GAME,
                                cli.getClientID(),
                                cli.getGameID()
                        )
                );
                break;

            case "/showboard":
                if(command.length < 2) {
                    System.out.println("Command not valid, try '/help' to view syntax");
                    break;
                }
                if(nameP.equals(command[1])) {
                    System.out.println("\nYou don't need this...");
                    break;
                }
                if(!nicknames.containsKey(command[1])) {
                    System.out.println("\nA player with the nickname '" + command[1] + "' doesn't exist!");
                    break;
                }
                requestShowBoard(command[1]);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "/infocardofplayer":
                if(command.length < 4) {
                    System.out.println("Command not valid, try '/help' to view syntax");
                    break;
                }
                if(!nicknames.containsKey(command[1])) {
                    System.out.println("\nA player with the nickname '" + command[1] + "' doesn't exist!");
                    break;
                }
                if(nameP.equals(command[1])) {
                    System.out.println("\nYou can use '/infoCard instead!");
                    break;
                }
                try {
                    requestCardInfos(command[1], Integer.parseInt(command[2]), Integer.parseInt(command[3]));
                } catch(Exception e) {
                    System.out.println("Command not valid, try '/help' to view syntax");
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;

            default:
                System.out.println("Command not valid, try '/help' to view syntax");
        }
    }

    /**
     * Displays the da details of the given card of the player with the given nickname.
     *
     * @param playerNickname The nickname of the player whose card has to be displayed.
     * @param posX The X position of the card.
     * @param posY The Y position of the card.
     */
    public void requestCardInfos(String playerNickname, int posX, int posY) {
        cli.sendMessage(
                new Message(
                        REQUEST_PLAYER_CARD,
                        cli.getClientID(),
                        cli.getGameID(),
                        new Object[]{playerNickname, posX, posY})
        );
    }

    /**
     * Displays the board of the player with the given nickname.
     *
     * @param playerNickname The nickname of the player whose board has to be displayed.
     */
    public void requestShowBoard(String playerNickname) {
        cli.sendMessage(
                new Message(
                        REQUEST_PLAYER_BOARD,
                        cli.getClientID(),
                        cli.getGameID(),
                        new Object[]{playerNickname})
        );
    }

    /**
     * Displays the chat.
     */
    void chat(){
        String [] command;
        for(int i = 0; i < 100; i++){
            System.out.println();
        }
        while(true){
            if(updateChat){
                chat.showChat(chatMessages);
                updateChat = false;
            }else {

                System.out.println("Type something to send a message to the lobby or /exit to return to the game:");
                while(!updateChat && inputQueue.isEmpty()){
                    Thread.onSpinWait();
                }

                if (updateChat) continue;

                command = getCommandFromQueue();

                if(command[0].equals("/exit")){
                    System.out.println("Exiting the chat room...");
                    try {
                        printFullScreen();
                    } catch (IOException | ParseException e) {
                    }
                    return;
                }else{
                    String msg = String.join(" ", command);
                    if (msg.isEmpty()) continue;
                    else {
                        cli.sendMessage(
                                new Message(
                                        NEW_CHAT_MESSAGE,
                                        cli.getClientID(),
                                        cli.getGameID(),
                                        new Object[]{" \033[38;5;208m" + nameP + ":\033[0m " + msg})
                        );
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
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

    /**
     * This method is used to reply to a heartbeat message to check whether a client is still connected or not.
     */
    private void replyHeartbeat() {

        cli.sendMessage(
                new Message(
                        HEARTBEAT_ACK,
                        cli.getClientID(),
                        cli.getGameID()
                )
        );

    }
}
