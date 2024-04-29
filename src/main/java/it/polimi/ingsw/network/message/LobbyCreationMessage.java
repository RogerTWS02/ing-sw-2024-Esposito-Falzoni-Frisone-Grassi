package it.polimi.ingsw.network.message;

import it.polimi.ingsw.network.server.Lobby;

public class LobbyCreationMessage extends Message{
    Lobby lobby;

    public LobbyCreationMessage(int id, Lobby lobby){
        super(MessageType.NEW_LOBBY, id, true);
        this.lobby = lobby;
    }

    public Lobby getLobby() {
        return lobby;
    }
}
