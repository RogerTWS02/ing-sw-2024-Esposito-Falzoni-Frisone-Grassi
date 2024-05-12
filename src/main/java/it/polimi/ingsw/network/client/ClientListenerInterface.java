package it.polimi.ingsw.network.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientListenerInterface extends Remote {

    /**
     * Method that allows the server to send a message to the client
     * @param message the message to be sent
     * @throws RemoteException if the remote operation fails
     */
    void receiveMessage(String message) throws RemoteException;
}
