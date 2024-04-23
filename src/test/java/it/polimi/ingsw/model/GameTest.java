package it.polimi.ingsw.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.Assert.*;

//TODO: setStartingPlayer() already tested

public class GameTest {
    Game game;

    @Before
    public void setUp() throws FileNotFoundException {
        this.game = new Game();
        game.setPlayers(fakePlayersArrayList());
    }

    public ArrayList<Player> fakePlayersArrayList(){
        ArrayList<Player> players = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            players.add(new Player("Player" + i, 0));
        }
        return players;
    }

    @After
    public void tearDown() {
        game = null;
    }

    @Test
    public void SetStartingPlayer_correctInput_correctOutput(){
        game.setStartingPlayer();
        assertNotNull(game.getStartingPlayer());
        assertTrue(game.getPlayers().contains(game.getStartingPlayer()));
    }
}
