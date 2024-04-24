package it.polimi.ingsw.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class PlayerBoardTest {

    Resource[] permanentResources = null;
    Corner[] corners = null;
    PlayerBoard board = null;
    PlayableCard card = null;
    @Before
    public void setUp(){
        Pawn r = Pawn.BLUE;
        board = new PlayerBoard(r);
        permanentResources = new Resource[1];
        permanentResources[0] = Resource.WOLF;
        corners = new Corner[4];
        corners[0] = null;
        corners[1] = null;
        corners[2] = null;
        corners[3] = null;
        card = new ResourceCard(permanentResources, corners, 2, "GC_1");
    }

    @After
    public void tearDown(){
        card = null;
        board = null;
        corners = null;
        permanentResources = null;
    }

    @Test
    public void placeCard_placein00_returncorrectcard(){
        board.placeCard(card, 0,0);

        assertEquals(card, board.getCard(0,0));
    }

    @Test
    public void placeCard_unavailablestate_correctOutput(){
        board.placeCard(card, 0,0);
        assertEquals(State.UNAVAILABLE, board.getState(1,1));
    }

    @Test
    public void placeCard_occupiedposition_correctOutput(){
        board.placeCard(card, 0,0);
        assertEquals(State.OCCUPIED, board.getState(0,0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void placeCard_unavailablePositioning_launchesException(){
        board.placeCard(card, 0,0);
        PlayableCard card2 = new ResourceCard(permanentResources, corners, 2, "GC_2");
        board.placeCard(card2, 1,1);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void placeCard_outOfTheGrid1_launchesException(){
        board.placeCard(card, 0,-1);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void placeCard_outOfTheGrid2_launchesException(){
        board.placeCard(card, 84,-1);
    }

    @Test
    public void placeCard_correctInput_availableState(){

        Corner[] corners2 = new Corner[4];
        corners2[0] = null;
        corners2[1] = null;
        corners2[2] = null;
        corners2[3] = new Corner(3, card, null);
        PlayableCard card2 = new ResourceCard(permanentResources, corners2, 2, "GC_2");
        board.placeCard(card2, 10,10);
        assertEquals(State.AVAILABLE, board.getState(11,11));
    }

}
