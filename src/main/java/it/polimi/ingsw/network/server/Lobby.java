package it.polimi.ingsw.network.server;

public class Lobby {
    private final int lobbySize;
    private final int playersConnected;
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

    public String getLobbyName() {
        return lobbyName;
    }
}