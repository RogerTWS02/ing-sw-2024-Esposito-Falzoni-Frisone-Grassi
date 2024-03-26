package it.polimi.ingsw.model;

public class Triplet {
    private final int position_X;
    private final int position_Y;
    private final Resource Resource;

    public Triplet(int positionX, int positionY, it.polimi.ingsw.model.Resource resource) {
        position_X = positionX;
        position_Y = positionY;
        Resource = resource;
    }


    public int position_X() {
        return position_X;
    }

    public int Position_Y (){
        return position_Y;
    }

    public Resource getResource() {
        return Resource;
    }
}
