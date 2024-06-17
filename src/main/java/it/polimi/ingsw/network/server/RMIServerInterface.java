package it.polimi.ingsw.network.server;


import it.polimi.ingsw.network.client.ClientListenerInterface;
import it.polimi.ingsw.network.message.Message;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.rmi.Remote;

/**
 * Interface that defines the methods that the client can call on the server.
 */
public interface RMIServerInterface extends Remote {
    //Methods that the client can call on the server

    /**
     * Handles the message received from the client.
     *
     * @param message The message received from the client.
     * @throws IOException If an I/O error occurs.
     * @throws ParseException If an error occurs while parsing the message.
     */
    void messageHandler(Message message) throws IOException, ParseException;

    /**
     * Creates a new skeleton for the client listener interface.
     *
     * @param skelly The client listener interface.
     * @return The id of the skeleton.
     * @throws IOException If an I/O error occurs.
     */
    int createSkeleton(ClientListenerInterface skelly) throws IOException;
}
