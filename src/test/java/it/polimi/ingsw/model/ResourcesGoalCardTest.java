package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameController;
import org.json.simple.JSONObject;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Unit tests for ResourcesGoalCard class.
 */
public class ResourcesGoalCardTest {
    GameController gameController;
    Game game;
    GoalCard goalCard;
    JSONObject JSONCard;
    PlayableCard card;

    @Before
    public void setUp() {
        gameController = new GameController(123);
        game = gameController.getCurrentGame();
        game.setPlayers(createFakePlayers()[0]);
        game.getPlayers().get(0).setPawn(Pawn.BLUE);
    }

    //Creates an array of array lists: the first one contains 4 players, the second one contains 3 players and one null value, the third one is empty
    public ArrayList<Player>[] createFakePlayers(){
        ArrayList<Player>[] playersLists = new ArrayList[4];
        playersLists[0] = new ArrayList<>();
        playersLists[1] = new ArrayList<>();
        playersLists[2] = null;
        playersLists[3] = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            playersLists[0].add(new Player("Player" + i, 0));
        }
        for(int i = 0; i < 3; i++){
            playersLists[1].add(new Player("Player" + i, 0));
        }
        playersLists[1].add(null);
        return playersLists;
    }

    @After
    public void tearDown() {
        this.game = null;
        goalCard = null;
        JSONCard = null;
        card = null;
    }

    /**
     * Checks the checkGoal method in the case of zero points scored.
     */
    @Test
    public void checkGoal_test_1() {
        do {
            goalCard = gameController.drawGoalFromDeck();
        } while(goalCard instanceof ResourcesGoalCard);
        assertEquals(0, goalCard.checkGoal(game.getPlayers().get(0).getPlayerBoard()));
    }

    /**
     * Checks the checkGoal method in case of reaching the goal one time with an "exact" number of resource occurrences.
     */
    @Test
    public void checkGoal_test_2() throws IllegalAccessException {
        JSONCard = (JSONObject) game.resourcesGoalDeck.get(3);
        goalCard = gameController.craftResourcesGoalCard(JSONCard);
        JSONCard = (JSONObject) game.startingDeck.get(0);
        card = gameController.craftStartingCard(JSONCard);
        gameController.placeCard(40, 40, card, game.getPlayers().get(0));
        JSONCard = (JSONObject) game.resourceDeck.get(21);
        card = gameController.craftResourceCard(JSONCard);
        gameController.placeCard(41, 39, card, game.getPlayers().get(0));
        JSONCard = (JSONObject) game.resourceDeck.get(25);
        card = gameController.craftResourceCard(JSONCard);
        gameController.placeCard(39, 39, card, game.getPlayers().get(0));
        assertEquals(2, goalCard.checkGoal(game.getPlayers().get(0).getPlayerBoard()));
    }

    /**
     * Checks the checkGoal method in case of reaching the goal one time without an "exact" number of resource occurrences.
     */
    @Test
    public void checkGoal_test_3() throws IllegalAccessException {
        JSONCard = (JSONObject) game.resourcesGoalDeck.get(3);
        goalCard = gameController.craftResourcesGoalCard(JSONCard);
        JSONCard = (JSONObject) game.startingDeck.get(0);
        card = gameController.craftStartingCard(JSONCard);
        gameController.placeCard(40, 40, card, game.getPlayers().get(0));
        JSONCard = (JSONObject) game.resourceDeck.get(21);
        card = gameController.craftResourceCard(JSONCard);
        gameController.placeCard(41, 39, card, game.getPlayers().get(0));
        gameController.placeCard(39, 39, card, game.getPlayers().get(0));
        assertEquals(2, goalCard.checkGoal(game.getPlayers().get(0).getPlayerBoard()));
    }

    /**
     * Checks the checkGoal method in case of reaching the goal more than one time.
     */
    @Test
    public void checkGoal_test_4() throws IllegalAccessException {
        JSONCard = (JSONObject) game.resourcesGoalDeck.get(3);
        goalCard = gameController.craftResourcesGoalCard(JSONCard);
        JSONCard = (JSONObject) game.startingDeck.get(0);
        card = gameController.craftStartingCard(JSONCard);
        gameController.placeCard(40, 40, card, game.getPlayers().get(0));
        JSONCard = (JSONObject) game.resourceDeck.get(21);
        card = gameController.craftResourceCard(JSONCard);
        gameController.placeCard(41, 39, card, game.getPlayers().get(0));
        gameController.placeCard(39, 39, card, game.getPlayers().get(0));
        gameController.placeCard(38, 40, card, game.getPlayers().get(0));
        assertEquals(4, goalCard.checkGoal(game.getPlayers().get(0).getPlayerBoard()));
    }
}
