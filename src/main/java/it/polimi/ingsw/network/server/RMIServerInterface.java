package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.PlayableCard;
import it.polimi.ingsw.network.client.ClientListenerInterface;
import it.polimi.ingsw.network.message.Message;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerInterface extends Remote {
    //Methods that the client can call on the server

    void loginRequest(String nickname, ClientListenerInterface client, int clientID) throws IOException;
    void newLobbyRequest(String nickname, String lobbyName, int lobbySize, ClientListenerInterface client, int clientID) throws RemoteException, FileNotFoundException;
    void cardRequest(boolean visible, int index,int clientID) throws RemoteException;
    void playerMove(int clientID, PlayableCard card, int x, int y) throws IOException;
    void requestinfoCard(int x, int y, int clientID) throws RemoteException;
}
