package ing.soft.model;

public class Pattern {

    private final Resource firstResource;
    private final int[] secondPosition;
    private final Resource secondResource;
    private final int[] thirdPosition;
    private final Resource thirdResource;


    public Pattern(Resource firstResource, int secondPosition, Resource secondResource, int thirdPosition, Resource thirdResource) {
        this.firstResource = firstResource;
        this.secondPosition = new int[]{secondPosition};
        this.secondResource = secondResource;
        this.thirdPosition = new int[]{thirdPosition};
        this.thirdResource = thirdResource;
    }

    public Resource getFirstResource() {
        return firstResource;
    }

    public int[] getSecondPosition() {
        return secondPosition;
    }

    public Resource getSecondResource() {
        return secondResource;
    }

    public int[] getThirdPosition() {
        return thirdPosition;
    }

    public Resource getThirdResource() {
        return thirdResource;
    }
}
