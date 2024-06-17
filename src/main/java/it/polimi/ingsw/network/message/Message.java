package it.polimi.ingsw.network.message;

import java.io.Serializable;

/**
 * Message class is used to send messages between server and client.
 */
public class Message implements Serializable{

    /**
     * The type of the message.
     */
    private final MessageType messageType;

    /**
     * The objects to send.
     */
    private final Object[] obj;

    /**
     * The client ID of the sender.
     */
    private final int senderID;

    /**
     * The game identifier.
     */
    private final int gameID;

    /**
     * The constructor initializes the message with the given parameters.
     *
     * @param messageType The type of the message.
     * @param senderID The client ID of the sender.
     * @param gameID The game identifier.
     * @param obj The objects to be sent.
     */
    public Message(MessageType messageType, int senderID, int gameID, Object... obj){
        this.messageType = messageType;
        this.senderID = senderID;
        this.gameID = gameID;
        this.obj = obj;
    }

    /**
     * Returns the type of the message.
     *
     * @return The type of the message.
     */
    public MessageType getMessageType(){
        return messageType;
    }

    /**
     * Returns the objects to send.
     *
     * @return The objects to send.
     */
    public Object[] getObj() {
        return obj;
    }

    /**
     * Returns the client ID of the sender.
     *
     * @return The client ID of the sender.
     */
    public int getSenderID() {
        return senderID;
    }

    /**
     * Returns the game identifier.
     *
     * @return The game identifier.
     */
    public int getGameID() {
        return gameID;
    }
}
