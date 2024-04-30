package it.polimi.ingsw.model;

public class ResourceCard extends PlayableCard {
    private final int points;

    public ResourceCard(Resource[] permResource, Corner[] cardCorners, int point, String UUID) {
        super(permResource, cardCorners, UUID);
        this.points = point;
    }

    public int getPoints() {
        return points;
    }
}
