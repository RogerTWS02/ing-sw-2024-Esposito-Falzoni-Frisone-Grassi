package it.polimi.ingsw.network.message;

public interface MessageListener {
    Message onMessageReceived(Message message);
}
