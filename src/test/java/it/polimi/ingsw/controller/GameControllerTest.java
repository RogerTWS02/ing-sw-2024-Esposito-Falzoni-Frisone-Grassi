package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit test for GameController class and, indirectly, for PlayerBoard class.
 */
public class GameControllerTest {
    GameController gameController;
    Game game;

    @Before
    public void setUp() throws FileNotFoundException {
        this.gameController = new GameController(123);
        this.game = gameController.getCurrentGame();
    }

    @After
    public void tearDown() {
        this.gameController = null;
        this.game = null;
    }

    /**
     * Checks if the game is correctly initialized, setting the viewable cards, the players' hands and the common goal cards correctly.
     */
    @Test
    public void initializeGame_test() {
        int i;
        game.setPlayers(createFakePlayers());
        gameController.initializeGame();
        for(i = 0; i < 3; i++) {
            assertNotNull(game.viewableGoldenCards[i]);
            assertNotNull(game.viewableResourceCards[i]);
        }
        for(i = 0; i < 2; i++)
            assertNotNull(game.commonGoalCards[i]);
        for(Player player : game.getPlayers()) {
            for(i = 0; i < 3; i++) {
                assertNotNull(player.getHand()[i]);
                if(i == 0)
                    assert(player.getHand()[i] instanceof GoldenCard);
                else
                    assert(player.getHand()[i] instanceof ResourceCard);
            }
        }
    }

    /**
     * Checks if the player's turn is correctly advanced.
     */
    @Test
    public void advancePlayerTurn_test() {
        game.setPlayers(createFakePlayers());
        game.getPlayers().get(0).setPawn(Pawn.BLUE);
        game.getPlayers().get(1).setPawn(Pawn.RED);
        game.getPlayers().get(2).setPawn(Pawn.GREEN);
        game.getPlayers().get(3).setPawn(Pawn.YELLOW);
        game.setCurrentPlayer(game.getPlayers().get(3));
        gameController.advancePlayerTurn();
        assertEquals(game.getPlayers().get(0), game.getCurrentPlayer());
    }

    /**
     * Checks if the preliminary choices of the player are correctly handled.
     */
    @Test
    public void cardToChoose_test() {
        game.setPlayers(createFakePlayers());
        game.getPlayers().get(0).setPawn(Pawn.BLUE);
        String[] temp = gameController.cardToChoose(game.getPlayers().get(0));
        assertNotNull(temp);
        assertEquals(3, temp.length);
        for(int i = 0; i < 3; i++) {
            assertNotNull(temp[i]);
        }
    }

    /**
     * Checks if the starting card is placed in the right position in every case and if the cells of the player board are correctly updated.
     */
    @Test
    public void placeCard_test_1() throws IllegalAccessException {
        game.setPlayers(createFakePlayers());
        game.getPlayers().get(0).setPawn(Pawn.BLUE);
        PlayableCard startingCard = gameController.drawPlayableFromDeck(game.startingDeck);
        gameController.placeCard(69, 69, startingCard, game.getPlayers().get(0));
        assertEquals(startingCard, game.getPlayers().get(0).getPlayerBoard().getCard(40, 40));
        assertEquals(State.OCCUPIED, game.getPlayers().get(0).getPlayerBoard().getState(40, 40));
        assertEquals(State.UNPLAYED, game.getPlayers().get(0).getPlayerBoard().getState(69, 69));
        if(startingCard.getCardCorners()[0] != null) {
            assertEquals(State.AVAILABLE, game.getPlayers().get(0).getPlayerBoard().getState(39, 41));
        } else {
            assertEquals(State.UNAVAILABLE, game.getPlayers().get(0).getPlayerBoard().getState(39, 41));
        }
        if(startingCard.getCardCorners()[1] != null) {
            assertEquals(State.AVAILABLE, game.getPlayers().get(0).getPlayerBoard().getState(41, 41));
        } else {
            assertEquals(State.UNAVAILABLE, game.getPlayers().get(0).getPlayerBoard().getState(41, 41));
        }
        if(startingCard.getCardCorners()[2] != null) {
            assertEquals(State.AVAILABLE, game.getPlayers().get(0).getPlayerBoard().getState(39, 39));
        } else {
            assertEquals(State.UNAVAILABLE, game.getPlayers().get(0).getPlayerBoard().getState(39, 39));
        }
        if(startingCard.getCardCorners()[3] != null) {
            assertEquals(State.AVAILABLE, game.getPlayers().get(0).getPlayerBoard().getState(41, 39));
        } else {
            assertEquals(State.UNAVAILABLE, game.getPlayers().get(0).getPlayerBoard().getState(41, 39));
        }
    }

    /**
     * Checks if the placement of a 'flipped' card is performed correctly.
     */
    @Test
    public void placeCard_test_2() throws IllegalAccessException {
        game.setPlayers(createFakePlayers());
        game.getPlayers().get(0).setPawn(Pawn.BLUE);
        PlayableCard card = gameController.drawPlayableFromDeck(game.startingDeck);
        gameController.placeCard(40, 40, card, game.getPlayers().get(0));
        card = gameController.drawPlayableFromDeck(game.resourceDeck);
        card.setFlipped(true);
        gameController.placeCard(39, 41, card, game.getPlayers().get(0));
        assert(game.getPlayers().get(0).getPlayerBoard().getCard(39, 41).equals(card));
        assert(game.getPlayers().get(0).getPlayerBoard().getState(41, 39).equals(State.OCCUPIED));
    }

    /**
     * Checks if the placement of a golden card without having the required resources is correctly handled.
     */
    @Test(expected = IllegalArgumentException.class)
    public void placeCard_test_3() throws IllegalAccessException {
        game.setPlayers(createFakePlayers());
        game.getPlayers().get(0).setPawn(Pawn.BLUE);
        PlayableCard card = gameController.drawPlayableFromDeck(game.startingDeck);
        gameController.placeCard(40, 40, card, game.getPlayers().get(0));
        card = gameController.drawPlayableFromDeck(game.goldenDeck);
        gameController.placeCard(39, 41, card, game.getPlayers().get(0));
    }

    /**
     * Checks if the placement of a card in an occupied cell is correctly handled.
     */
    @Test(expected = IllegalArgumentException.class)
    public void placeCard_test_4() throws IllegalAccessException {
        game.setPlayers(createFakePlayers());
        game.getPlayers().get(0).setPawn(Pawn.BLUE);
        PlayableCard card = gameController.drawPlayableFromDeck(game.startingDeck);
        gameController.placeCard(40, 40, card, game.getPlayers().get(0));
        card = gameController.drawPlayableFromDeck(game.resourceDeck);
        card.setFlipped(true);
        gameController.placeCard(40, 40, card, game.getPlayers().get(0));
    }

    /**
     * Checks if the placement of a resource card which gives points is correctly handled.
     */
    @Test
    public void placeCard_test_5() throws IllegalAccessException {
        game.setPlayers(createFakePlayers());
        game.getPlayers().get(0).setPawn(Pawn.BLUE);
        PlayableCard card = gameController.drawPlayableFromDeck(game.startingDeck);
        gameController.placeCard(40, 40, card, game.getPlayers().get(0));
        do {
            card = gameController.drawPlayableFromDeck(game.resourceDeck);
        } while(card.getPoints() != 0);
        gameController.placeCard(39, 41, card, game.getPlayers().get(0));
        assert(game.getPlayers().get(0).getPlayerBoard().getCard(39, 41).equals(card));
        assert(game.getPlayers().get(0).getPlayerBoard().getState(41, 39).equals(State.OCCUPIED));
        assertEquals(card.getPoints(), game.getPlayers().get(0).getScore());
    }

    /**
     * Checks if the placement of a golden card which gives points is correctly handled in the case of giving points for every corner covered.
     */
    @Test
    public void placeCard_test_6() throws IllegalAccessException {
        game.setPlayers(createFakePlayers());
        game.getPlayers().get(0).setPawn(Pawn.BLUE);
        PlayableCard card = gameController.drawPlayableFromDeck(game.startingDeck);
        gameController.placeCard(40, 40, card, game.getPlayers().get(0));
        JSONObject JSONCard = (JSONObject) game.resourceDeck.get(0);
        card = gameController.craftResourceCard(JSONCard);
        gameController.placeCard(41, 39, card, game.getPlayers().get(0));
        JSONCard = (JSONObject) game.resourceDeck.get(1);
        card = gameController.craftResourceCard(JSONCard);
        gameController.placeCard(39, 39, card, game.getPlayers().get(0));
        JSONCard = (JSONObject) game.resourceDeck.get(18);
        card = gameController.craftResourceCard(JSONCard);
        gameController.placeCard(38, 38, card, game.getPlayers().get(0));
        JSONCard = (JSONObject) game.goldenDeck.get(5);
        card = gameController.craftGoldenCard(JSONCard);
        gameController.placeCard(40, 38, card, game.getPlayers().get(0));
        assertEquals(4, game.getPlayers().get(0).getScore());
    }

    /**
     * Checks if the placement of a golden card which gives points is correctly handled in the case of giving points for the resources quantity on the board.
     */
    @Test
    public void placeCard_test_7() throws IllegalAccessException {
        game.setPlayers(createFakePlayers());
        game.getPlayers().get(0).setPawn(Pawn.BLUE);
        PlayableCard card;
        gameController.placeCard(40, 40, gameController.craftStartingCard((JSONObject) game.startingDeck.get(3)), game.getPlayers().get(0));
        JSONObject JSONCard = (JSONObject) game.resourceDeck.get(4);
        card = gameController.craftResourceCard(JSONCard);
        gameController.placeCard(41, 39, card, game.getPlayers().get(0));
        JSONCard = (JSONObject) game.goldenDeck.get(0);
        card = gameController.craftGoldenCard(JSONCard);
        gameController.placeCard(39, 39, card, game.getPlayers().get(0));
        assertEquals(2, game.getPlayers().get(0).getScore());
    }

    /**
     * Checks if the available position on the board are correctly shown.
     */
    @Test
    public void showAvailableOnBoard_test() throws IllegalAccessException {
        game.setPlayers(createFakePlayers());
        game.getPlayers().get(0).setPawn(Pawn.BLUE);
        PlayableCard startingCard = gameController.drawPlayableFromDeck(game.startingDeck);
        gameController.placeCard(69, 69, startingCard, game.getPlayers().get(0));
        Corner[] corners = startingCard.getCardCorners();
        int availableCorners = 0;
        for (int i = 0; i < corners.length; i++) {
            if (corners[i] != null) {
                availableCorners++;
            }
        }
        assertEquals(availableCorners, gameController.showAvailableOnBoard(game.getPlayers().get(0)).size());
    }

    /**
     * Checks if drawing a card from the deck is correctly handled.
     */
    @Test
    public void drawPlayableFromDeck_test_1() {
        assertNotNull(gameController.drawPlayableFromDeck(game.resourceDeck));
        assertNotNull(gameController.drawPlayableFromDeck(game.goldenDeck));
        assertNotNull(gameController.drawPlayableFromDeck(game.startingDeck));
    }

    /**
     * Checks if drawing a card from an empty deck is correctly handled.
     */
    @Test(expected = IllegalArgumentException.class)
    public void drawPlayableFromDeck_test_2() {
        for(int i = 0; i < 41; i++) {
            gameController.drawPlayableFromDeck(game.resourceDeck);
        }
    }

    /**
     * Checks if drawing a goal card is correctly handled.
     */
    @Test
    public void drawGoalFromDeck_test() {
        GoalCard card = gameController.drawGoalFromDeck();
        assertNotNull(card);
    }

    /**
     * Checks if the crafting a resources goal card is correctly performed.
     */
    @Test
    public void craftResourcesGoalCard_test() {
        JSONObject JSONcard = new JSONObject();
        JSONcard.put("UUID", "testUUID");
        JSONcard.put("points", 99);
        JSONArray resources = new JSONArray();
        resources.add("WOLF");
        resources.add("MUSHROOM");
        resources.add("MUSHROOM");
        JSONcard.put("resources", resources);
        GoalCard card = gameController.craftResourcesGoalCard(JSONcard);
        assertNotNull(card);
        assertEquals("testUUID", card.getUUID());
        assertEquals(99, card.getPoints());
        ResourcesGoalCard resourcesGoalCard = (ResourcesGoalCard) card;
        Map<Resource, Integer> resourcesMap = resourcesGoalCard.getResourcesMap();
        assertEquals(1, resourcesMap.get(Resource.WOLF).intValue());
        assertEquals(2, resourcesMap.get(Resource.MUSHROOM).intValue());
    }

    /**
     * Checks if the crafting a pattern goal card is correctly performed.
     */
    @Test
    public void craftPatternGoalCard_test() {
        JSONObject JSONcard = new JSONObject();
        JSONcard.put("UUID", "testUUID");
        JSONcard.put("points", 99);
        JSONArray pattern = new JSONArray();
        pattern.add(1);
        pattern.add(2);
        pattern.add(3);
        pattern.add(4);
        pattern.add(5);
        pattern.add(6);
        JSONcard.put("pattern", pattern);
        JSONArray resources = new JSONArray();
        resources.add("WOLF");
        resources.add("MUSHROOM");
        resources.add("LEAF");
        JSONcard.put("resources", resources);
        GoalCard card = gameController.craftPatternGoalCard(JSONcard);
        assertNotNull(card);
        assertEquals("testUUID", card.getUUID());
        assertEquals(99, card.getPoints());
        PatternGoalCard patternGoalCard = (PatternGoalCard) card;
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6}, patternGoalCard.getPatternPosition());
        assertArrayEquals(new Resource[]{Resource.WOLF, Resource.MUSHROOM, Resource.LEAF}, patternGoalCard.getPatternResource());
    }

    /**
     * Checks if the crafting a resource card is correctly performed.
     */
    @Test
    public void craftResourceCard_test() {
        JSONObject JSONcard = new JSONObject();
        JSONcard.put("UUID", "testUUID");
        JSONcard.put("points", 99);
        JSONcard.put("permRes", "WOLF");
        JSONArray corners = new JSONArray();
        corners.add("WOLF");
        corners.add("MUSHROOM");
        corners.add(null);
        corners.add("EMPTY");
        JSONcard.put("corners", corners);
        PlayableCard card = gameController.craftResourceCard(JSONcard);
        assertNotNull(card);
        assertEquals("testUUID", card.getUUID());
        ResourceCard resourceCard = (ResourceCard) card;
        assertEquals(99, resourceCard.getPoints());
        assertEquals(Resource.WOLF, resourceCard.getPermResource()[0]);
        Corner[] cornersArray = resourceCard.getCardCorners();
        for (int i = 0; i < cornersArray.length; i++) {
            if (cornersArray[i] != null && cornersArray[i].getCornerResource().isPresent()) {
                assertEquals(corners.get(i), cornersArray[i].getCornerResource().get().toString());
            } else if(cornersArray[i] != null && corners.get(i).equals("EMPTY")) {
                assertTrue(cornersArray[i].getCornerResource().isEmpty());
            } else {
                assertNull(corners.get(i));
            }
        }
    }

    /**
     * Checks if the crafting a golden card is correctly performed.
     */
    @Test
    public void craftGoldenCard_test() {
        JSONObject JSONcard = new JSONObject();
        JSONcard.put("UUID", "testUUID");
        JSONcard.put("points", 99);
        JSONcard.put("permRes", "WOLF");
        JSONArray corners = new JSONArray();
        corners.add("WOLF");
        corners.add("MUSHROOM");
        corners.add(null);
        corners.add("EMPTY");
        JSONcard.put("corners", corners);
        JSONcard.put("rule", "CORNERS");
        JSONArray require = new JSONArray();
        require.add("WOLF");
        require.add("MUSHROOM");
        require.add("LEAF");
        JSONcard.put("require", require);
        PlayableCard card = gameController.craftGoldenCard(JSONcard);
        assertNotNull(card);
        assertEquals("testUUID", card.getUUID());
        GoldenCard goldenCard = (GoldenCard) card;
        assertEquals(99, goldenCard.getPoints());
        assertEquals(Resource.WOLF, goldenCard.getPermResource()[0]);
        assertEquals("CORNERS", goldenCard.getRule());
        ArrayList<Resource> expectedResources = new ArrayList<>();
        expectedResources.add(Resource.WOLF);
        expectedResources.add(Resource.MUSHROOM);
        expectedResources.add(Resource.LEAF);
        ArrayList<Resource> actualResources = goldenCard.getRequiredResource();
        for(int i = 0; i < expectedResources.size(); i++) {
            assertEquals(expectedResources.get(i), actualResources.get(i));
        }
        Corner[] cornersArray = goldenCard.getCardCorners();
        for (int i = 0; i < cornersArray.length; i++) {
            if (cornersArray[i] != null && cornersArray[i].getCornerResource().isPresent()) {
                assertEquals(corners.get(i), cornersArray[i].getCornerResource().get().toString());
            } else if(cornersArray[i] != null && corners.get(i).equals("EMPTY")) {
                assertTrue(cornersArray[i].getCornerResource().isEmpty());
            } else {
                assertNull(corners.get(i));
            }
        }
    }

    /**
     * Checks if the crafting a starting card is correctly performed.
     */
    @Test
    public void craftStartingCard_test() {
        JSONObject JSONcard = new JSONObject();
        JSONcard.put("UUID", "testUUID");
        JSONArray permRes = new JSONArray();
        permRes.add("WOLF");
        permRes.add("MUSHROOM");
        JSONcard.put("permRes", permRes);
        JSONArray frontCorners = new JSONArray();
        frontCorners.add("WOLF");
        frontCorners.add("MUSHROOM");
        frontCorners.add(null);
        frontCorners.add("EMPTY");
        JSONcard.put("frontCorners", frontCorners);
        JSONArray backCorners = new JSONArray();
        backCorners.add("LEAF");
        backCorners.add("BUTTERFLY");
        backCorners.add(null);
        backCorners.add("EMPTY");
        JSONcard.put("backCorners", backCorners);
        PlayableCard card = gameController.craftStartingCard(JSONcard);
        assertNotNull(card);
        assertEquals("testUUID", card.getUUID());
        assertArrayEquals(new Resource[]{Resource.WOLF, Resource.MUSHROOM}, card.getPermResource());
        StartingCard startingCard = (StartingCard) card;
        Corner[] frontCornersArray = startingCard.getFrontCardCorners();
        for (int i = 0; i < frontCornersArray.length; i++) {
            if (frontCornersArray[i] != null && frontCornersArray[i].getCornerResource().isPresent()) {
                assertEquals(frontCorners.get(i), frontCornersArray[i].getCornerResource().get().toString());
            } else if(frontCornersArray[i] != null && frontCorners.get(i).equals("EMPTY")) {
                assertTrue(frontCornersArray[i].getCornerResource().isEmpty());
            } else {
                assertNull(frontCorners.get(i));
            }
        }
        Corner[] backCornersArray = startingCard.getBackCardCorners();
        for (int i = 0; i < backCornersArray.length; i++) {
            if (backCornersArray[i] != null && backCornersArray[i].getCornerResource().isPresent()) {
                assertEquals(backCorners.get(i), backCornersArray[i].getCornerResource().get().toString());
            } else if(backCornersArray[i] != null && backCorners.get(i).equals("EMPTY")) {
                assertTrue(backCornersArray[i].getCornerResource().isEmpty());
            } else {
                assertNull(backCorners.get(i));
            }
        }
    }

    /**
     * Checks if the conversion of a string to a resource is correctly performed.
     */
    @Test
    public void stringToResource_test() {
        assertNull(gameController.stringToResource("test"));
        assertNull(gameController.stringToResource(null));
        assertEquals(Resource.WOLF, gameController.stringToResource("WOLF"));
        assertEquals(Resource.MUSHROOM, gameController.stringToResource("MUSHROOM"));
        assertEquals(Resource.LEAF, gameController.stringToResource("LEAF"));
        assertEquals(Resource.BUTTERFLY, gameController.stringToResource("BUTTERFLY"));
        assertEquals(Resource.FEATHER, gameController.stringToResource("FEATHER"));
        assertEquals(Resource.SCROLL, gameController.stringToResource("SCROLL"));
        assertEquals(Resource.GLASSVIAL, gameController.stringToResource("GLASSVIAL"));
    }

    /**
     * Checks if creating a corner array of a card is correctly performed.
     */
    @Test
    public void craftCornerArray_test() {
        JSONArray JSONCorners = new JSONArray();
        JSONCorners.add("WOLF");
        JSONCorners.add("MUSHROOM");
        JSONCorners.add(null);
        JSONCorners.add("EMPTY");
        PlayableCard card = new ResourceCard(new Resource[]{Resource.WOLF}, null, 99, "testUUID");
        Corner[] corners = gameController.craftCornerArray(JSONCorners, card);
        assertNotNull(corners);
        assertEquals(4, corners.length);
        assertEquals(Resource.WOLF, corners[0].getCornerResource().get());
        assertEquals(Resource.MUSHROOM, corners[1].getCornerResource().get());
        assertNull(corners[2]);
        assertTrue(corners[3].getCornerResource().isEmpty());
    }

    /**
     * Checks if drawing a viewable card with an unexpected index is correctly handled.
     */
    @Test(expected = IllegalArgumentException.class)
    public void drawViewableCard_test_1() {
        gameController.drawViewableCard(true, -1);
    }

    /**
     * Checks if drawing a viewable card with an unexpected index is correctly handled.
     */
    @Test(expected = IllegalArgumentException.class)
    public void drawViewableCard_test_2() {
        gameController.drawViewableCard(true, 3);
    }

    /**
     * Checks if drawing a viewable card is correctly handled.
     */
    @Test
    public void drawViewableCard_test_3() {
        for(int i = 2; i >= 0; i--)
            gameController.drawViewableCard(true, i);
        for(int i = 2; i >= 0; i--)
            gameController.drawViewableCard(false, i);
        PlayableCard card = gameController.drawViewableCard(true, 0);
        assertNotNull(card);
        assert(card instanceof GoldenCard);
        assertNotNull(game.viewableGoldenCards[0]);
        card = gameController.drawViewableCard(false, 0);
        assertNotNull(card);
        assert(card instanceof ResourceCard);
        assertNotNull(game.viewableResourceCards[0]);
    }

    /**
     * Checks if trying to get a player's hand with a 'null' player is correctly handled.
     */
    @Test(expected = IllegalArgumentException.class)
    public void getHand_test_1() {
        gameController.returnHand(null);
    }

    /**
     * Checks if trying to get a player's hand with a player which is not in the game is correctly handled.
     */
    @Test(expected = IllegalArgumentException.class)
    public void getHand_test_2() {
        ArrayList<Player> fakePlayers1 = createFakePlayers();
        ArrayList<Player> fakePlayers2 = createFakePlayers();
        game.setPlayers(fakePlayers1);
        gameController.returnHand(fakePlayers2.get(0));
    }

    /**
     * Checks if trying to set up a game with an unexpected number of players is correctly handled.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setNumberOfPlayers_test_1() {
        gameController.setNumberOfPlayers(0);
    }

    /**
     * Checks if trying to set up a game with an unexpected number of players is correctly handled.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setNumberOfPlayers_test_2() {
        gameController.setNumberOfPlayers(5);
    }

    /**
     * Checks if setting up a game with an ok number of players is correctly handled.
     */
    @Test
    public void setNumberOfPlayers_test_3() {
        gameController.setNumberOfPlayers(3);
        assertEquals(3, game.getPlayers().size());
    }

    /**
     * Checks if drawing goal cards to choose is correctly handled.
     */
    @Test
    public void drawGoalCardsToChoose_test() {
        GoalCard[] cards = gameController.drawGoalCardsToChoose();
        assertNotNull(cards);
        assertNotNull(cards[0]);
        assertNotNull(cards[1]);
    }

    /**
     * Checks if adding a player to the game is correctly handled.
     */
    @Test
    public void addPlayer_test() {
        ArrayList<Player> players = createFakePlayers();
        players.removeFirst();
        players.removeFirst();
        players.removeFirst();
        players.add(null);
        players.add(null);
        players.add(null);
        game.setPlayers(players);
        players = createFakePlayers();
        gameController.addPlayer(players.get(0));
        assertEquals(4, game.getPlayers().size());
        for(int i = 0; i < 2; i++)
            assertNotNull(game.getPlayers().get(i));
        assertEquals(players.get(0), game.getPlayers().get(1));
        for(int i = 2; i <4; i++)
            assertNull(game.getPlayers().get(i));
    }

    /**
     * Checks if the starting of the end game phase is correctly recognized.
     */
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

    /**
     * Checks if a player which reaches a score of 20 is correctly recognized.
     */
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
}
