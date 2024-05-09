package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * The abstract class GoalCard is the superclass of the GoalCards, which are the cards that give points for the objectives.

 */
public abstract class GoalCard implements Serializable {
    private final String UUID;
    private final int points;

    /**
     * The constructor creates a GoalCard object, setting the points and the UUID.
     *
     * @param points the points the card gives for the objective.
     * @param UUID the UUID of the card, which identifies it uniquely.
     */
    public GoalCard(int points, String UUID) {
        this.points = points;
        this.UUID = UUID;
    }

    /**
     * Returns the points the GoalCard gives for the objective.
     *
     * @return the points the GoalCard gives for the objective.
     */
    public int getPoints(){
        return points;
   }

    /**
     * Overriden by the subclasses, checks if the GoalCard relative objective is reached.
     *
     * @param board the PlayerBoard to check the objective on.
     * @return the points the GoalCard gives for the objective.
     */
    public int checkGoal(PlayerBoard board){
        return 0;
    }

    /**
     * Returns the UUID of the GoalCard, which identifies it uniquely.
     *
     * @return the UUID of the GoalCard.
     */
    public String getUUID() {
        return UUID;
    }
}
