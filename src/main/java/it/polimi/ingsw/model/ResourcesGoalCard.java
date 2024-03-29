package it.polimi.ingsw.model;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a GoalCard subclass which includes all the GoalCards which have an array of
 * resources as objective.
 */
public class ResourcesGoalCard extends GoalCard{
    private final Map<Resource, Integer> resources;
    private final Map<Resource, Integer> countResources;

    /* how can we combine the fact that has a final attribute to the playerboard with the existence of common goalgard? */


    /**
     * This is the constructor of these cards.
     *
     * @param points the points the card gives each time the player matches the objective
     * @param resources the objective to reach
     */
    public ResourcesGoalCard(int points, Map<Resource, Integer> resources) {
        super(points);
        this.resources = new HashMap<>();
        this.countResources = new HashMap<>();
    }


    /**
     * This method is an override of the previous method in GoalCard.
     * It checks whether in the board the pattern is matched and how many times it is reached.
     *
     * @param board the board in which we are looking for the goal
     * @return the points the player has actually scored
     */
    @Override
    public int checkGoal(PlayerBoard board) {

        /*Now I compare the resources in the resources array and check how many of them
        are there in the board*/

        for (Resource r : board.getResources()) {
            this.countResources.put(r, this.countResources.get(r) + 1);
        }

        /*Now I have to count the points*/

        resources.forEach((r, i) -> i = countResources.get(r)/i);
        return resources.values().stream()
                .mapToInt(v -> v * this.getPoints())
                .min()
                .orElse(0);
    }
}
