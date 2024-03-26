package it.polimi.ingsw.model;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.HashMap;
import java.util.Map;

public class ResourcesGoalCard extends GoalCard{
    private final Map<Resource, Integer> resources;
    private final Map<Resource, Integer> countResources;

    /* how can we combine the fact that has a final attribute to the playerboard with the existence of common goalgard? */


    public ResourcesGoalCard(int points, Map<Resource, Integer> resources) {
        super(points);
        this.resources = new HashMap<>();
        this.countResources = new HashMap<>();
    }


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
