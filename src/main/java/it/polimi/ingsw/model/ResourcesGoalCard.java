package it.polimi.ingsw.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the goal cards that require the player to have a certain amount of resources in oder to score points.
 */
public class ResourcesGoalCard extends GoalCard{
    private final Map<Resource, Integer> resources;
    private final Map<Resource, Integer> countResources;
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
            this.countResources.put(r, this.countResources.getOrDefault(r, 0) + 1);
        }

        /*Now I have to count the points*/
        int minValue = resources.entrySet().stream()
                .mapToInt(entry -> countResources.getOrDefault(entry.getKey(), 0) / entry.getValue())
                .min()
                .orElse(0);

        return minValue * this.getPoints();
    }

    // Getters
    public Map<Resource, Integer> getResourcesMap() {
        return resources;
    }

    public Map<Resource, Integer> getCountResources() {
        return countResources;
    }
}
