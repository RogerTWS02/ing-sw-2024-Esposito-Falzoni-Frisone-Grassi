package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GoalCard;
import it.polimi.ingsw.model.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class GameControllerTest {
    GameController gameController;
    Game game;


    @Before
    public void setUp() throws FileNotFoundException {
        this.gameController = new GameController();
        this.game = new Game();
        gameController.setCurrentGame(game);
    }

    @After
    public void tearDown() {
        this.gameController = null;
        this.game = null;
    }

    @Test
    public void drawPlayableFromDeck_test() {
        //TODO
    }

    @Test
    public void drawGoalFromDeck_test() {
        //TODO
    }

    @Test
    public void craftResourcesGoalCard_test() {
        //TODO
    }

    @Test
    public void craftPatternGoalCard_test() {
        //TODO
    }

    @Test
    public void craftResourceCard_test() {
        //TODO
    }

    @Test
    public void craftGoldenCard_test() {
        //TODO
    }

    @Test
    public void craftStartingCard_test() {
        //TODO
    }

    @Test
    public void stringToResource_test() {
        //TODO
    }

    @Test
    public void craftCornerArray_test() {
        //TODO
    }

    @Test
    public void drawViewableCard_test() {
        //TODO
    }

    @Test(expected = IllegalArgumentException.class)
    public void getHand_test_1() {
        gameController.returnHand(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getHand_test_2() {
        ArrayList<Player> fakePlayers1 = createFakePlayers();
        ArrayList<Player> fakePlayers2 = createFakePlayers();
        game.setPlayers(fakePlayers1);
        gameController.returnHand(fakePlayers2.get(0));
    }

    @Test
    public void placeCard_test() {
        //TODO
    }

    @Test
    public void showAvailableOnBoard_test() {
        //TODO
    }

    @Test
    public void getPointsFromGoalCards_test() {
        //TODO
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNumberOfPlayers_test_1() {
        gameController.setNumberOfPlayers(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNumberOfPlayers_test_2() {
        gameController.setNumberOfPlayers(5);
    }

    @Test
    public void setNumberOfPlayers_test_3() {
        gameController.setNumberOfPlayers(3);
        assertEquals(3, game.getPlayers().size());
    }

    @Test
    public void drawGoalCardsToChoose_test() {
        GoalCard[] cards = gameController.drawGoalCardsToChoose();
        assertNotNull(cards);
        assertNotNull(cards[0]);
        assertNotNull(cards[1]);
    }

    @Test
    public void beginGame_test() throws IOException {
        this.game = null;
        this.gameController.setCurrentGame(null);
        //Game class constructor already tested
        System.setIn(new ByteArrayInputStream("4\n".getBytes()));
        gameController.beginGame();
        ArrayList<Player> fakePlayers = createFakePlayers();
        for(int i = 0; i < 4; i++) {
            gameController.addPlayer(fakePlayers.get(i).getNickname(), 0);
        }
        assertNotNull(gameController.getCurrentGame());
        assertFalse(gameController.getCurrentGame().getPlayers().contains(null));
        assertEquals(4, gameController.getCurrentGame().getPlayers().size());

        //TODO: complete testing when the method is complete

    }

    @Test
    public void checkEndGamePhase_test() {
        game.setPlayers(createFakePlayers());
        game.getPlayers().get(0).setScore(5);
        game.getPlayers().get(1).setScore(10);
        game.getPlayers().get(2).setScore(19);
        game.getPlayers().get(3).setScore(20);
        //Both conditions true
        assertTrue(gameController.checkEndGamePhase());
        gameController.setNewViewableCard(true, 2);
        gameController.setNewViewableCard(false, 2);
        //Only one condition true
        assertTrue(gameController.checkEndGamePhase());
        game.getPlayers().get(3).setScore(0);
        //Both conditions false
        assertFalse(gameController.checkEndGamePhase());
    }

    @Test
    public void checkPlayersScore_test() {
        game.setPlayers(createFakePlayers());
        game.getPlayers().get(0).setScore(5);
        game.getPlayers().get(1).setScore(10);
        game.getPlayers().get(2).setScore(19);
        game.getPlayers().get(3).setScore(20);
        assertTrue(gameController.checkPlayersScore());
        game.getPlayers().get(3).setScore(0);
        assertFalse(gameController.checkPlayersScore());
    }

    //Create an arrayList of fake players
    public ArrayList<Player> createFakePlayers(){
        ArrayList<Player> players = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            players.add(new Player("Player" + i, 0));
        }
        return players;
    }

    @Test
    public void endGamePhase_test() {
        //TODO
    }
}
