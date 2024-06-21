package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameController;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Unit test for the Game class.
 */
public class GameTest {
    GameController gameController;
    Game game;

    @Before
    public void setUp() {
        gameController = new GameController(123);
        game = gameController.getCurrentGame();
        game.setPlayers(createFakePlayers()[0]);
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
    }

    /**
     * Checks if the winner of a game is correctly calculated.
     */
    @Test
    public void getWinner_test_1() {
        ArrayList<Player> players = createFakePlayers()[0];
        game.setPlayers(players);
        for(int i = 0; i < 2; i++)
            game.commonGoalCards[i] = gameController.drawGoalFromDeck();
        for(int i = 0; i < 4; i++) {
            Object[] myCards = new Object[3];
            myCards[0] = new Object();
            GoalCard[] temp = gameController.drawGoalCardsToChoose();
            myCards[1] = temp[0];
            myCards[2] = temp[1];
            game.getPlayers().get(i).setCardToChoose(myCards);
            GoalCard card = (GoalCard) game.getPlayers().get(i).getCardToChoose()[1];
            game.getPlayers().get(i).setSecretGoalCard(card.getUUID());
        }
        players.get(0).setPawn(Pawn.BLUE);
        players.get(1).setPawn(Pawn.GREEN);
        players.get(2).setPawn(Pawn.YELLOW);
        players.get(3).setPawn(Pawn.RED);
        players.get(0).setScore(10);
        players.get(1).setScore(5);
        players.get(2).setScore(3);
        players.get(3).setScore(24);
        assertEquals(players.get(3), game.getWinner()[0]);
        assertEquals(1, game.getWinner().length);
    }

    /**
     * Checks if the winner of a game is correctly calculated in the case of a draw and zero goals achieved.
     */
    @Test
    public void getWinner_test_2() {
        ArrayList<Player> players = createFakePlayers()[0];
        game.setPlayers(players);
        for(int i = 0; i < 2; i++)
            game.commonGoalCards[i] = gameController.drawGoalFromDeck();
        for(int i = 0; i < 4; i++) {
            Object[] myCards = new Object[3];
            myCards[0] = new Object();
            GoalCard[] temp = gameController.drawGoalCardsToChoose();
            myCards[1] = temp[0];
            myCards[2] = temp[1];
            game.getPlayers().get(i).setCardToChoose(myCards);
            GoalCard card = (GoalCard) game.getPlayers().get(i).getCardToChoose()[1];
            game.getPlayers().get(i).setSecretGoalCard(card.getUUID());
        }
        players.get(0).setPawn(Pawn.BLUE);
        players.get(1).setPawn(Pawn.GREEN);
        players.get(2).setPawn(Pawn.YELLOW);
        players.get(3).setPawn(Pawn.RED);
        players.get(0).setScore(24);
        players.get(1).setScore(5);
        players.get(2).setScore(3);
        players.get(3).setScore(24);
        assert(game.getWinner()[0] == players.get(0) || game.getWinner()[0] == players.get(3));
        assert(game.getWinner()[1] == players.get(0) || game.getWinner()[1] == players.get(3));
        assertNotSame(game.getWinner()[0], game.getWinner()[1]);
        assertEquals(2, game.getWinner().length);
    }

    /**
     * Checks if the winner of a game is correctly calculated in the case of a draw and != zero goals achieved.
     */
    @Test
    public void getWinner_test_3() throws IllegalAccessException {
        ArrayList<Player> players = createFakePlayers()[0];
        game.setPlayers(players);
        players.get(0).setPawn(Pawn.BLUE);
        players.get(1).setPawn(Pawn.GREEN);
        players.get(2).setPawn(Pawn.YELLOW);
        players.get(3).setPawn(Pawn.RED);
        players.get(0).setScore(24);
        players.get(1).setScore(5);
        players.get(2).setScore(3);
        players.get(3).setScore(24);
        JSONObject JSONCard = (JSONObject) game.patternGoalDeck.get(0);
        GoalCard card = gameController.craftPatternGoalCard(JSONCard);
        for(int i = 0; i < 2; i++)
            game.commonGoalCards[i] = card;
        JSONCard = (JSONObject) game.resourcesGoalDeck.get(0);
        card = gameController.craftResourcesGoalCard(JSONCard);
        for(int i = 0; i < 4; i++) {
            Object[] myCards = new Object[3];
            myCards[0] = new Object();
            GoalCard[] temp = gameController.drawGoalCardsToChoose();
            myCards[2] = temp[0];
            myCards[1] = card;
            game.getPlayers().get(i).setCardToChoose(myCards);
            game.getPlayers().get(i).setSecretGoalCard(card.getUUID());
        }
        JSONCard = (JSONObject) game.startingDeck.get(1);
        PlayableCard card_2 = gameController.craftStartingCard(JSONCard);
        gameController.placeCard(40, 40, card_2, game.getPlayers().get(0));
        gameController.placeCard(40, 40, card_2, game.getPlayers().get(3));
        JSONCard = (JSONObject) game.goldenDeck.get(0);
        card_2 = gameController.craftGoldenCard(JSONCard);
        JSONCard = (JSONObject) game.resourceDeck.get(0);
        card_2 = gameController.craftResourceCard(JSONCard);
        gameController.placeCard(39, 39, card_2, game.getPlayers().get(0));
        gameController.placeCard(39, 39, card_2, game.getPlayers().get(3));
        assert(game.getWinner()[0] == players.get(0) || game.getWinner()[0] == players.get(3));
        assert(game.getWinner()[1] == players.get(0) || game.getWinner()[1] == players.get(3));
        assertNotSame(game.getWinner()[0], game.getWinner()[1]);
        assertEquals(2, game.getWinner().length);
    }

    /**
     * Checks if the winner of a game is correctly calculated in the case of equal points and != number of goals achieved.
     */
    @Test
    public void getWinner_test_4() throws IllegalAccessException {
        ArrayList<Player> players = createFakePlayers()[0];
        game.setPlayers(players);
        players.get(0).setPawn(Pawn.BLUE);
        players.get(1).setPawn(Pawn.GREEN);
        players.get(2).setPawn(Pawn.YELLOW);
        players.get(3).setPawn(Pawn.RED);
        players.get(0).setScore(24);
        players.get(1).setScore(5);
        players.get(2).setScore(3);
        players.get(3).setScore(24);
        JSONObject JSONCard = (JSONObject) game.patternGoalDeck.get(0);
        GoalCard card = gameController.craftPatternGoalCard(JSONCard);
        for(int i = 0; i < 2; i++)
            game.commonGoalCards[i] = card;
        JSONCard = (JSONObject) game.resourcesGoalDeck.get(0);
        card = gameController.craftResourcesGoalCard(JSONCard);
        for(int i = 0; i < 4; i++) {
            Object[] myCards = new Object[3];
            myCards[0] = new Object();
            GoalCard[] temp = gameController.drawGoalCardsToChoose();
            myCards[2] = temp[0];
            myCards[1] = card;
            game.getPlayers().get(i).setCardToChoose(myCards);
            game.getPlayers().get(i).setSecretGoalCard(card.getUUID());
        }
        JSONCard = (JSONObject) game.startingDeck.get(1);
        PlayableCard card_2 = gameController.craftStartingCard(JSONCard);
        gameController.placeCard(40, 40, card_2, game.getPlayers().get(3));
        JSONCard = (JSONObject) game.resourceDeck.get(0);
        card_2 = gameController.craftResourceCard(JSONCard);
        gameController.placeCard(39, 39, card_2, game.getPlayers().get(3));
        assertEquals(players.get(3), game.getWinner()[0]);
        assertEquals(1, game.getWinner().length);
    }

    /**
     * Checks if the winner of a game is correctly calculated in the case of equal points and != number of goals achieved.
     */
    @Test
    public void getWinner_test_5() throws IllegalAccessException {
        ArrayList<Player> players = createFakePlayers()[0];
        game.setPlayers(players);
        players.get(0).setPawn(Pawn.BLUE);
        players.get(1).setPawn(Pawn.GREEN);
        players.get(2).setPawn(Pawn.YELLOW);
        players.get(3).setPawn(Pawn.RED);
        players.get(0).setScore(24);
        players.get(1).setScore(5);
        players.get(2).setScore(3);
        players.get(3).setScore(24);
        JSONObject JSONCard = (JSONObject) game.patternGoalDeck.get(0);
        GoalCard card = gameController.craftPatternGoalCard(JSONCard);
        for(int i = 0; i < 2; i++)
            game.commonGoalCards[i] = card;
        JSONCard = (JSONObject) game.resourcesGoalDeck.get(0);
        card = gameController.craftResourcesGoalCard(JSONCard);
        for(int i = 0; i < 4; i++) {
            Object[] myCards = new Object[3];
            myCards[0] = new Object();
            GoalCard[] temp = gameController.drawGoalCardsToChoose();
            myCards[2] = temp[0];
            myCards[1] = card;
            game.getPlayers().get(i).setCardToChoose(myCards);
            game.getPlayers().get(i).setSecretGoalCard(card.getUUID());
        }
        JSONCard = (JSONObject) game.startingDeck.get(1);
        PlayableCard card_2 = gameController.craftStartingCard(JSONCard);
        gameController.placeCard(40, 40, card_2, game.getPlayers().get(0));
        gameController.placeCard(40, 40, card_2, game.getPlayers().get(3));
        JSONCard = (JSONObject) game.resourceDeck.get(0);
        card_2 = gameController.craftResourceCard(JSONCard);
        gameController.placeCard(39, 39, card_2, game.getPlayers().get(0));
        gameController.placeCard(39, 39, card_2, game.getPlayers().get(3));
        JSONCard = (JSONObject) game.resourceDeck.get(1);
        card_2 = gameController.craftResourceCard(JSONCard);
        gameController.placeCard(41, 39, card_2, game.getPlayers().get(3));
        JSONCard = (JSONObject) game.resourceDeck.get(2);
        card_2 = gameController.craftResourceCard(JSONCard);
        gameController.placeCard(42, 40, card_2, game.getPlayers().get(3));
        assertEquals(players.get(3), game.getWinner()[0]);
        assertEquals(1, game.getWinner().length);
    }

    @Test
    public void setStartingPlayer_test_1(){
            ArrayList<Player>[] playersLists = createFakePlayers();
            game.setPlayers(playersLists[0]);
            game.setStartingPlayer();
            assertTrue(game.getPlayers().contains(game.getStartingPlayer()));
            assertNotNull(game.getStartingPlayer());
            assertEquals(game.getPlayers().get(0), game.getStartingPlayer());
    }

    /**
     * Checks if trying to set the starting player with a null list of players is correctly handled.
     */
    @Test(expected = RuntimeException.class)
    public void setStartingPlayer_test_2(){
        ArrayList<Player>[] playersLists = createFakePlayers();
        game.setPlayers(playersLists[2]);
        game.setStartingPlayer();
    }

    /**
     * Checks if trying to set the starting player when the list of players is not full is correctly handled.
     */
    @Test(expected = RuntimeException.class)
    public void setStartingPlayer_test_3(){
        ArrayList<Player>[] playersLists = createFakePlayers();
        game.setPlayers(playersLists[3]);
        game.setStartingPlayer();
    }

    /**
     * Checks if decks are correctly created.
     */
    @Test
    public void createDecks_test() {
        game.createDecks();
        assertNotNull(game.resourceDeck);
        assertNotNull(game.goldenDeck);
        assertNotNull(game.startingDeck);
        assertNotNull(game.resourcesGoalDeck);
        assertNotNull(game.patternGoalDeck);
    }
}