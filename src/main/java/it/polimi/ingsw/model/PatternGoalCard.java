package it.polimi.ingsw.model;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is a subclass of GoalCard, and particularly it includes all the cards
 * which have a pattern objective.
 */
public class PatternGoalCard extends GoalCard {
    // array [0,0,x1,y1,x2,y2]  of relative position of the card in the pattern related to the first card
    private final int[] patternPosition;
   // array of resources, in the order in witch they are expected to be found
    private final Resource[] patternResource;
    private final String UUID;

    /**
     * This method constructs a PatternGoalCard.
     *
     * @param points the points the card gives for each objective
     * @param patternPosition the pattern positions we are looking for.
     * @param patternResource the pattern resources we are looking for (the resources identify the colors).
     */

    public PatternGoalCard(int points, int[] patternPosition, Resource[] patternResource, String UUID) {
        super(points, UUID);
        this.patternPosition = patternPosition;
        this.patternResource = patternResource;
        this.UUID = UUID;
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
        //iterate on all card in the matrix
        for (int i = 0; i < 81; i++) {
            for (int j = 0; j <81 ; j++) {

                /*iterate all the cell as starting card of a pattern, z+2 to iterate all pairs [x,y] */
                for (int z = 0; z < 6; z+=2) {
                    try {
                        /*   check if the position is null                         */
                        if (board.getCard(i + patternPosition[z], j + patternPosition[z+1]) == null) {
                            break;
                        }
                        /* check if the position is [0,0], because the starting card can not be used in a pattern */
                        if (i + patternPosition[z]==40 && j + patternPosition[z+1]==40){break;}

                        /* checks if the resource is the resource we expect in the pattern  */
                        if (!Arrays.asList(board.getCard(i + patternPosition[z], j + patternPosition[z+1]).getPermResource()).contains(patternResource[z/2])) {
                            break;
                        }
                        /* checks if the UUID of the card is in the array of card already used */
                        if (usedCard.contains(board.getCard(i + patternPosition[z], j + patternPosition[z+1]).getUUID())) {
                            break;
                        }
                        /* pattern matched no previous case matched, z is 4 so checked last card, add cards used to the list */
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


