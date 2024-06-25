package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.polimi.ingsw.network.message.MessageType.*;

//TODO

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
    private final Object lock = new Object();

    /**
     * Handles arriving message from the server and updates the TUI.
     *
     * @param message The message received.
     */
    public void onMessageReceived(Message message) {
        switch (message.getMessageType()) {

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
        }
    }

    /**
     * Refreshes the available lobbies.
     */
    public void refreshAvailableLobbies() {
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
        if(message.getObj()[0].equals("Invalid nickname, please try a different one!"))
            validatedNickname = false;
    }

    /**
     * Handles the reply containing the available lobbies.
     *
     * @param message The message received.
     */
    public void handleReplyAvailableLobbies(Message message) {
        String[] availableLobbies = (String[]) message.getObj()[0];
        if(availableLobbies.length == 0){
            areThereAvailableLobbies = false;
            return;
        }
        areThereAvailableLobbies = true;
        this.availableLobbies = new ArrayList<>(Arrays.asList(availableLobbies));
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
        if(areThereAvailableLobbies) {
            GuiApp.getWelcomeScreenController().showAvailableLobbies((ArrayList<String>) availableLobbies);
        }
        else
            createNewLobbyOnRequest();
    }

    /**
     * This method is used to create a new lobby on request.
     */
    public void createNewLobbyOnRequest() {
        GuiApp.getWelcomeScreenController().setUpNicknameInsertion();

        while(nameP == null && lobbySize == 0)
            Thread.onSpinWait();

        requestLoginForNewLobby();
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
     * Sets the nickname of the player.
     *
     * @param nickname The nickname of the player.
     */
    public void setNickname(String nickname) {
        nickname = nickname.trim();
        if(nickname.isEmpty() || nickname.length() > 16 || nickname.contains(" "))
            GuiApp.getWelcomeScreenController().setUpNicknameInsertion();
        else {
            nameP = nickname;
            GuiApp.getWelcomeScreenController().setUpLobbySizeSelection();
        }
    }

    /**
     * Sets the lobby size.
     *
     * @param lobbySize The size of the lobby.
     */
    public void setLobbySize(int lobbySize) {
        this.lobbySize = lobbySize;
    }

    /**
     * Runs the GUI.
     */
    public void run() throws InterruptedException {
        Thread guiAppThread = new Thread(() -> Application.launch(GuiApp.class));
        guiAppThread.start();

        GuiApp.setGui(this);
        while(!GuiApp.guiStarted)
            Thread.onSpinWait();

        //Starts the first flow: lobby, nickname, lobby size
        welcomeScreenFlow_LobbyAndNickname();

        while (gameState == GameFlowState.LOBBY) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (Exception e) {
                    System.err.println("Error in GUI: waiting for lock (LOBBY)");
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
     * Sets the state of the game.
     *
     * @param state The state of the game.
     */
    private void setState(Gui.GameFlowState state) {
        synchronized (lock) {
            this.gameState = state;
            lock.notifyAll();
        }
    }

    /**
     * Enumerates the possible states of the game.
     */
    private enum GameFlowState {
        LOBBY,
        GAME,
        END
    }
}
