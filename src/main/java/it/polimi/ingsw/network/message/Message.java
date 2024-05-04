package it.polimi.ingsw.network.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

public class Message implements Serializable{
    private final MessageType messageType;
    private final Object[] obj;
    private final int senderID, gameID;

    public Message(MessageType messageType, int senderID, int gameID, Object... obj){
        this.messageType = messageType;
        this.senderID = senderID;
        this.gameID = gameID;
        this.obj = obj;
    }

    public MessageType getMessageType(){
        return messageType;
    }

    public Object[] getObj() {
        return obj;
    }

    public int getSenderID() {
        return senderID;
    }

    public int getGameID() {
        return gameID;
    }
}
