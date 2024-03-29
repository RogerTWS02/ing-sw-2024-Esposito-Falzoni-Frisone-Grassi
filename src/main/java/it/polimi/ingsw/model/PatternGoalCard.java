package it.polimi.ingsw.model;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is a subclass of GoalCard, and particularly it includes all the cards
 * which have a pattern objective.
 */
public class PatternGoalCard extends GoalCard {
    private final int[] patternPosition;
    private final Resource[] patternResource;

    /**
     * This method constructs a PatternGoalCard.
     *
     * @param points the points the card gives for each objective
     * @param patternPosition the pattern positions we are looking for.
     * @param patternResource the pattern resources we are looking for (the resources identify the colors).
     */

    public PatternGoalCard(int points, int[] patternPosition, Resource[] patternResource) {
        super(points);
        this.patternPosition = patternPosition;
        this.patternResource = patternResource;
    }

    /**
     * This method iterates all the board, and it returns the points scored summing how many times
     * the pattern given is matched.
     *
     * @param board the board in which we are looking for the goal
     * @return an int of how many points the player has actually scored
     */

    @Override
    public int checkGoal(PlayerBoard board) {
        int timesMatched = 0;
        ArrayList<String> usedCard = new ArrayList<>();
        for (int i = 0; i <= 80; i++) {
            for (int j = 0; j <= 80; j++) {

                /*iterate al the cell in the grid as possible starting point of the pattern*/
                for (int z = 0; z < 6; z+=2) {
                    try {
                        /*          position null?                         */
                        if (board.getCard(i + patternPosition[z], j + patternPosition[z+1]) == null) {
                            break;
                        }
                        /* checks if the resource is the right one */
                        if (!Arrays.asList(board.getCard(i + patternPosition[z], j + patternPosition[z+1]).getPermResource()).contains(patternResource[z/2])) {
                            break;
                        }
                        /* checks if the card has been already used for this pattern */
                        if (usedCard.contains(board.getCard(i + patternPosition[z], j + patternPosition[z+1]).getUUID())) {
                            break;
                        }
                        /* pattern matched add cards used to the list */
                        if (z == 4) {
                            timesMatched++;
                            usedCard.add(board.getCard(i, j).getUUID());
                            usedCard.add(board.getCard(i + patternPosition[2], j + patternPosition[3]).getUUID());
                            usedCard.add(board.getCard(i + patternPosition[4], j + patternPosition[5]).getUUID());
                        }

                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }

                }
            }
        }

        return timesMatched * this.getPoints();
    }
}


