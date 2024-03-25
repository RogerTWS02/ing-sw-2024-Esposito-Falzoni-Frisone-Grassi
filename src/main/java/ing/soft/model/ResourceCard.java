package ing.soft.model;

public class ResourceCard extends PlayableCard {
    private final int points;


    public ResourceCard(Resource[] permResource, Corner[] cardCorners, int point, int UUID) {
        super(permResource, cardCorners, UUID);
        this.points = point;
    }

    public int getPoint() {
        return points;
    }
}
