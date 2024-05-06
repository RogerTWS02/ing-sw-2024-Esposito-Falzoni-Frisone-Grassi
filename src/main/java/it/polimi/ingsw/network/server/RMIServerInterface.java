package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.PlayableCard;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerInterface extends Remote {
    //Methods that the client can call on the server

    void loginRequest(String nickname, int gameID) throws RemoteException;
    void newLobbyRequest(String nickname, String lobbyName, int lobbySize, int gameID) throws RemoteException;
    void cardRequest(boolean visible, int index, int gameID) throws RemoteException;
    void playerMove(int gameID, String nickname, PlayableCard card, int x, int y) throws RemoteException;
}
