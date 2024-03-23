package ing.soft.model;

public class ResourceCard extends PlayableCard {
    private final int points;


    public ResourceCard(Resource[] permResource, Corner[] cardCorners, int point) {
        super(permResource, cardCorners);
        this.points = point;
    }

    public int getPoint() {
        return points;
    }
}
