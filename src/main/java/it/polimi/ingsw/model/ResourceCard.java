package it.polimi.ingsw.model;

/**
 * ResourceCard class represents a card that can be placed on the board.

 */
public class ResourceCard extends PlayableCard {
    private final int points;

    /**
     * The constructor initializes the card with the given parameters.
     *
     * @param permResource The permanent resources of the card.
     * @param cardCorners The corners of the card.
     * @param point The points given by the card.
     * @param UUID The UUID of the card, which identifies it uniquely.
     */
    public ResourceCard(Resource[] permResource, Corner[] cardCorners, int point, String UUID) {
        super(permResource, cardCorners, UUID);
        this.points = point;
    }

    /**
     * Returns the points given by the card.
     *
     * @return The points given by the card.
     */
    public int getPoints() {
        return points;
    }
}
