package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.PlayableCard;
import it.polimi.ingsw.network.message.Message;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerImpl extends UnicastRemoteObject implements RMIServerInterface{
    private final Server server;

    public RMIServerImpl(Server server) throws RemoteException {
        this.server = server;
    }

    @Override
    public void loginRequest(Message message) throws IOException {
        server.serverLogin(message);
    }

    @Override
    public void newLobbyRequest(String nickname, String lobbyName, int lobbySize, int gameID) throws RemoteException {
        //need to implement the new lobby request
    }

    @Override
    public void cardRequest(boolean visible, int index, int gameID) throws RemoteException {
        //need to implement the card request
    }

    @Override
    public void playerMove(int gameID, String nickname, PlayableCard card, int x, int y) throws RemoteException {
        //need to implement the player move
    }
}
