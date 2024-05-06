package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;

public class GameControllerTest {
    GameController gameController;
    Game game;


    @Before
    public void setUp() throws FileNotFoundException {
        this.gameController = new GameController();
        this.game = gameController.getCurrentGame();
    }

    @After
    public void tearDown() {
        this.gameController = null;
        this.game = null;
    }

    @Test
    public void placeCard_test_1() {
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

    @Test(expected = IllegalArgumentException.class)
    public void placeCard_test_2() {
        game.setPlayers(createFakePlayers());
        game.getPlayers().get(0).setPawn(Pawn.BLUE);
        PlayableCard startingCard = gameController.drawPlayableFromDeck(game.startingDeck);
        gameController.placeCard(81, 81, startingCard, game.getPlayers().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void placeCard_test_3() {
        game.setPlayers(createFakePlayers());
        game.getPlayers().get(0).setPawn(Pawn.BLUE);
        PlayableCard startingCard = gameController.drawPlayableFromDeck(game.startingDeck);
        gameController.placeCard(-4, -56, startingCard, game.getPlayers().get(0));
    }


    @Test
    public void showAvailableOnBoard_test() {
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

    @Test
    public void updatePlayerPointsFromAllGoalCards_test() throws FileNotFoundException {
        //TODO
        Player fakeplayer= new Player("Donnie", 28064212);

        GoalCard fgc1= new ResourcesGoalCard(5,null, "fgc1"){
            @Override
            public int checkGoal(PlayerBoard board) {
                return this.getPoints();
            }
        };
        fakeplayer.setSecretGoalCard(fgc1);
        GoalCard fgc2= new ResourcesGoalCard(5,null, "fgc2"){
            @Override
            public int checkGoal(PlayerBoard board) {
                return this.getPoints();
            }
        };
        GoalCard fgc3= new ResourcesGoalCard(4,null, "fgc3"){
            @Override
            public int checkGoal(PlayerBoard board) {
                return this.getPoints();
            }

        };

         game.setCommonGoalCards(new GoalCard[]{fgc2, fgc3});
         GoalCard[] goalCards = game.getCommonGoalCards();
         gameController.getPointsFromGoalCards(fakeplayer);
         assertEquals(14, fakeplayer.getScore());

    };

    @Test
    public void drawPlayableFromDeck_test_1() {
        assertNotNull(gameController.drawPlayableFromDeck(game.resourceDeck));
        assertNotNull(gameController.drawPlayableFromDeck(game.goldenDeck));
        assertNotNull(gameController.drawPlayableFromDeck(game.startingDeck));
    }

    @Test(expected = IllegalArgumentException.class)
    public void drawPlayableFromDeck_test_2() {
        for(int i = 0; i < 41; i++) {
            gameController.drawPlayableFromDeck(game.resourceDeck);
        }
    }

    @Test
    public void drawGoalFromDeck_test() {
        GoalCard card = gameController.drawGoalFromDeck();
        assertNotNull(card);
    }

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

    @Test(expected = IllegalArgumentException.class)
    public void drawViewableCard_test_1() {
        gameController.drawViewableCard(true, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void drawViewableCard_test_2() {
        gameController.drawViewableCard(true, 3);
    }

    @Test
    public void drawViewableCard_test_3() {
        for(int i = 0; i < 3; i++)
            gameController.drawViewableCard(true, i);
        for(int i = 0; i < 3; i++)
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
        System.setIn(new ByteArrayInputStream("4\n".getBytes()));
        gameController.beginGame();
        ArrayList<Player> fakePlayers = createFakePlayers();
        for(int i = 0; i < 4; i++) {
            gameController.addPlayer(fakePlayers.get(i).getNickname(), 0);
        }
        assertFalse(gameController.getCurrentGame().getPlayers().contains(null));
        assertEquals(4, gameController.getCurrentGame().getPlayers().size());

        //TODO: complete testing when the method is complete

    }

    @Test
    public void addPlayer_test() {
        ArrayList<Player> fakePlayers = createFakePlayers();
        fakePlayers.remove(3);
        game.setPlayers(fakePlayers);
        String nickname = "testPlayer";
        int clientPort = 8080;
        gameController.addPlayer(nickname, clientPort);
        Player player = null;
        Player addedPlayer = null;
        for (int i = 0; i < game.getPlayers().size(); i++) {
            player = game.getPlayers().get(i);
            if (player != null && player.getNickname().equals(nickname) && player.clientPort == clientPort) {
                break;
            }
        }
        assertNotNull(player);
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
