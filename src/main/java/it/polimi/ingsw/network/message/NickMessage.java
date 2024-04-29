package it.polimi.ingsw.network.message;

public class NickMessage extends Message{

    private final String name;

    /**
     * This constructor is created for those messages which contain nicknames
     * @param type type of message
     * @param sender the id of the player
     * @param initializationMessage if it is an initialization message
     * @param name the nick the player wants
     */
    public NickMessage(MessageType type, int sender, boolean initializationMessage, String name) {
        super(type, sender, initializationMessage);
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
