package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.message.Message;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface is used to allow the server to send messages to clients.
 */
public interface ClientListenerInterface extends Remote {

    /**
     * Allows the server to send a message to clients.
     *
     * @param message The message to be sent.
     * @throws RemoteException If the remote operation fails.
     */
    //void receiveMessage(Message message) throws IOException, ParseException;
    void sendMessageToClient(Message message) throws IOException, ParseException;
}
