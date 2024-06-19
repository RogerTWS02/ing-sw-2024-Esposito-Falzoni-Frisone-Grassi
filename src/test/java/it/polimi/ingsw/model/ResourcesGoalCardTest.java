package it.polimi.ingsw.model;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Unit tests for ResourcesGoalCard class.
 */
public class ResourcesGoalCardTest {
    GoalCard card = null;
    Map<Resource, Integer> requiredResources = null;

    @Before
    public void setUp(){
        requiredResources = new HashMap<>();
        requiredResources.put(Resource.WOLF, 2);
        requiredResources.put(Resource.LEAF, 1);

        card = new ResourcesGoalCard(3, requiredResources, "GC_1");
    }

    @After
    public void tearDown(){card = null;}

    /**
     * Checks if the points for reaching the goal are calculated correctly.
     */
    @Test
    public void checkGoal_correctOutput(){
        Pawn r = null;
        ArrayList<Resource> resources = new ArrayList<>();
        resources.add(Resource.WOLF);
        resources.add(Resource.LEAF);
        resources.add(Resource.LEAF);
        resources.add(Resource.FEATHER);
        resources.add(Resource.BUTTERFLY);
        resources.add(Resource.WOLF);
        PlayerBoard fakePlayerboard = new PlayerBoard(r){
            public ArrayList<Resource> getResources(){
                return resources;
            }
        };
        assertEquals(card.checkGoal(fakePlayerboard), 3);
    }

    /**
     * Checks if the points for reaching the goal are calculated correctly int he case the player has not reached the goal.
     */
    @Test
    public void checkGoal_zeroPointsInput_correctOutput(){
        Pawn r = null;
        ArrayList<Resource> resources = new ArrayList<>();
        resources.add(Resource.WOLF);
        resources.add(Resource.LEAF);

        PlayerBoard fakePlayerboard = new PlayerBoard(r){
            public ArrayList<Resource> getResources(){
                return (ArrayList<Resource>) resources;
            }
        };
        assertEquals(card.checkGoal(fakePlayerboard), 0);
    }

    /**
     * Checks if the points for reaching the goal are calculated correctly in the case the player has not reached the goal, having no resources in the player board.
     */
    @Test
    public void checkGoal_emptyResourcesListInput_correctOutput(){
        Pawn r = null;
        ArrayList<Resource> resources = new ArrayList<>();
        PlayerBoard fakePlayerboard = new PlayerBoard(r){
            public ArrayList<Resource> getResources(){
                return resources;
            }
        };
        assertEquals(card.checkGoal(fakePlayerboard), 0);
    }

}
