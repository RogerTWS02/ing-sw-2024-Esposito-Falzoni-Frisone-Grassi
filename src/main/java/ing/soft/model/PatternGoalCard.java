package ing.soft.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class PatternGoalCard extends GoalCard {
    private Pattern pattern;

    public PatternGoalCard(int points, Pattern pattern) {
        super(points);
        this.pattern = pattern;
    }

    /*Yet to be thought how to check the pattern; method checkGoal is not finished*/
 /* iterate all the board */
    public int checkGoal(Playerboard board) {
        int timesMatched=0;
       ArrayList<Integer> usedcard = new ArrayList<Integer>();
        for (int i = 0; i <= 80; i++) {
            for (int j = 0; j <= 80; j++) {
                int[] position= new int[2];
                position[0]=i;
                position[1]=j;

                /*it`s the starting point of a pattern */
                if (matchedPattern(position,pattern, board, usedcard)) {timesMatched+=1;}
                }
            }

        return 0;
    }

    /* check if the pattern is matched, it can surly be done in a recursive way, ned more time to elaborate the method,
    now it`s should just work not optimal solution, (arraylist passed by reference?)
     */
    private boolean matchedPattern(int[] startingPoint, Pattern pattern, Playerboard board, ArrayList usedcard) {
       /*
       just checking every conditions that mismatch the pattern with a brute force algorithm
        */
    /* same resource? */
        if (board.getCard(startingPoint[0], startingPoint[1]).getPermResource() != pattern.getFirstResource()) {
            return false;
        }
    /* already visited? */
        if(usedcard.contains(board.getCard(startingPoint[0], startingPoint[1]).getUUID())){return false;}
        usedcard.add(usedcard.contains(board.getCard(startingPoint[0], startingPoint[1]).getUUID()));

      /* next card in the pattern */
        startingPoint[0] = startingPoint[0] + pattern.getSecondPosition()[0];
        startingPoint[1] = startingPoint[1] + pattern.getSecondPosition()[1];

        if (board.getCard(startingPoint[0], startingPoint[1]).getPermResource() != pattern.getSecondResource()) {
            return false;
        }
        if(usedcard.contains(board.getCard(startingPoint[0], startingPoint[1]).getUUID())){return false;}
        usedcard.add(usedcard.contains(board.getCard(startingPoint[0], startingPoint[1]).getUUID()));

        /* next card in the pattern */

        startingPoint[0] = startingPoint[0] + pattern.getThirdPosition()[0];
        startingPoint[1] = startingPoint[1] + pattern.getThirdPosition()[1];

        if (board.getCard(startingPoint[0], startingPoint[1]).getPermResource() != pattern.getThirdResource()) {
            return false;
        }
        if(usedcard.contains(board.getCard(startingPoint[0], startingPoint[1]).getUUID())){return false;}
        usedcard.add(usedcard.contains(board.getCard(startingPoint[0], startingPoint[1]).getUUID()));
        return true;
    };



}




