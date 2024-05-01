package it.polimi.ingsw.network.server;

public class Lobby {
    private final int lobbySize;
    private int playersConnected;
    private final String lobbyName;

    public Lobby(int lobbySize, int playersConnected, String lobbyName) {
        this.lobbySize = lobbySize;
        this.playersConnected = playersConnected;
        this.lobbyName = lobbyName;
    }

    public int getLobbySize() {
        return lobbySize;
    }

    public int getPlayersConnected() {
        return playersConnected;
    }

    public void incrementPlayersConnected() {
        this.playersConnected++;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public boolean isFull() {
        return playersConnected == lobbySize;
    }
}