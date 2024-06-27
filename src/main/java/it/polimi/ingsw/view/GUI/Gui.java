package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.view.GUI.controllers.WelcomeScreenController;
import javafx.application.Application;
import javafx.application.Platform;

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

    private Queue<String> chatMessages = new LinkedList<>();

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
        }
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
     * Notify to server the preliminary choices made by the player.
     *
     * @param choicesMade The preliminary choices made by the player.
     */
    public void preliminaryChoicesMade(Boolean[] choicesMade) {
        String selectedUUID = cardToChooseUUID.get(choicesMade[0] ? 0 : 1);
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
        Platform.runLater(() -> GuiApp.getEndGameScreenController().initialize_2());
        GuiApp.changeScene(GuiApp.getMainPlayerViewRoot());
    }

    /**
     * Handles the message containing the winners of the game.
     *
     * @param message The message received.
     */
    public void replyEndGameHandler(Message message) {
        winners = (ArrayList<String>) message.getObj()[0];
        gameState = GameFlowState.END;
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
        playerResources = (List<Resource>) message.getObj()[6];
        gameState = GameFlowState.PRELIMINARY_CHOICES;
        Platform.runLater(() -> GuiApp.getPreliminaryChoicesViewController().initialize_2());
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
     * Returns the UUIDs of the common goal cards.
     *
     * @return The UUIDs of the common goal cards.
     */
    public List<String> getAllGoalsUUID() {
        return allGoalsUUID;
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
