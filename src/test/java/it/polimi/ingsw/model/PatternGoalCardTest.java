package it.polimi.ingsw.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class PatternGoalCardTest {

    GoalCard card = null;
    int[] patternPosition = null;
    Resource[] patternResources = null;
    @Before
    public void setUp(){
        patternResources= new Resource[3];
        patternResources[0]=Resource.WOLF;
        patternResources[1]=Resource.LEAF;
        patternResources[2]=Resource.MUSHROOM;
        patternPosition = new int[6];
        for (int x=0; x<3; x++){
            patternPosition[x]=x;
            patternPosition[x+1]=x;
        }
        card = new PatternGoalCard(3, patternPosition,patternResources, "GC_0");
    }
    @After
    public void tearDown(){card = null;}

    
    
     @Test
    public void checkGoal_correctInput_correctOutput(){

        Pawn r = null;
        Corner[] corners = new Corner[4];
        for(int x=0; x<4; x++){
            corners[x]= new Corner(1, null,null);
        }
        Resource[] resource1= new Resource[1];
        resource1[0]= Resource.MUSHROOM;
        ResourceCard prima= new ResourceCard(resource1,corners,3, "CG_1");
         Resource[] resource2 = new Resource[1];
        resource2[0]= Resource.WOLF;
         ResourceCard seconda= new ResourceCard(resource2,corners,3, "CG_2");
         Resource[] resource3= new Resource[1];
         resource3[0]= Resource.LEAF;
         ResourceCard terza= new ResourceCard(resource3,corners,3, "CG_3");
         Resource[] resource4 = new Resource[1];
         resource4[0]= Resource.BUTTERFLY;
         ResourceCard quarta= new ResourceCard(resource4,corners,3, "CG_4");

         StartingCard startingCard = new StartingCard(resource1, corners, corners,"SC_1");

         PlayerBoard fakePlayerboard = new PlayerBoard(r);
         fakePlayerboard.placeCard(startingCard,40,40);
         fakePlayerboard.placeCard(seconda,41,41);
         fakePlayerboard.placeCard(terza,42,42);
         fakePlayerboard.placeCard(prima,43,43);
         fakePlayerboard.placeCard(quarta,39,39);

        assertEquals(card.checkGoal(fakePlayerboard), 3);
    }


}
