package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * This class represents a generic card that can be played on a PlayerBoard.
 */
public abstract class PlayableCard implements Serializable {
    private State state;
    private boolean isFlipped;
    private final Resource[] permResource;
    private Corner[] cardCorners;

    private final String UUID;

    /**
     * The constructor initializes the card with the given parameters.
     *
     * @param permResource The permanent resources of the card.
     * @param cardCorners The corners of the card.
     * @param UUID The UUID of the card, which identifies it uniquely.
     */
    public PlayableCard(Resource[] permResource, Corner[] cardCorners, String UUID) {
        this.permResource = permResource;
        this.cardCorners = cardCorners;
        this.UUID = UUID;
    }

    /**
     * Sets the card's state.
     *
     * @param state The card's state to be set.
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Sets the card's corners.
     *
     * @param cardCorners The card's corners.
     */
    public void setCorners(Corner[] cardCorners) {
        this.cardCorners = cardCorners;
    }

    /**
     * Sets the card's orientation status.
     *
     * @param flipped The card's orientation status.
     */
    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    /**
     * Returns the card's state.
     *
     * @return The card's state.
     */
    public State getState() {
        return state;
    }

    /**
     * Returns the card's orientation status.
     *
     * @return The card's orientation status.
     */
    public boolean isFlipped() {
        return isFlipped;
    }

    /**
     * Returns the card's permanent resources.
     *
     * @return The card's permanent resources.
     */
    public Resource[] getPermResource() {
        return permResource;
    }

    /**
     * Returns the card's corners.
     *
     * @return The card's corners.
     */
    public Corner[] getCardCorners() {
        return cardCorners;
    }

    /**
     * Returns the card's UUID, which identifies it uniquely.
     *
     * @return The card's UUID.
     */
    public String getUUID() {return UUID;}

    /**
     * Returns the card's points.
     *
     * @return The card's points.
     */
    public int getPoints(){return 0;}

    /**
     * Returns the card's rule for giving points.
     *
     * @return The card's rule for giving points.
     */
    public Object getRule(){return "NONE";}
}
