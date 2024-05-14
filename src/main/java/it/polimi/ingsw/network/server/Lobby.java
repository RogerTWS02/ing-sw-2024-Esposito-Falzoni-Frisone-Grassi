package it.polimi.ingsw.network.server;

public class Lobby {
    private boolean gameStarted = false;
    private final String lobbyName;
    private int playersConnected;
    private final int size;


    public Lobby(int lobbySize, int playersConnected, String lobbyName) {
        this.playersConnected = playersConnected;
        this.lobbyName = lobbyName;
        this.size = lobbySize;
    }

    public void incrementPlayersConnected() {
        this.playersConnected++;
    }

    public int getPlayersConnected() {
        return playersConnected;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public boolean isLobbyFull(){
        return playersConnected == size;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public int getSize() {
        return size;
    }
}