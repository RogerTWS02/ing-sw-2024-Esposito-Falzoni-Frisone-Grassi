package it.polimi.ingsw.model;

import java.util.Map;

public class GoldenCard extends PlayableCard {
    private final int points;
    private final Map<Resource, Integer > requiredResource;

    public GoldenCard(Resource[] permResource, Corner[] cardCorners, int points, Map<Resource, Integer > requiredResource, int UUID) {
        super(permResource, cardCorners, UUID);
        this.points = points;
        this.requiredResource = requiredResource;
    }

    public int getPoints() {
        return points;
    }

    public Map<Resource, Integer > getRequiredResource() {
        return requiredResource;
    }
}
