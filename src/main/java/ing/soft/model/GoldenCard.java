package ing.soft.model;

public class GoldenCard extends PlayableCard {
    private final int points;
    private final Resource[] requiredResource;

    public GoldenCard(Resource[] permResource, Corner[] cardCorners, int points, Resource[] requiredResource, int UUID) {
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
