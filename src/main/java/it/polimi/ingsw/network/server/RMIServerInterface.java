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
    void messageHandler(Message message) throws IOException, ParseException;

    int createSkeleton(ClientListenerInterface skelly) throws IOException;
}
