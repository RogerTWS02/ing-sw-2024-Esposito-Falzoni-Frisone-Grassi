package it.polimi.ingsw.model;

import java.io.Serializable;

public class GoldenCard extends PlayableCard{
    private final int points;
    private final Resource[] requiredResource;

    public GoldenCard(Resource[] permResource, Corner[] cardCorners, int points, Resource[] requiredResource, Resource rule, String UUID) {
        super(permResource, cardCorners, UUID);
        this.points = points;
        this.requiredResource = requiredResource;
    }

    public int getPoints() {
        return points;
    }

    public Resource[] getRequiredResource() {
        return requiredResource;
    }
}
