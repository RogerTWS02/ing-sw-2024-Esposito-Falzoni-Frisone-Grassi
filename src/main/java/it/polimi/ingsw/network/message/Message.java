package it.polimi.ingsw.network.message;

public abstract class Message{
    private static final long serialVersionUID = 4951303731052728724L;
    private final MessageType messageType;
    private final int id; //id of the client that sends the message
    private final boolean initializationMessage;

    public Message(MessageType messageType, int id, boolean initMessage){
        this.messageType = messageType;
        this.id = id;
        this.initializationMessage = initMessage;
    }

    public MessageType getMessageType(){
        return messageType;
    }

    public int getSenderId(){
        return id;
    }

    public boolean isInitializationMessage() {
        return initializationMessage;
    }
}