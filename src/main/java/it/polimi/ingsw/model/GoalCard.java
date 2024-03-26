package it.polimi.ingsw.model;

public abstract class GoalCard {
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
