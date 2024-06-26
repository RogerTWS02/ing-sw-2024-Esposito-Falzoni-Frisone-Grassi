package it.polimi.ingsw.model;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represents the PatternGoalCard, a type of GoalCard that checks if a certain pattern is matched on the board.
 */
public class PatternGoalCard extends GoalCard {
    // array [0,0,x1,y1,x2,y2]  of relative position of the card in the pattern related to the first card
    /**
     * The relative position of the card in the pattern related to the card.
     */
    private final int[] patternPosition;
   // array of resources, in the order in witch they are expected to be found
    /**
     * The resources that are expected to be found in the pattern.
     */
    private final Resource[] patternResource;


    /**
     * The constructor of the class, it creates a new PatternGoalCard with the given parameters.
     *
     * @param points The points that the player will get if the pattern is matched.
     * @param patternPosition The relative position of the card in the pattern related to the card.
     * @param patternResource The resources that are expected to be found in the pattern.
     * @param UUID The UUID of the card, which identifies it uniquely.
     */
    public PatternGoalCard(int points, int[] patternPosition, Resource[] patternResource, String UUID) {
        super(points, UUID);
        this.patternPosition = patternPosition;
        this.patternResource = patternResource;

    }

    /**
     * Returns the points scored summing how many times the card pattern is matched.
     *
     * @param board The player board to check the goal on.
     * @return The points scored by the player.
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

                        /*   check if the position is null
                                           */
                        if (board.getCard(i + patternPosition[z], j + patternPosition[z+1]) == null) {
                            //System.out.println("esco per posizione non valida");
                            break;
                        };
                        /* check if the position is [0,0], because the starting card can not be used in a pattern */
                        if (i + patternPosition[z]==40 && j + patternPosition[z+1]==40){
                            //System.out.println("esco per starting card");
                            break;}

                        /* checks if the resource is the resource we expect in the pattern  */
                        if (!Arrays.asList(board.getCard(i + patternPosition[z], j + patternPosition[z+1]).getPermResource()).contains(patternResource[z/2])) {
                            //System.out.println("esco per risorsa non valida");
                            break;
                        }
                        /* checks if the UUID of the card is in the array of card already used */
                        if (usedCard.contains(board.getCard(i + patternPosition[z], j + patternPosition[z+1]).getUUID())) {
                            //System.out.println("esco per carta already used");
                            break;
                        }
                        /* pattern matched no previous case matched, z is 4 so checked last card, add cards used to the list */
                        if (z == 4) {
                            //System.out.println("esco per matched");
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

    /**
     * Returns the pattern of the card.
     *
     * @return The pattern of the card.
     */
    public int[] getPatternPosition() {
        return patternPosition;
    }

    /**
     * Returns the resources that are expected to be found in the pattern.
     *
     * @return The array of resources that are expected to be found in the pattern.
     */
    public Resource[] getPatternResource() {
        return patternResource;
    }
}


