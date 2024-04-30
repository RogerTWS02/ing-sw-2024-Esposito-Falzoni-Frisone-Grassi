package it.polimi.ingsw.network.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

public class Message implements Serializable{
    private final MessageType messageType;
    private final Object obj;
    private final int id;

    public Message(MessageType messageType, int id, Object... obj){
        this.messageType = messageType;
        this.obj = obj;
        this.id = id;
    }

    public MessageType getMessageType(){
        return messageType;
    }

    public Object getObj() {
        return obj;
    }

    public int getSenderId(){
        return id;
    }

}
