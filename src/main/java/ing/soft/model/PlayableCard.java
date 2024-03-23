package ing.soft.model;

public abstract class PlayableCard {
    private boolean State;
    private boolean isFlipped;
    private final Resource[] permResource;
    private final Corner[] cardCorners;

    private final int UUID;

    public PlayableCard(Resource[] permResource, Corner[] cardCorners, int UUID) {
        this.permResource = permResource;
        this.cardCorners = cardCorners;
        this.UUID = UUID;
    }

    public void setState(boolean state) {
        State = state;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public boolean getState() {
        return State;
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
