package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 *The abstract class GoalCard represents those cards which have an amount of points for each
 * time the objective is reached.
 */
public abstract class GoalCard implements Serializable {
    private final String UUID;
    private final int points;

    /**
     * Builder of a new GoalCard.
     *
     * @param points the points associated to the card
     */
    public GoalCard(int points, String UUID) {
        this.points = points;
        this.UUID = UUID;
    }

    /**
     * This method returns the points a card gives for each objective.
     *
     * @return points
     */
    public int getPoints(){
        return points;
   }

    /**
     * This is the function that actually checks how many points the player has scored
     * counting how many objectives he has reached.
     *
     * @param board the board in which we are looking for the goal
     * @return 0 this is a standard returning, in the subclasses it is overridden, returning the points scored.
     */
    public int checkGoal(PlayerBoard board){
        return 0;
    }

    //UUID getter
    public String getUUID() {
        return UUID;
    }
}
