package it.polimi.ingsw.model;

public abstract class PlayableCard {
    private State state;
    private boolean isFlipped;
    private final Resource[] permResource;
    private final Corner[] cardCorners;

    private final int UUID;

    public PlayableCard(Resource[] permResource, Corner[] cardCorners, int UUID) {
        this.permResource = permResource;
        this.cardCorners = cardCorners;
        this.UUID = UUID;
    }

    public void setState(State state) {
        this.state = state;
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

    public int getUUID() {return UUID;}
}
