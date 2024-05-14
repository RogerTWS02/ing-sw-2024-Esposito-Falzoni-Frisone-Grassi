package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.PlayableCard;
import it.polimi.ingsw.network.client.ClientListenerInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface that defines the methods that the client can call on the server.
 */
public interface RMIServerInterface extends Remote {
    //Methods that the client can call on the server

    /**
     * Sends a login request to the server.
     *
     * @param nickname The nickname of the client (sender).
     * @param client The client that is sending the request.
     * @param clientID The ID of the client.
     * @throws IOException if an I/O error occurs.
     */
    void loginRequest(String nickname, ClientListenerInterface client, int clientID) throws IOException;

    /**
     * Sends a new lobby request to the server.
     *
     * @param nickname The nickname of the client (sender).
     * @param lobbyName The name of the lobby.
     * @param lobbySize The size of the lobby.
     * @param client The client that is sending the request.
     * @param clientID The ID of the client.
     * @throws RemoteException if a remote error occurs.
     * @throws FileNotFoundException if the file is not found.
     */
    void newLobbyRequest(String nickname, String lobbyName, int lobbySize, ClientListenerInterface client, int clientID) throws RemoteException, FileNotFoundException;

    /**
     * Sends a request of a certain card to the server.
     *
     * @param visible Viewable status of the card.
     * @param index Index of the card.
     * @param clientID The ID of the client.
     * @throws RemoteException if a remote error occurs.
     */
    void cardRequest(boolean visible, int index,int clientID) throws RemoteException;

    /**
     * Sends the request of a card placement and game flow status.
     *
     * @param clientID The ID of the client.
     * @param card The card to be placed.
     * @param x The X coordinate of the card.
     * @param y The Y coordinate of the card.
     * @throws IOException if an I/O error occurs.
     */
    void playerMove(int clientID, PlayableCard card, int x, int y) throws IOException;

    /**
     * Sends the request of infos about a certain card.
     *
     * @param x The X coordinate of the card.
     * @param y The Y coordinate of the card.
     * @param clientID The ID of the client.
     * @throws RemoteException if a remote error occurs.
     */
    void requestinfoCard(int x, int y, int clientID) throws RemoteException;
}
