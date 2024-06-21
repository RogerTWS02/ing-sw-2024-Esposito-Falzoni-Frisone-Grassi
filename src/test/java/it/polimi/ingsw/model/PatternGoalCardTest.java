package it.polimi.ingsw.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for PatternGoalCard class.
 */
public class PatternGoalCardTest {

    GoalCard card = null;
    int[] patternPosition = null;
    Resource[] patternResources = null;
    @Before
    public void setUp(){
        patternResources= new Resource[3];
        patternResources[0]=Resource.WOLF;
        patternResources[1]=Resource.LEAF;
        patternResources[2]=Resource.WOLF;
        patternPosition = new int[6];
        for (int x=0; x<3; x++){
            patternPosition[2*x]=x;
            patternPosition[2*x+1]=x;
        }
        card = new PatternGoalCard(2, patternPosition,patternResources, "GC_0");

    }
    @After
    public void tearDown(){card = null;}

    /**
     * Checks if the points for reaching the goal are calculated correctly.
     */
     @Test
    public void checkGoalcorrectOutput(){

        Pawn r = null;
        Corner[] corners = new Corner[4];
        for(int x=0; x<4; x++){
            corners[x]= new Corner(x, null,null);
        }
         Resource[] resourceWolf= new Resource[1];
         resourceWolf[0]= Resource.WOLF;
         PlayableCard prima= new ResourceCard(resourceWolf,corners,3, "CG_1");
         PlayableCard seconda= new ResourceCard(resourceWolf,corners,3, "CG_2");
         PlayableCard quinta= new ResourceCard(resourceWolf,corners,3, "CG_5");
         Resource[] resourceLeaf= new Resource[1];
         resourceLeaf[0]= Resource.LEAF;
         PlayableCard terza= new ResourceCard(resourceLeaf,corners,3, "CG_3");
         PlayableCard quarta= new ResourceCard(resourceLeaf,corners,3, "CG_4");
         Resource[] resourceButterfly= new Resource[1];
         resourceButterfly[0]= Resource.BUTTERFLY;
         PlayableCard sesta= new ResourceCard(resourceButterfly,corners,3, "CG_4");

         StartingCard startingCard = new StartingCard(resourceWolf, corners, corners,"SC_1");

         PlayerBoard fakePlayerboard = new PlayerBoard(r);
         fakePlayerboard.placeCard(startingCard,40,40);
         fakePlayerboard.placeCard(seconda,41,41);
         fakePlayerboard.placeCard(quinta,42,42);
         fakePlayerboard.placeCard(terza,43,43);
         fakePlayerboard.placeCard(prima,44,44);
         fakePlayerboard.placeCard(sesta,39,39);

        assertEquals(card.checkGoal(fakePlayerboard), 2);
    }

    @Test
    public void checkGoal_dont_count_twice_the_same_card(){

        Pawn r = null;
        Corner[] corners = new Corner[4];
        for(int x=0; x<4; x++){
            corners[x]= new Corner(x, null,null);
        }
        Resource[] resourceWolf= new Resource[1];
        resourceWolf[0]= Resource.WOLF;
        PlayableCard prima= new ResourceCard(resourceWolf,corners,3, "CG_1");
        PlayableCard seconda= new ResourceCard(resourceWolf,corners,3, "CG_2");
        PlayableCard quinta= new ResourceCard(resourceWolf,corners,3, "CG_5");
        Resource[] resourceLeaf= new Resource[1];
        resourceLeaf[0]= Resource.LEAF;
        PlayableCard terza= new ResourceCard(resourceLeaf,corners,3, "CG_3");
        PlayableCard quarta= new ResourceCard(resourceLeaf,corners,3, "CG_4");

        StartingCard startingCard = new StartingCard(resourceWolf, corners, corners,"SC_1");

        PlayerBoard fakePlayerboard = new PlayerBoard(r);
        fakePlayerboard.placeCard(startingCard,40,40);
        fakePlayerboard.placeCard(seconda,41,41);
        fakePlayerboard.placeCard(terza,42,42);
        fakePlayerboard.placeCard(prima,43,43);
        fakePlayerboard.placeCard(quarta,44,44);
        fakePlayerboard.placeCard(quinta,45,45);

        assertEquals(card.checkGoal(fakePlayerboard), 2);
    }

    @Test
    public void checkGoal_correctly_count_occurencies(){

        Pawn r = null;
        Corner[] corners = new Corner[4];
        for(int x=0; x<4; x++){
            corners[x]= new Corner(x, null,null);
        }
        Resource[] resourceWolf= new Resource[1];
        resourceWolf[0]= Resource.WOLF;
        PlayableCard prima= new ResourceCard(resourceWolf,corners,3, "CG_1");
        PlayableCard seconda= new ResourceCard(resourceWolf,corners,3, "CG_2");
        PlayableCard quinta= new ResourceCard(resourceWolf,corners,3, "CG_5");
        PlayableCard settima= new ResourceCard(resourceWolf,corners,3, "CG_7");
        PlayableCard ottava= new ResourceCard(resourceWolf,corners,3, "CG_8");
        Resource[] resourceLeaf= new Resource[1];
        resourceLeaf[0]= Resource.LEAF;
        PlayableCard terza= new ResourceCard(resourceLeaf,corners,3, "CG_3");
        PlayableCard quarta= new ResourceCard(resourceLeaf,corners,3, "CG_4");

        StartingCard startingCard = new StartingCard(resourceWolf, corners, corners,"SC_1");

        PlayerBoard fakePlayerboard = new PlayerBoard(r);
        fakePlayerboard.placeCard(startingCard,40,40);
        fakePlayerboard.placeCard(seconda,41,41);
        fakePlayerboard.placeCard(terza,42,42);
        fakePlayerboard.placeCard(prima,43,43);
        fakePlayerboard.placeCard(quinta,42,41);
        fakePlayerboard.placeCard(quarta,43,42);
        fakePlayerboard.placeCard(settima,44,43);

        assertEquals(card.checkGoal(fakePlayerboard), 4);
    }

    @Test
    public void checkGoal_dont_count_opposite_diagonal(){

        Pawn r = null;
        Corner[] corners = new Corner[4];
        for(int x=0; x<4; x++){
            corners[x]= new Corner(x, null,null);
        }
        Resource[] resourceWolf= new Resource[1];
        resourceWolf[0]= Resource.WOLF;
        PlayableCard prima= new ResourceCard(resourceWolf,corners,3, "CG_1");
        PlayableCard seconda= new ResourceCard(resourceWolf,corners,3, "CG_2");
        PlayableCard quinta= new ResourceCard(resourceWolf,corners,3, "CG_5");
        Resource[] resourceLeaf= new Resource[1];
        resourceLeaf[0]= Resource.LEAF;
        PlayableCard terza= new ResourceCard(resourceLeaf,corners,3, "CG_3");
        PlayableCard quarta= new ResourceCard(resourceLeaf,corners,3, "CG_4");

        StartingCard startingCard = new StartingCard(resourceWolf, corners, corners,"SC_1");

        PlayerBoard fakePlayerboard = new PlayerBoard(r);
        fakePlayerboard.placeCard(startingCard,40,40);
        fakePlayerboard.placeCard(seconda,41,41);
        fakePlayerboard.placeCard(terza,40,42);
        fakePlayerboard.placeCard(prima,39,43);

        assertEquals(card.checkGoal(fakePlayerboard), 0);
    }





}
