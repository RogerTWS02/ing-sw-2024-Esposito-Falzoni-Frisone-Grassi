package it.polimi.ingsw.model;

import java.io.Serializable;

public class StartingCard extends PlayableCard{
    private Corner[] FrontCardCorners;
    private Corner[] BackCardCorners;

    public StartingCard(Resource[] permResource, Corner[] FrontCardCorners, Corner[] BackCardCorners, String UUID) {
        super(permResource, null, UUID);
    }

    public Corner[] getFrontCardCorners() {
        return FrontCardCorners;
    }

    public Corner[] getBackCardCorners() {
        return BackCardCorners;
    }

    public void setFrontCardCorners(Corner[] FrontCardCorners) {
        this.FrontCardCorners = FrontCardCorners;
    }

    public void setBackCardCorners(Corner[] BackCardCorners) {
        this.BackCardCorners = BackCardCorners;
    }
}
