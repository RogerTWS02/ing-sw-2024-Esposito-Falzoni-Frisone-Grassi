package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.polimi.ingsw.network.message.MessageType.*;

/**
 * The GUI view.
 */
public class Gui {
    public Client cli;
    private volatile Boolean areThereAvailableLobbies = null;
    private List<String> availableLobbies;

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
        }
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
     * Makes the player choose a lobby.
     */
    public void lobbyChoice() {
        //TODO
    }

    /**
     * Makes the player insert a nickname.
     */
    public void insertNickname() {
        //TODO
    }

    /**
     * Makes the player choose the lobby size.
     */
    public void setLobbySize() {
        //TODO
    }

    /**
     * Flow for lobby choice, nickname insertion and lobby size selection.
     */
    public void welcomeScreenFlow() {
        //Request the available lobbies
        requestLobbies();
        while(areThereAvailableLobbies == null)
            Thread.onSpinWait();

        //If there are available lobbies, show them to the user, otherwise create a new one
        if(areThereAvailableLobbies) {
            //TODO
        } else {
            //TODO
        }
    }

    /**
     * Runs the GUI.
     */
    public void run() {
        Application.launch(GuiApp.class);
        GuiApp.setGui(this);
        sleep(1000);

        //Starts the first flow: lobby, nickname, lobby size
        welcomeScreenFlow();
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
}
