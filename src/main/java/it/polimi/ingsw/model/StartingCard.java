package it.polimi.ingsw.model;

import java.io.Serializable;

public class StartingCard extends PlayableCard{
    public StartingCard(Resource[] permResource, Corner[] cardCorners, int UUID) {

        super(permResource, cardCorners, UUID);
    }
}
