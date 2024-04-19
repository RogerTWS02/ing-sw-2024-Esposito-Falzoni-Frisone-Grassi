package it.polimi.ingsw.model;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Test
    public void checkGoal_correctInput_correctOutput(){
        Pawn r = null;
        List<Resource> resources = Arrays.asList(Resource.WOLF, Resource.LEAF, Resource.LEAF, Resource.FEATHER, Resource.BUTTERFLY, Resource.WOLF);

        PlayerBoard fakePlayerboard = new PlayerBoard(r){
            public List<Resource> getResources(){
                return resources;
            }
        };

        assertEquals(card.checkGoal(fakePlayerboard), 3);
    }

    @Test
    public void checkGoal_zeroPointsInput_correctOutput(){
        Pawn r = null;
        List<Resource> resources = Arrays.asList(Resource.WOLF, Resource.LEAF);

        PlayerBoard fakePlayerboard = new PlayerBoard(r){
            public List<Resource> getResources(){
                return resources;
            }
        };

        assertEquals(card.checkGoal(fakePlayerboard), 0);
    }

    @Test
    public void checkGoal_emptyResourcesListInput_correctOutput(){
        Pawn r = null;
        List<Resource> resources = Arrays.asList();

        PlayerBoard fakePlayerboard = new PlayerBoard(r){
            public List<Resource> getResources(){
                return resources;
            }
        };

        assertEquals(card.checkGoal(fakePlayerboard), 0);
    }

}
