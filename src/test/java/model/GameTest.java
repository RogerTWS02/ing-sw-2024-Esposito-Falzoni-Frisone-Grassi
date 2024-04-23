package model;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.Assert.*;

//TODO: setStartingPlayer, checkOldGame already tested

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
        this.game = null;
        File oldGameFile = new File("savings/game.svs");
        oldGameFile.delete();
    }

    @Test
    public void SetStartingPlayer_correctInput_correctOutput(){
        game.setStartingPlayer();
        assertNotNull(game.getStartingPlayer());
        assertTrue(game.getPlayers().contains(game.getStartingPlayer()));
    }

    @Test
    public void checkOldGame_correctInput_correctOutput(){
        File oldGameFile = new File("savings/game.svs");
        File directory = oldGameFile.getParentFile();
        if (!directory.exists()){
            directory.mkdirs();
        }
        assertFalse(Game.checkOldGame());
        try{
            oldGameFile.createNewFile();
        } catch (Exception e){
            System.err.println("Error creating the dummy saving file!");
        }
        assertTrue(Game.checkOldGame());
    }
}
