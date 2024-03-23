package ing.soft.model;

import java.util.Map;

public class PatternGoalCard extends GoalCard{
    private Map<int[],Resource> pattern;

    public PatternGoalCard(int points, Map<int[], Resource> pattern) {
        super(points);
        this.pattern = pattern;
    }

    /*Yet to be thought how to check the pattern; method checkGoal is not finished*/

    public boolean checkGoal(int[] coordinates){
        return true;
    }
}