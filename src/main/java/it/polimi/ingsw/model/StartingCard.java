package it.polimi.ingsw.model;

/**
 * StartingCard class is a subclass of PlayableCard and represents the cards that are placed on the board at the beginning of the game for each player.
 */
public class StartingCard extends PlayableCard{

    /**
     * The corners of the card when it is not flipped.
     */
    private Corner[] frontCardCorners;

    /**
     * The corners of the card when it is flipped.
     */
    private Corner[] backCardCorners;

    /**
     * The constructor creates a StartingCard object with the specified parameters.
     *
     * @param permResource The permanent resources of the card.
     * @param FrontCardCorners The corners of the card when it is not flipped.
     * @param BackCardCorners The corners of the card when it is flipped.
     * @param UUID The UUID of the card, which identifies it uniquely.
     */
    public StartingCard(Resource[] permResource, Corner[] FrontCardCorners, Corner[] BackCardCorners, String UUID) {
        super(permResource, null, UUID);
        this.backCardCorners =BackCardCorners;
        this.frontCardCorners =FrontCardCorners;
    }

    /**
     * Returns the corners of the card based on its orientation status.
     *
     * @return The visible corners of the card.
     */
    public Corner[] getCardCorners() {
        if (!isFlipped())
            return backCardCorners;
        return frontCardCorners;
    }

    /**
     * Sets the corners of the card on the front side.
     *
     * @param FrontCardCorners The corners of the card when it is not flipped.
     */
    public void setFrontCardCorners(Corner[] FrontCardCorners) {
        this.frontCardCorners = FrontCardCorners;
    }

    /**
     * Sets the corners of the card on the back side.
     *
     * @param BackCardCorners The corners of the card when it is flipped.
     */
    public void setBackCardCorners(Corner[] BackCardCorners) {
        this.backCardCorners = BackCardCorners;
    }

    /**
     * Returns the corners of the card when it is not flipped.
     *_
     * @return The corners on the front side of the card.
     */
    public Corner[] getFrontCardCorners() {
        return frontCardCorners;
    }

    /**
     * Returns the corners of the card when it is flipped.
     *
     * @return The corners on the back side of the card.
     */
    public Corner[] getBackCardCorners() {
        return backCardCorners;
    }
}
