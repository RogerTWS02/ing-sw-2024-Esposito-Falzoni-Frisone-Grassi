package it.polimi.ingsw.model;

import java.io.Serializable;

public class GoldenCard extends PlayableCard{
    private final int points;
    private final Resource[] requiredResource;
    private final Object rule;

    public GoldenCard(Resource[] permResource, Corner[] cardCorners, int points, Resource[] requiredResource, Object rule, String UUID) {
        super(permResource, cardCorners, UUID);
        this.points = points;
        this.requiredResource = requiredResource;
        this.rule = rule;
    }

    public int getPoints() {
        return points;
    }

    public Resource[] getRequiredResource() {
        return requiredResource;
    }

    public Object getRule() {
        return rule;
    }
}
