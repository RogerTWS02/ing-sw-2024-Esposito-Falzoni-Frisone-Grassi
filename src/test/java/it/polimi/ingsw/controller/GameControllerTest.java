package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.Assert.*;

//TODO: checkPlayersScore, checkEndGamePhase already tested

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
