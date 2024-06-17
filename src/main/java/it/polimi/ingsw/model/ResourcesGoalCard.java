package it.polimi.ingsw.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the goal cards that require the player to have a certain amount of resources in oder to score points.
 */
public class ResourcesGoalCard extends GoalCard{

    /**
     * The resources required by the goal card in order to score points.
     */
    private final Map<Resource, Integer> resources;
    /**
     * The UUID of the goal card, which identifies it uniquely.
     */
    private final String UUID;

    /**
     * The constructor creates a goal card, given the points, the resources and the UUID of the card.
     *
     * @param points The points given by the goal card.
     * @param resources The resources required by the goal card in order to score points.
     * @param UUID The UUID of the goal card, which identifies it uniquely.
     */
    public ResourcesGoalCard(int points, Map<Resource, Integer> resources, String UUID) {
        super(points, UUID);
        this.resources = resources;
        this.UUID = UUID;
    }

    /**
     * Checks if the player has the required resources to score points with this goal card and how many times and returns the points scored.
     *
     * @param board The PlayerBoard to check the objective on.
     * @return The points scored by the player with this goal card.
     */
    @Override
    public int checkGoal(PlayerBoard board) {
        Map<Resource, Integer> countResources = new HashMap<>();
        /*Now I compare the resources in the resources array and check how many of them
        are there in the board*/
        countResources.clear();
        for (Resource r : board.getResources()) {
            countResources.put(r, countResources.getOrDefault(r, 0) + 1);
        }

        /*Now I have to count the points*/
        int minValue = resources.entrySet().stream()
                .mapToInt(entry -> countResources.getOrDefault(entry.getKey(), 0) / entry.getValue())
                .min()
                .orElse(0);

        return minValue * this.getPoints();
    }

    /**
     * Returns the resources required by the goal card.
     *
     * @return The resources required by the goal card.
     */
    public Map<Resource, Integer> getResourcesMap() {
        return resources;
    }
}
