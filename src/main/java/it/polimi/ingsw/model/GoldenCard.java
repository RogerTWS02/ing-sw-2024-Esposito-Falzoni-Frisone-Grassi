package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 * GoldenCard class is a subclass of PlayableCard class, it represents the Golden cards in the game.
 */
public class GoldenCard extends PlayableCard{

    /**
     * The points given by the card.
     */
    private final int points;

    /**
     * The resources required to place the card.
     */
    private final ArrayList<Resource> requiredResource;

    /**
     * The rule the card gives points for.
     */
    private final Object rule;

    /**
     * The constructor creates a GoldenCard object by setting the permanent resources, the card corners, the points, the required resources, the rule and the UUID.
     *
     * @param permResource The permanent resources of the card.
     * @param cardCorners The corners of the card.
     * @param points The points given by the card.
     * @param requiredResource The resources required to place the card.
     * @param rule The rule the card gives points for.
     * @param UUID The UUID of the card, which identifies it uniquely.
     */
    public GoldenCard(Resource[] permResource, Corner[] cardCorners, int points, ArrayList<Resource> requiredResource, Object rule, String UUID) {
        super(permResource, cardCorners, UUID);
        this.points = points;
        this.requiredResource = requiredResource;
        this.rule = rule;
    }

    /**
     * Returns the points given by the card.
     *
     * @return The points given by the card.
     */
    @Override
    public int getPoints() {
        return points;
    }

    /**
     * Returns the resources required to place the card.
     *
     * @return The resources required to place the card.
     */
    public ArrayList<Resource> getRequiredResource() {
        return requiredResource;
    }

    /**
     * Returns the rule the card gives points for.
     *
     * @return The rule the card gives points for.
     */
    @Override
    public Object getRule() {
        return rule;
    }
}
