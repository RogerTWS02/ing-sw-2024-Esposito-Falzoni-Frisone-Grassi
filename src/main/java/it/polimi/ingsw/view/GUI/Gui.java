package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.view.GUI.controllers.WelcomeScreenController;
import javafx.application.Application;
import javafx.application.Platform;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

import static it.polimi.ingsw.network.message.MessageType.*;

/**
 * The GUI view.
 */
public class Gui {
    private GameFlowState gameState = GameFlowState.LOBBY;
    public Client cli;
    private volatile Boolean areThereAvailableLobbies = null;
    private List<String> availableLobbies;
    private volatile String nameP = null;
    private volatile int lobbySize = 0;
    private volatile Boolean validatedNickname = null;
    private volatile Boolean validatedLobby = null;
    private final Object lock = new Object();
    private ArrayList<String> currentHandUUID;
    private List<String> allGoalsUUID = new ArrayList<>();
    private List<String> cardToChooseUUID;
    private volatile boolean myTurn;
    private Map<String, Integer> nicknames;
    private String currentPlayerNickname;
    private String startingPlayer;
    private List<Resource> playerResources;
    private ArrayList<String> winners;
    private String[] gUUID = new String[3];
    private String[] rUUID = new String[3];
    private List<int[]> available;
    private Resource[][] onBoard = new Resource[81][81];
    private int positionX = 0, positionY = 0;

    private Queue<String> chatMessages = new LinkedList<>();
    private int numHand;
    private boolean side;

    /**
     * Handles arriving message from the server and updates the TUI.
     *
     * @param message The message received.
     */
    public void onMessageReceived(Message message) {
        switch (message.getMessageType()) {

            case REPLY_CHAT_MESSAGE:
                replyChatMessage(message);
                break;

            case HEARTBEAT:
                replyHeartbeat();
                break;

            case REPLY_AVAILABLE_LOBBIES:
                handleReplyAvailableLobbies(message);
                break;

            case REPLY_BAD_REQUEST:
                replyBadRequestHandler(message);
                break;

            case REPLY_NEW_LOBBY:
                replyNewLobbyHandler(message);
                break;

            case REPLY_LOBBY_INFO:
                replyLobbyInfoHandler(message);
                break;

            case REPLY_BEGIN_GAME:
                replyBeginGameHandler(message);
                break;

            case REPLY_INTERRUPT_GAME:
                handleReplyInterruptGame(message);
                break;

            case REPLY_END_GAME:
                replyEndGameHandler(message);
                break;

            case NOTIFY_GAME_STARTING:
                notifyGameStartingHandler();
                break;

            case REPLY_YOUR_TURN:
                replyYourTurnHandler(message);
                break;

            case REPLY_VIEWABLE_CARDS:
                replyViewableCardsHandler(message);
                break;

            case REPLY_HAND_UPDATE:
                replyHandUpdateHandler(message);
                break;

            case REPLY_UPDATED_SCORE:
                replyUpdatedScoreHandler(message);
                break;

            case REQUEST_PLAYER_BOARD_INFOS:
                requestPlayerBoardInfosHandler(message);
                break;

            case REPLY_POINTS_UPDATE:
                replyPointsUpdateHandler(message);
                break;

            case REPLY_CHOICES_MADE:
                handleChoicesMade(message);
                break;

            case NOTIFY_END_GAME:
                notifyEndGameHandler(message);
                break;

            case REPLY_EMPTY_DECK:
                replyEmptyDeckHandler();
                break;
        }
    }

    /**
     * Handles the message containing the info of empty deck.
     */
    public void replyEmptyDeckHandler() {
        GuiApp.getMainPlayerViewController().showError("You can't draw this card, the deck is empty!");
    }

    /**
     * Handles the message which notifies the end phase of the game.
     *
     * @param message The message received.
     */
    public void notifyEndGameHandler(Message message) {
        GuiApp.getMainPlayerViewController().showTurnsLeft((Integer) message.getObj()[0]);
    }

    /**
     * Handles the message containing the updated score and the available positions.
     *
     * @param message The message received.
     */

    public void replyUpdatedScore(Message message){
        String prevUUID = currentHandUUID.get(numHand);
        currentHandUUID.set(numHand, "");

        //Available places
        available = (List<int[]>) message.getObj()[0];

        //Update the board with the placed card
        String nick = (String) message.getObj()[2];
        int score = (int) message.getObj()[3];
        nicknames.put(nick, score);
        playerResources = (List<Resource>) message.getObj()[4];

        //Update the view of the player board
        GuiApp.getMainPlayerViewController().updatePlayerBoard(prevUUID, side, (Resource) message.getObj()[1], available);
    }

    /**
     * Updates the main player view top row.
     */
    public void updateScores() {
        StringBuilder text = new StringBuilder();
        text.append("SCORES     ");
        for(String nickname : nicknames.keySet())
            text.append(nickname).append(": ").append(nicknames.get(nickname)).append("  ");
        GuiApp.getMainPlayerViewController().updateTopRowLabel(text.toString());
    }

    /**
     * Handles the message containing the updated points.
     *
     * @param message The message received.
     */
    public void replyPointsUpdateHandler(Message message){
        String name = (String) message.getObj()[0];
        int points = (int) message.getObj()[1];
        nicknames.put(name, points);

        updateScores();
    }

    /**
     * Handles the message containing the request of the player board infos.
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
     * Handles the message containing the updated score.
     *
     * @param message The message received.
     */
    public void replyUpdatedScoreHandler(Message message) {
        String prevUUID = currentHandUUID.get(numHand);
        currentHandUUID.set(numHand, "");

        //Available places
        available = (List<int[]>) message.getObj()[0];

        onBoard[positionX][positionY] = (Resource) message.getObj()[1];

        //Update the board with the placed card
        String nick = (String) message.getObj()[2];
        int score = (int) message.getObj()[3];
        nicknames.put(nick, score);
        playerResources = (List<Resource>) message.getObj()[4];

        //Update the view of the player board
        GuiApp.getMainPlayerViewController().updatePlayerBoard(prevUUID, side, (Resource) message.getObj()[1], available);
    }

    /**
     * Handles the message containing the updated hand.
     *
     * @param message The message received.
     */
    public void replyHandUpdateHandler(Message message) {
        String newCardUUID = (String) message.getObj()[0];
        for(int i = 0; i < 3; i++){
            if(currentHandUUID.get(i).isEmpty()){
                currentHandUUID.set(i, newCardUUID);
                break;
            }
        }
        GuiApp.getMainPlayerViewController().update_view();
    }

    /**
     * Sends to the server the request of the viewable cards.
     */
    public void requestViewableCards() {
        cli.sendMessage(
                new Message(
                        REQUEST_VIEWABLE_CARDS,
                        cli.getClientID(),
                        cli.getGameID())
        );
    }

    /**
     * Handles the message containing the viewable cards.
     *
     * @param message The message received.
     */
    public void replyViewableCardsHandler(Message message) {
        rUUID = (String[]) message.getObj()[0];
        gUUID = (String[]) message.getObj()[1];
    }

    /**
     * Handles the message containing the chat message.
     *
     * @param message The message received.
     */
    public void replyChatMessage(Message message){
        chatMessages.add((String) message.getObj()[0]);
        if(chatMessages.size() > 20)
            chatMessages.poll();

        //msg.forEach(System.out::println);
        StringBuilder concatStr = new StringBuilder();
        chatMessages.forEach(concatStr::append);

        //send the new message to the chat View
        GuiApp.getChatController().updateMessage(concatStr);
    }

    /**
     * Handles the message containing the choices made by the player and updates the player board with the starting card.
     *
     * @param message The message received.
     */
    public void handleChoicesMade(Message message) {
        available = (List<int[]>) message.getObj()[0];
    }

    /**
     * Sends a chat message.
     * @param msg The message to send.
     */

    public void sendChatMessage(String msg){
        cli.sendMessage(
                new Message(
                        NEW_CHAT_MESSAGE,
                        cli.getClientID(),
                        cli.getGameID(),
                        new Object[]{" \033[38;5;208m" + nameP + ":\033[0m " + msg + "\n"})
        );
    }

    /**
     * Handles the message containing turn information.
     *
     * @param message The message received.
     */
    public void replyYourTurnHandler(Message message) {
        currentPlayerNickname = (String) message.getObj()[0];
        myTurn = (boolean) message.getObj()[1];
        if(nameP.equals(currentPlayerNickname))
            GuiApp.getMainPlayerViewController().setTurnLabel("It's your turn!", true);
        else
            GuiApp.getMainPlayerViewController().setTurnLabel("It's " + currentPlayerNickname + "'s turn!", false);
    }

    /**
     * Places the card in the specified position.
     *
     * @param numHand The index of the card in the hand.
     * @param positionX The X position of the card.
     * @param positionY The Y position of the card.
     * @param side The side of the card.
     */
    public void placeCard(int numHand, int positionX, int positionY, boolean side){
        this.side = side;
        this.numHand = numHand;
        cli.sendMessage(
                new Message(
                        REQUEST_PLAYER_MOVE,
                        cli.getClientID(),
                        cli.getGameID(),
                        new Object[]{
                                numHand,
                                side,
                                positionX,
                                positionY
                        })
        );
    }

    /**
     * Draws a card from the deck.
     *
     * @param type The type of the card.
     * @param pos The index of the card.
     */
    public void drawCard(boolean type, int pos) {
        cli.sendMessage(
                new Message(
                        REQUEST_CARD,
                        cli.getClientID(),
                        cli.getGameID(),
                        new Object[]{type, pos})
        );
    }

    /**
     * Notify to server the preliminary choices made by the player.
     *
     * @param choicesMade The preliminary choices made by the player.
     */
    public void preliminaryChoicesMade(Boolean[] choicesMade) {
        String selectedUUID = cardToChooseUUID.get(choicesMade[0] ? 1 : 2);
        allGoalsUUID.add(selectedUUID);
        boolean side = !choicesMade[1];
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
    }

    /**
     * Handles the message notifying the beginning of the game.
     */
    public void notifyGameStartingHandler() {
        gameState = GameFlowState.GAME;
        Platform.runLater(() -> {
            GuiApp.getMainPlayerViewController().initialize_2();
        });
        requestViewableCards();
        GuiApp.changeScene(GuiApp.getMainPlayerViewRoot());

        //Update the view of the starting card
        GuiApp.getMainPlayerViewController().updatePlayerBoard(cardToChooseUUID.get(0), side, null, available);
    }

    /**
     * Handles the message containing the winners of the game.
     *
     * @param message The message received.
     */
    public void replyEndGameHandler(Message message) {
        winners = (ArrayList<String>) message.getObj()[0];
        gameState = GameFlowState.END;
        Platform.runLater(() -> GuiApp.getEndGameScreenController().initialize_2());
        GuiApp.changeScene(GuiApp.getEndGameScreenRoot());
    }

    /**
     * Handles the interruption of the game.
     */
    public void handleReplyInterruptGame(Message message) {
        if(gameState != GameFlowState.END) {
            System.out.println((String) message.getObj()[0]);
            cli.closeConnection();
        }
    }

    /**
     * Handles the receiving of the message notifying the beginning of the game.
     *
     * @param message The message received.
     */
    public void replyBeginGameHandler(Message message) {
        cli.setGameID(message.getGameID());
        currentHandUUID = new ArrayList<>((List<String>) message.getObj()[0]);
        allGoalsUUID = (List<String>) message.getObj()[1];
        cardToChooseUUID = (List<String>) message.getObj()[2];
        myTurn = (boolean) message.getObj()[3];
        nicknames = (Map<String, Integer>) message.getObj()[4];
        currentPlayerNickname = (String) message.getObj()[5];
        startingPlayer = (String) message.getObj()[5];

        if(startingPlayer.equals(nameP))
            GuiApp.getMainPlayerViewController().setTurnLabel("You start!", true);
        else
            GuiApp.getMainPlayerViewController().setTurnLabel("Starting player is: " + startingPlayer, false);

        playerResources = (List<Resource>) message.getObj()[6];
        gameState = GameFlowState.PRELIMINARY_CHOICES;
        Platform.runLater(() -> GuiApp.getPreliminaryChoicesViewController().initialize_2());
        updateScores();
        GuiApp.changeScene(GuiApp.getPreliminaryChoicesViewRoot());
    }

    /**
     * Refreshes the available lobbies.
     */
    public void refreshAvailableLobbies() {
        areThereAvailableLobbies = null;
        availableLobbies = null;
        welcomeScreenFlow_LobbyAndNickname();
    }

    /**
     * Handles the reply containing the lobby information.
     *
     * @param message The message received.
     */
    public void replyLobbyInfoHandler(Message message) {
        cli.setLobbyName((String) message.getObj()[0]);
        cli.setLobbySize((Integer) message.getObj()[1]);
        GuiApp.getWelcomeScreenController().waitForOtherPlayers();
    }

    /**
     * Handles the reply containing the new lobby.
     *
     * @param message The message received.
     */
    public void replyNewLobbyHandler(Message message) {
        validatedNickname = true;
        cli.setLobbyName((String) message.getObj()[0]);
        cli.sendMessage(
                new Message(
                        REQUEST_NEW_LOBBY,
                        cli.getClientID(),
                        -1, //gameID is not set until the game actually starts
                        new Object[]{
                                nameP,
                                cli.getLobbyName(),
                                lobbySize
                        })
        );
    }

    /**
     * Handles the reply containing the new lobby.
     *
     * @param message The message received.
     */
    public void replyBadRequestHandler(Message message) {
        switch ((String) message.getObj()[0]){
            case "Invalid nickname, please try a different one!":
                validatedNickname = false;
                break;

            case "The chosen lobby is full! Creating a new one...":
                GuiApp.getWelcomeScreenController().showFullLobbyError();
                sleep(3000);
                validatedLobby = false;
                validatedNickname = false;
                break;

            case "You don't have the required resources to place this card!":
                GuiApp.getMainPlayerViewController().showError(
                        "You don't have the required resources to place this card!"
                );
                break;

            case "You can't place a card here!":
                GuiApp.getMainPlayerViewController().showError(
                        "You can't place a card here!"
                );
                break;
        }
    }

    /**
     * Handles the reply containing the available lobbies.
     *
     * @param message The message received.
     */
    public void handleReplyAvailableLobbies(Message message) {
        String[] availableLobbies = (String[]) message.getObj()[0];
        if(availableLobbies == null) {
            areThereAvailableLobbies = false;
            return;
        }
        if(availableLobbies.length == 0){
            areThereAvailableLobbies = false;
            return;
        }
        this.availableLobbies = new ArrayList<>(Arrays.asList(availableLobbies));
        areThereAvailableLobbies = true;
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
     * Flow for lobby choice, nickname insertion and lobby size selection.
     */
    public void welcomeScreenFlow_LobbyAndNickname() {
        //Request the available lobbies
        requestLobbies();
        while(areThereAvailableLobbies == null)
            Thread.onSpinWait();

        //If there are available lobbies, show them to the user, otherwise create a new one
        if(areThereAvailableLobbies)
            GuiApp.getWelcomeScreenController().showAvailableLobbies((ArrayList<String>) availableLobbies);
        else
            createNewLobbyOnRequest();
    }

    /**
     * This method is used to create a new lobby on request.
     */
    public void createNewLobbyOnRequest() {
        GuiApp.getWelcomeScreenController().setUpNicknameInsertion(WelcomeScreenController.WelComeScreenStateEnum.INSERTING_NICKNAME);

        while(nameP == null || lobbySize == 0)
            Thread.onSpinWait();

        requestLoginForNewLobby();
    }

    /**
     * Handles the choice of the lobby, if the user wants to join an existing one.
     */
    public void handleLobbyChoice(String lobbyChoice) {
        if(availableLobbies.contains(lobbyChoice)) {
            GuiApp.getWelcomeScreenController().setUpNicknameInsertion(WelcomeScreenController.WelComeScreenStateEnum.INSERTING_JUST_NICKNAME);
            while(nameP == null)
                Thread.onSpinWait();

            cli.sendMessage(
                    new Message(
                            REQUEST_LOGIN,
                            cli.getClientID(),
                            -1, //gameID is not set until the game actually starts
                            new Object[]{nameP, lobbyChoice}));

            while(validatedNickname == null && validatedLobby == null)
                Thread.onSpinWait();

            if(!validatedNickname || !validatedLobby) {
                validatedNickname = null;
                validatedLobby = null;
                welcomeScreenFlow_LobbyAndNickname();
            }
        } else {
            GuiApp.getWelcomeScreenController().showInvalidLobbyNameError();
            sleep(3000);
            welcomeScreenFlow_LobbyAndNickname();
        }
    }

    /**
     * This method is used to request the login for a new lobby.
     */
    public void requestLoginForNewLobby() {
        cli.sendMessage(
                new Message(
                        REQUEST_LOGIN,
                        cli.getClientID(),
                        -1, //gameID is not set until the game actually starts
                        new Object[]{nameP, "create"}));

        while(validatedNickname == null)
            Thread.onSpinWait();

        if(!validatedNickname) {
            validatedNickname = null;
            nameP = null;
            lobbySize = 0;
            welcomeScreenFlow_LobbyAndNickname();
        }
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

    /**
     * Sets the nickname of the player.
     *
     * @param nickname The nickname of the player.
     */
    public void setNickname(String nickname) {
        nickname = nickname.trim();
        if(nickname.isEmpty() || nickname.length() > 16 || nickname.contains(" "))
            GuiApp.getWelcomeScreenController().setUpNicknameInsertion(null);
        else {
            nameP = nickname;
            if(GuiApp.getWelcomeScreenController().getScreenState() == WelcomeScreenController.WelComeScreenStateEnum.INSERTING_JUST_NICKNAME)
                return;
            GuiApp.getWelcomeScreenController().setUpLobbySizeSelection();
        }
    }

    /**
     * Sets the lobby size.
     *
     * @param lobbySize The size of the lobby.
     */
    public void setLobbySize(int lobbySize) {
        cli.setLobbySize(lobbySize);
        this.lobbySize = lobbySize;
    }

    /**
     * Returns the winner of the game.
     *
     * @return The winner of the game.
     */
    public ArrayList<String> getWinners() {
        return winners;
    }

    /**
     * Returns the viewable resource cards.
     *
     * @return The viewable resource cards.
     */
    public String[] getResourceViewableCards() {
        return rUUID;
    }

    /**
     * Returns the viewable golden cards.
     *
     * @return The viewable golden cards.
     */
    public String[] getGoldenViewableCards() {
        return gUUID;
    }

    /**
     * Returns the UUIDs of the common goal cards.
     *
     * @return The UUIDs of the common goal cards.
     */
    public List<String> getAllGoalsUUID() {
        return allGoalsUUID;
    }

    /**
     * Sets posX and posY of the last card placed.
     *
     * @param x The X position of the card.
     * @param y The Y position of the card.
     */
    public void setPositions(int x, int y){
        positionX = x;
        positionY = y;
    }

    /**
     * Returns the UUIDs of the cards to choose.
     *
     * @return The UUIDs of the cards to choose.
     */
    public List<String> getCardToChooseUUID() {
        return cardToChooseUUID;
    }

    /**
     * Returns the cards in the hand.
     *
     * @return The cards in the hand.
     */
    public ArrayList<String> getCurrentHandUUID() {
        return currentHandUUID;
    }

    /**
     * Runs the GUI.
     */
    public void run() throws InterruptedException {
        GuiApp.setGui(this);
        Thread guiAppThread = new Thread(() -> Application.launch(GuiApp.class));
        guiAppThread.start();

        while(!GuiApp.guiStarted)
            Thread.onSpinWait();

        //Starts the first flow: lobby, nickname, lobby size
        welcomeScreenFlow_LobbyAndNickname();

        while (gameState != GameFlowState.USEFUL) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (Exception e) {
                    System.err.println("Error in GUI class");
                }
            }
        }
    }

    /**
     * Makes the current thread sleep for the specified amount of milliseconds.
     *
     * @param millis The number of milliseconds to sleep.
     */
    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enumerates the possible states of the game.
     */
    private enum GameFlowState {
        LOBBY,
        PRELIMINARY_CHOICES,
        GAME,
        END,
        USEFUL
    }
}
