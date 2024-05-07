package it.polimi.ingsw.model;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class GameTest {
    Game game;

    @Before
    public void setUp() throws IOException {
        Server server = new Server();
        Message message = new Message(null, 0, 123, new Object[]{"Player0", "LobbyName", 4});
        server.requestNewLobby(message);
        this.game = server.getGameControllerMap().get(123).getCurrentGame();
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

    @Test
    public void setStartingPlayer_test_1(){
            ArrayList<Player>[] playersLists = createFakePlayers();
            game.setPlayers(playersLists[0]);
            game.setStartingPlayer();
            assertTrue(game.getPlayers().contains(game.getStartingPlayer()));
            assertNotNull(game.getStartingPlayer());
    }

    @Test(expected = RuntimeException.class)
    public void setStartingPlayer_test_2(){
        ArrayList<Player>[] playersLists = createFakePlayers();
        game.setPlayers(playersLists[1]);
        game.setStartingPlayer();
    }

    @Test(expected = RuntimeException.class)
    public void setStartingPlayer_test_3(){
        ArrayList<Player>[] playersLists = createFakePlayers();
        game.setPlayers(playersLists[2]);
        game.setStartingPlayer();
    }

    @Test(expected = RuntimeException.class)
    public void setStartingPlayer_test_4(){
        ArrayList<Player>[] playersLists = createFakePlayers();
        game.setPlayers(playersLists[3]);
        game.setStartingPlayer();
    }

    @Test
    public void checkOldGame_test(){
        initSaving(123);
        assertTrue(Game.checkOldGame(123));
        assertFalse(game.checkOldGame(777));
        game.deleteOldSaving();
        assertFalse(game.checkOldGame(123));
        deleteSavingFolder();
    }

    @Test
    public void retrieveGame_test() throws IOException {
        initSaving(123);
        assertNull(Game.retrieveGame(123));
        game.saveGame();
        assertNotNull(Game.retrieveGame(123));
        game.deleteOldSaving();
        deleteSavingFolder();
    }

    public void initSaving(int gameID) {
        File oldGameFile = new File("savings/" + gameID + "game.svs");
        File directory = oldGameFile.getParentFile();
        if (!directory.exists()){
            directory.mkdirs();
        } else {
            for (File file : directory.listFiles()) {
                file.delete();
            }
        }
        try{
            oldGameFile.createNewFile();
        } catch (Exception e){
            System.err.println("Error creating the dummy saving file!");
        }
    }

    public void deleteSavingFolder() {
        File oldGameFile = new File("savings/game.svs");
        File directory = oldGameFile.getParentFile();
        directory.delete();
    }

    @Test
    public void deleteOldSaving_test() {
        initSaving(123);
        game.deleteOldSaving();
        assertFalse(game.checkOldGame(123));
        deleteSavingFolder();
    }

    @Test
    public void saveGame_test() throws IOException {
        initSaving(123);
        game.deleteOldSaving();
        game.saveGame();
        assertTrue(game.checkOldGame(123));
        game.deleteOldSaving();
        deleteSavingFolder();
    }

    @Test
    public void createDecks_test() {
        game.createDecks();
        assertNotNull(game.resourceDeck);
        assertNotNull(game.goldenDeck);
        assertNotNull(game.startingDeck);
        assertNotNull(game.resourcesGoalDeck);
        assertNotNull(game.patternGoalDeck);
    }

    @Test
    public void game_Constructor_test() throws IOException {
        //TODO
    }
}