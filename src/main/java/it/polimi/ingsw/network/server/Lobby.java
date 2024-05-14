package it.polimi.ingsw.network.server;

/**
 * Class that represents a game lobby in the server.
 */
public class Lobby {
    private boolean gameStarted = false;
    private final String lobbyName;
    private int playersConnected;
    private final int size;

    /**
     * The constructor, which initializes the lobby with the given parameters.
     *
     * @param lobbySize The size of the lobby.
     * @param playersConnected The number of players connected to the lobby.
     * @param lobbyName The name of the lobby.
     */
    public Lobby(int lobbySize, int playersConnected, String lobbyName) {
        this.playersConnected = playersConnected;
        this.lobbyName = lobbyName;
        this.size = lobbySize;
    }

    /**
     * Increments the number of players connected to the lobby.
     */
    public void incrementPlayersConnected() {
        this.playersConnected++;
    }

    /**
     * Returns the number of players connected to the lobby.
     *
     * @return The number of players connected to the lobby.
     */
    public int getPlayersConnected() {
        return playersConnected;
    }

    /**
     * Returns the lobby name.
     *
     * @return The lobby name.
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Sets the gameStarted attribute to the given value, which represents whether the game has started or not.
     *
     * @param gameStarted The value to set the gameStarted attribute to.
     */
    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    /**
     * Returns the status of the lobby about whether it is full or not.
     *
     * @return True if the lobby is full, false otherwise.
     */
    public boolean isLobbyFull(){
        return playersConnected == size;
    }

    /**
     * Returns whether the game has started or not.
     *
     * @return True if the game has started, false otherwise.
     */
    public boolean isGameStarted() {
        return gameStarted;
    }

    public int getSize() {
        return size;
    }
}