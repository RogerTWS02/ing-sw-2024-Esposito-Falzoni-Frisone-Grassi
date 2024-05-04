package it.polimi.ingsw.model;

import java.io.Serializable;

public abstract class PlayableCard  implements Serializable {
    private State state;
    private boolean isFlipped;
    private final Resource[] permResource;
    private Corner[] cardCorners;

    private final String UUID;

    public PlayableCard(Resource[] permResource, Corner[] cardCorners, String UUID) {
        this.permResource = permResource;
        this.cardCorners = cardCorners;
        this.UUID = UUID;
    }

    public void setState(State state) {
        this.state = state;
    }

    //Corner array setter
    public void setCorners(Corner[] cardCorners) {
        this.cardCorners = cardCorners;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public State getState() {
        return state;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public Resource[] getPermResource() {
        return permResource;
    }

    public Corner[] getCardCorners() {
        return cardCorners;
    }

    public String getUUID() {return UUID;}


    public Resource getResource(){return null;}

    public int getPoints(){return 0;}
    public Object getRule(){return "NONE";}
}
