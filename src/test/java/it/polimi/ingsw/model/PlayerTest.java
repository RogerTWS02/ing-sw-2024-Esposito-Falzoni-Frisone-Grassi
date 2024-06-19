package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Player testing class.
 */
public class PlayerTest {
    GameController gameController;
    Game game;

    @Before
    public void setUp() {
        gameController = new GameController(123);
        game = gameController.getCurrentGame();
        game.setPlayers(createFakePlayers()[0]);
    }

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
     * Tests the setCardToChoose method, which checks if the secret goal card is set correctly, accordingly to the player's choice.
     */
    @Test
    public void setSecretGoalCard_test() {
        Object[] myCards = new Object[3];
        myCards[0] = new Object();
        GoalCard[] temp = gameController.drawGoalCardsToChoose();
        myCards[2] = temp[0];
        myCards[1] = temp[1];
        game.getPlayers().get(0).setCardToChoose(myCards);
        GoalCard card = (GoalCard) game.getPlayers().get(0).getCardToChoose()[1];
        game.getPlayers().get(0).setSecretGoalCard(card.getUUID());
        assertEquals(card, game.getPlayers().get(0).getSecretGoalCard());
    }
}
