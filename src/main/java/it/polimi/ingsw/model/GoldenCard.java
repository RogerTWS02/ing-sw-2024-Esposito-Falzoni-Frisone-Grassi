package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.ArrayList;

public class GoldenCard extends PlayableCard{
    private final int points;
    private final ArrayList<Resource> requiredResource;
    private final Object rule;

    public GoldenCard(Resource[] permResource, Corner[] cardCorners, int points, ArrayList<Resource> requiredResource, Object rule, String UUID) {
        super(permResource, cardCorners, UUID);
        this.points = points;
        this.requiredResource = requiredResource;
        this.rule = rule;
    }

    public int getPoints() {
        return points;
    }

    public ArrayList<Resource> getRequiredResource() {
        return requiredResource;
    }

    public Object getRule() {
        return rule;
    }
}
