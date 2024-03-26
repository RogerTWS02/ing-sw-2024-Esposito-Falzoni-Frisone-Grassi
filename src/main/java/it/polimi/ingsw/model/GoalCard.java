package it.polimi.ingsw.model;

import java.io.Serializable;

public abstract class GoalCard implements Serializable {
    private final int points;

    public GoalCard(int points) {
        this.points = points;
    }

    public int getPoints(){
        return points;
   }

    public int checkGoal(PlayerBoard board){
        return 0;
    }
}
