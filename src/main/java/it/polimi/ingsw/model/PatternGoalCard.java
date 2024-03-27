package it.polimi.ingsw.model;
import java.util.ArrayList;
import java.util.Arrays;

public class PatternGoalCard extends GoalCard {
    private final int[] patternPosition;
    private final Resource[] patternResource;

    public PatternGoalCard(int points, int[] patternPosition, Resource[] patternResource) {
        super(points);
        this.patternPosition = patternPosition;
        this.patternResource = patternResource;
    }


    /*Yet to be thought how to check the pattern; method checkGoal is not finished*/
    /* iterate all the board */
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
                        if (!Arrays.asList(board.getCard(i + patternPosition[z], j + patternPosition[z+1]).getPermResource()).contains(patternResource[z])) {
                            break;
                        }
                        /* checks if the card has been already used for this pattern */
                        if (usedCard.contains(board.getCard(i + patternPosition[z], j + patternPosition[z+1]).getUUID())) {
                            break;
                        }
                        /* pattern matched add cards used to the list */
                        if (z == 2) {
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


