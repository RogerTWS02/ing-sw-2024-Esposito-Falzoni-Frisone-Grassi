package it.polimi.ingsw.network.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientListenerInterface extends Remote {

    /**
     * Allows the server to send a message to clients.
     *
     * @param message The message to be sent.
     * @throws RemoteException if the remote operation fails.
     */
    void receiveMessage(String message) throws RemoteException;
}
