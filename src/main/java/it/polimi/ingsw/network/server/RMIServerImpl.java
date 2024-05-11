package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.PlayableCard;
import it.polimi.ingsw.network.message.Message;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerImpl extends UnicastRemoteObject implements RMIServerInterface{
    private final Server server;

    public RMIServerImpl(Server server) throws RemoteException {
        this.server = server;
    }

    @Override
    public void loginRequest(String nickname) throws IOException {
        server.serverLogin(new Message(null, server.getPlayersConnectedToServer(), -1, nickname));
    }

    @Override
    public void newLobbyRequest(String nickname, String lobbyName, int lobbySize) throws RemoteException, FileNotFoundException {
        server.requestNewLobby(new Message(null, server.getPlayersConnectedToServer(), -1, nickname, lobbyName, lobbySize));
    }

    @Override
    public void cardRequest(boolean visible, int index, int playerID) throws RemoteException {

        //PlayerId is the clientPort of the player
        if(visible){
            server.requestCard(new Message(null, playerID, server.getGameControllerMap()
                    .get(playerID)
                    .getCurrentGame().getGameID(), new Object[]{1,index}));
        }else{
            server.requestCard(new Message(null, playerID, server.getGameControllerMap()
                    .get(playerID)
                    .getCurrentGame().getGameID(), new Object[]{0,index}));

        }


    }

    @Override
    public void playerMove(int senderID, PlayableCard card, int x, int y) throws IOException {
        server.playerMove(new Message(null, senderID, server.getGameControllerMap().get(senderID).getCurrentGame().getGameID(), new Object[]{card, x, y}));
    }

    @Override
    public void requestinfoCard(int x, int y) throws RemoteException {
        server.requestInfoCard(new Message(null,-1, -1, new Object[]{x, y}));
    }
}
