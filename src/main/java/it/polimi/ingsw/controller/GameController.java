package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;


/**
 * GameController class controls the game, by managing decks, players, cards.
*/
public class GameController {

    /**
     * The current game object, which is being managed by the GameController.
     */
    private final Game currentGame;

    /**
     * Constructor of the GameController class. It creates a new Game object.
     *
     * @param gameID The ID of the game to be created, which identifies it uniquely.
     */
    public GameController(int gameID) {
        this.currentGame = new Game(gameID);
    }

    /**
     * Draws a random PlayableCard from the deck passed by parameter.
     *
     * @param deck The deck from which the card is drawn.
     * @return The PlayableCard object drawn from the deck and instantiated.
     */
    public PlayableCard drawPlayableFromDeck(JSONArray deck) {
        if(deck.isEmpty()){
            throw new IllegalArgumentException("Selected deck is empty!");
        }
        Random random = new Random();
        int randomIndex = random.nextInt(deck.size());
        JSONObject JSONcard = (JSONObject) deck.get(randomIndex);
        deck.remove(randomIndex);
        if(deck == currentGame.resourceDeck){
            return craftResourceCard(JSONcard);
        } else if(deck == currentGame.goldenDeck){
            return craftGoldenCard(JSONcard);
        } else if(deck == currentGame.startingDeck){
            return craftStartingCard(JSONcard);
        }
        return null;
    }

    /**
     * Draws a random GoalCard from the deck, which can be either a ResourcesGoalCard or a PatternGoalCard.
     *
     * @return The GoalCard object drawn from the deck and instantiated.
     */
    public GoalCard drawGoalFromDeck(){
        Random random = new Random();
        int randomIndex;
        int whichDeck = random.nextInt(2);
        if(whichDeck == 0){
            if(!currentGame.resourcesGoalDeck.isEmpty()){
                randomIndex = random.nextInt(currentGame.resourcesGoalDeck.size());
                JSONObject JSONcard = (JSONObject) currentGame.resourcesGoalDeck.get(randomIndex);
                currentGame.resourcesGoalDeck.remove(randomIndex);
                return craftResourcesGoalCard(JSONcard);
            } else {
                randomIndex = random.nextInt(currentGame.patternGoalDeck.size());
                JSONObject JSONcard = (JSONObject) currentGame.patternGoalDeck.get(randomIndex);
                currentGame.patternGoalDeck.remove(randomIndex);
                return craftPatternGoalCard(JSONcard);
            }
        } else {
            if(!currentGame.patternGoalDeck.isEmpty()){
                randomIndex = random.nextInt(currentGame.patternGoalDeck.size());
                JSONObject JSONcard = (JSONObject) currentGame.patternGoalDeck.get(randomIndex);
                currentGame.patternGoalDeck.remove(randomIndex);
                return craftPatternGoalCard(JSONcard);
            } else {
                randomIndex = random.nextInt(currentGame.resourcesGoalDeck.size());
                JSONObject JSONcard = (JSONObject) currentGame.resourcesGoalDeck.get(randomIndex);
                currentGame.resourcesGoalDeck.remove(randomIndex);
                return craftResourcesGoalCard(JSONcard);
            }
        }
    }

    /**
     * Crafts a ResourcesGoalCard object from a JSON object, passed by parameter.
     *
     * @param JSONcard The JSON object from which the GoalCard is instantiated.
     * @return The ResourcesGoalCard object crafted from the JSON object.
     */
    public GoalCard craftResourcesGoalCard(JSONObject JSONcard){
        String UUID = (String) JSONcard.get("UUID");
        int points = ((Number) JSONcard.get("points")).intValue();
        Resource[] resources = new Resource[3];
        Map<Resource, Integer> resourcesMap = new HashMap<>();
        JSONArray JSONResources = (JSONArray) JSONcard.get("resources");
        for(int i = 0; i < 3; i++){
            resources[i] = stringToResource((String) JSONResources.get(i));
        }
        for(int i = 0; i < 3; i++){
            if(resources[i] != null)
            {
                if(resourcesMap.containsKey(resources[i])){
                    resourcesMap.put(resources[i], resourcesMap.get(resources[i]) + 1);
                } else {
                    resourcesMap.put(resources[i], 1);
                }
            }
        }
        return new ResourcesGoalCard(points, resourcesMap, UUID);
    }

    /**
     * Crafts a PatternGoalCard object from a JSON object, passed by parameter.
     *
     * @param JSONcard The JSON object from which the GoalCard is instantiated.
     * @return The PatternGoalCard object crafted from the JSON object.
     */
    public GoalCard craftPatternGoalCard(JSONObject JSONcard){
        String UUID = (String) JSONcard.get("UUID");
        int points = ((Number) JSONcard.get("points")).intValue();
        JSONArray JSONPattern = (JSONArray) JSONcard.get("pattern");
        JSONArray JSONResources = (JSONArray) JSONcard.get("resources");
        int[] pattern = new int[6];
        Resource[] resources = new Resource[3];
        for(int i = 0; i < 6; i++){
            pattern[i] = ((Number) JSONPattern.get(i)).intValue();
        }
        for(int i = 0; i < 3; i++){
            resources[i] = stringToResource((String) JSONResources.get(i));
        }
        return new PatternGoalCard(points, pattern, resources, UUID);
    }

    /**
     * Crafts a ResourceCard object from a JSON object, passed by parameter.
     *
     * @param JSONcard The JSON object from which the PlayableCard is instantiated.
     * @return The ResourceCard object crafted from the JSON object.
     */
    public PlayableCard craftResourceCard(JSONObject JSONcard){
        String UUID = (String) JSONcard.get("UUID");
        int points = ((Number) JSONcard.get("points")).intValue();
        Resource permRes = stringToResource((String) JSONcard.get("permRes"));
        JSONArray JSONCorners = (JSONArray) JSONcard.get("corners");
        PlayableCard card = new ResourceCard(new Resource[]{permRes}, null, points, UUID);
        card.setCorners(craftCornerArray(JSONCorners, card));
        return card;
    }

    /**
     * Crafts a GoldenCard object from a JSON object, passed by parameter.
     *
     * @param JSONcard The JSON object from which the PlayableCard is instantiated.
     * @return The GoldenCard object crafted from the JSON object.
     */
    public PlayableCard craftGoldenCard(JSONObject JSONcard){
        Object rule;
        String UUID = (String) JSONcard.get("UUID");
        int points = ((Number) JSONcard.get("points")).intValue();
        Resource permRes = stringToResource((String) JSONcard.get("permRes"));
        JSONArray JSONCorners = (JSONArray) JSONcard.get("corners");
        String temp = (String) JSONcard.get("rule");
        if(temp.equals("CORNERS") || temp.equals("NONE")){
            rule = temp;
        } else {
            rule = stringToResource((String) JSONcard.get("rule"));
        }
        JSONArray JSONRequire = (JSONArray) JSONcard.get("require");
        ArrayList<Resource> require = new ArrayList<>();
        for(int i = 0; i < JSONRequire.size(); i++){
            require.add(stringToResource((String) JSONRequire.get(i)));
        }
        PlayableCard card = new GoldenCard(new Resource[]{permRes}, null, points, require, rule, UUID);
        card.setCorners(craftCornerArray(JSONCorners, card));
        return card;
    }

    /**
     * Crafts a StartingCard object from a JSON object, passed by parameter.
     *
     * @param JSONcard The JSON object from which the PlayableCard is instantiated.
     * @return The StartingCard object crafted from the JSON object.
     */
    public PlayableCard craftStartingCard(JSONObject JSONcard){
        String UUID = (String) JSONcard.get("UUID");
        JSONArray JSONpermRes = (JSONArray) JSONcard.get("permRes");
        Resource[] permRes = new Resource[JSONpermRes.size()];
        for(int i = 0; i < JSONpermRes.size(); i++){
            permRes[i] = stringToResource((String) JSONpermRes.get(i));
        }
        JSONArray JSONFrontCorners = (JSONArray) JSONcard.get("frontCorners");
        JSONArray JSONBackCorners = (JSONArray) JSONcard.get("backCorners");
        PlayableCard card = new StartingCard(permRes, null, null, UUID);
        ((StartingCard) card).setFrontCardCorners(craftCornerArray(JSONFrontCorners, card));
        ((StartingCard) card).setBackCardCorners(craftCornerArray(JSONBackCorners, card));
        return card;
    }

    /**
     * Converts a string to a Resource object.
     *
     * @param resource The string to be converted.
     * @return The Resource object converted from the string.
     */
    public Resource stringToResource(String resource){
        if(resource == null){
            return null;
        }
        return switch (resource) {
            case "WOLF" -> Resource.WOLF;
            case "MUSHROOM" -> Resource.MUSHROOM;
            case "LEAF" -> Resource.LEAF;
            case "BUTTERFLY" -> Resource.BUTTERFLY;
            case "FEATHER" -> Resource.FEATHER;
            case "SCROLL" -> Resource.SCROLL;
            case "GLASSVIAL" -> Resource.GLASSVIAL;
            default -> null;
        };
    }

    /**
     * Crafts a Corner array from a JSON array and a PlayableCard object, passed by parameter.
     *
     * @param JSONCorners The JSON array from which the Corner array is instantiated.
     * @param card The PlayableCard object to which the Corner array belongs.
     * @return The Corner array crafted from the JSON array.
     */
    public Corner[] craftCornerArray(JSONArray JSONCorners, PlayableCard card){
        Corner[] corners = new Corner[4];
        for(int i = 0; i < 4; i++){
            if(JSONCorners.get(i) == null){
                corners[i] = null;
            } else if(JSONCorners.get(i).equals("EMPTY")){
                corners[i] = new Corner(i, Optional.empty());
            } else {
                corners[i] = new Corner(i, Optional.ofNullable(stringToResource((String) JSONCorners.get(i))));
            }
        }
        return corners;
    }

    /**
     * Draws a card picked from the viewable cards, which can be either a golden card, a resource card or a card on the top of the decks.
     * Also, replaces the drawn card with a new one.
     *
     * @return The picked PlayableCard object drawn from the viewable cards.
     */
    public PlayableCard drawViewableCard(boolean whichType, int whichCard){
        //WhichType: true = golden, false = resource
        //WhichCard: 0 =first visible card, 1 = second visible card, 2 = top of the deck.
        if(whichCard < 0 || whichCard > 2){
            throw new IllegalArgumentException("Invalid card index!");
        }
        PlayableCard card;

        //golden card
        if(whichType){
            card = currentGame.getViewableGoldenCards()[whichCard];
        }
        //resource card
        else {
            card = currentGame.getViewableResourceCards()[whichCard];
        }

        //set the new card
        setNewViewableCard(whichType, whichCard);
        return card;
    }

    /**
     * Sets a new viewable card in the deck and position specified by the parameters.
     *
     * @param type The type of the card to be set, true if it's a golden card, false if it's a resource card.
     * @param index The index of the card to be set.
     */
    public void setNewViewableCard(boolean type, int index) {
        if(type){
            try {
                currentGame.viewableGoldenCards[index] = currentGame.viewableGoldenCards[2];
                currentGame.viewableGoldenCards[2] = (GoldenCard) drawPlayableFromDeck(currentGame.goldenDeck);
            }catch (IllegalArgumentException e){
                currentGame.viewableGoldenCards[2] = null;
            }
        } else {
            try{
                currentGame.viewableResourceCards[index] = currentGame.viewableResourceCards[2];
                currentGame.viewableResourceCards[2] = (ResourceCard) drawPlayableFromDeck(currentGame.resourceDeck);
            }catch (IllegalArgumentException e){
                currentGame.viewableResourceCards[2] = null;
            }
        }
    }

    /**
     * Sets the two random common goal cards of the game.
     */
    public void setCommonGoalCards(){
        currentGame.commonGoalCards[0] = drawGoalFromDeck();
        currentGame.commonGoalCards[1] = drawGoalFromDeck();
    }

    /**
     * Adds a player to the game.
     *
     * @param p The player to be added to the game.
     */
    public void addPlayer(Player p){
        ArrayList<Player> players = currentGame.getPlayers();
        for(int n = 0 ; n < currentGame.getPlayers().size(); n++) {
            if (currentGame.getPlayers().get(n) == null) {
                players.add(n, p);
                players.remove(n + 1);
                currentGame.setPlayers(players);
                switch (n) {
                    case 0 -> players.get(n).setPawn(Pawn.RED);
                    case 1 -> players.get(n).setPawn(Pawn.GREEN);
                    case 2 -> players.get(n).setPawn(Pawn.YELLOW);
                    case 3 -> players.get(n).setPawn(Pawn.BLUE);
                }
                break;
            }
        }
    }

    /**
     * Player's hand getter.
     *
     * @param player The player whose hand is to be returned.
     * @return The PlayableCard array representing the player's hand.
     * @throws IllegalArgumentException In case the player is null or not in the game.
     */
    public PlayableCard[] returnHand(Player player){
        if(player == null || !currentGame.getPlayers().contains(player)) {
            throw new IllegalArgumentException("Illegal player parameter!");
        }
        return player.getHand();
    }

    /**
     * Places a card on the player's board, at the position passed by parameter.
     *
     * @param x The X coordinate of the position where the card is to be placed.
     * @param y The Y coordinate of the position where the card is to be placed.
     * @param card The card to be placed on the board.
     * @param player The player who is placing the card.
     * @throws IllegalArgumentException In case the position is invalid.
     * @throws IllegalAccessException In case the card can't be placed on the board.
     */
    public void placeCard(int x, int y, PlayableCard card, Player player) throws IllegalArgumentException, IllegalAccessException {
        if (card instanceof StartingCard) {
            player.getPlayerBoard().placeCard(card, 40, 40);
        } else if (player.getPlayerBoard().getCard(x, y) != null && player.getPlayerBoard().getCard(x, y).getState() == State.AVAILABLE) {
                //if the card is flipped I just place it on the board
                if(card.isFlipped()){
                    player.getPlayerBoard().placeCard(card, x, y);
                }
                //if it's a golden card not flipped I need to check its rules
                else if(card instanceof GoldenCard){

                    if(!player.getPlayerBoard().checkGoldenCardRequirements((GoldenCard) card)) {
                        throw new IllegalArgumentException("You don't have the required resources to place this card!");
                    }

                    switch (card.getRule().toString()) {
                        case "NONE":
                            player.getPlayerBoard().placeCard(card, x, y);
                            player.addScore(card.getPoints());
                            break;
                        case "CORNERS":
                            int covered = player.getPlayerBoard().placeCard(card, x, y);
                            player.addScore(covered * card.getPoints());
                            break;
                        default:
                            String s = card.getRule().toString();
                            int occurency = 1;
                            for (Resource element : player.getPlayerBoard().getResources()) {
                                if (element == Resource.valueOf(s)) {
                                    occurency++;
                                }
                            }
                            player.getPlayerBoard().placeCard(card, x, y);
                            player.addScore(occurency * card.getPoints());
                            break;
                    }
                }
                //If it's a resource card not flipped I just place it and update the score
                else{
                    player.getPlayerBoard().placeCard(card, x, y);
                    player.addScore(card.getPoints());
                }
        } else {
            throw new IllegalArgumentException("You can't place a card here!");
        }
    }

    /**
     * Shows the available positions on the board where the player can place a card.
     *
     * @param player The player whose available positions are to be shown.
     * @return The list of int arrays representing the available positions.
     */
    public List<int[]> showAvailableOnBoard(Player player){
        List<int[]> availablePosition = new ArrayList<>();
        for (int i = 0; i <= 80; i++) {
            for (int j = 0; j <= 80; j++) {
                if (player.getPlayerBoard().getState(i,j) == State.AVAILABLE) {
                    availablePosition.add(new int[]{i, j});
                }
            }
        }
        return availablePosition;
    }

    /**
     * Sets the number of players in the game.
     *
     * @param number The number of players to be set.
     * @throws IllegalArgumentException In case the number of players is invalid.
     */
    public void setNumberOfPlayers(int number) throws IllegalArgumentException {
        if(number < 5 && number > 1) {
            ArrayList<Player> list = new ArrayList<>(); //(number);
            for(int i = 0; i< number; i++){
                list.add(null);
            }
            currentGame.setPlayers(list);
        }else{
            System.err.println("Set a valid number of players!");
            throw new IllegalArgumentException("Position not valid, check the board for valid positions!");
        }
    }

    /**
     * Draws two goal cards from the deck.
     *
     * @return The array of GoalCard objects drawn from the deck.
     */
    public GoalCard[] drawGoalCardsToChoose(){
        GoalCard[] cards = new GoalCard[2];
        cards[0] = drawGoalFromDeck();
        cards[1] = drawGoalFromDeck();
        return cards;
    }

    /**
     * Performs a series of useful actions to begin the game, setting the viewable cards, the players' hands and the common goal cards.
     */
    public void initializeGame() {
        ///Initialize the viewable cards
        for(int i = 2; i >= 0 ; i--){
            setNewViewableCard(false, i);
            setNewViewableCard(true, i);
        }

        //For each player
        for(Player p: currentGame.getPlayers()){
            //Set the hand
            //Golden card
            p.setHand(drawPlayableFromDeck(currentGame.goldenDeck), 0);
            //Resource card
            p.setHand(drawPlayableFromDeck(currentGame.resourceDeck), 1);
            p.setHand(drawPlayableFromDeck(currentGame.resourceDeck), 2);
        }
        ///Set cards
        setCommonGoalCards();
    }

    /**
     * Draws the cards for the player to choose before starting the game.
     *
     * @param p The player who has to choose the cards.
     * @return The array of strings representing the UUIDs of the cards to be chosen.
     */
    public String[] cardToChoose(Player p){
        StartingCard tempSC = (StartingCard) drawPlayableFromDeck(currentGame.startingDeck);
        GoalCard[] temp = drawGoalCardsToChoose();

        //Send the cards to choose
        p.setCardToChoose(new Object[]{
                tempSC,
                temp[0],
                temp[1]
        });

        //Send to server the cards UUID
        return new String[]{
                tempSC.getUUID(),
                temp[0].getUUID(),
                temp[1].getUUID()
        };
    }

    /**
     * Returns the current game.
     *
     * @return The Game object representing the current game.
     */
    public Game getCurrentGame() {
        return currentGame;
    }

    /**
     * Checks if it's time to begin the end game phase flow.
     *
     * @return True if the end game phase has to be begun, false otherwise.
     */
    public boolean checkEndGamePhase(){
        return (currentGame.viewableResourceCards[2] == null && currentGame.viewableGoldenCards[2] == null) || checkPlayersScore();
    }

    /**
     * Checks if a player has reached a score of 20 points.
     *
     * @return True if a player has reached a score of 20 points, false otherwise.
     */
    public boolean checkPlayersScore(){
        for(int i = 0; i < currentGame.getPlayers().size(); i++){
            if(currentGame.getPlayers().get(i).getScore() >= 20)
                return true;
        }
        return false;
    }

    /**
     * This method advances the player turn to the next player.
     *
     * @return The clientPort of the player whose turn is next.
     */
    public int advancePlayerTurn(){
        //Pick current player index
        int index = currentGame.getPlayers().indexOf(currentGame.getCurrentPlayer());
        //Set the next player
        currentGame.setCurrentPlayer(
                currentGame.getPlayers().get(
                //Loopback case
                (index + 1) % currentGame.getPlayers().size()
                )
        );
        return currentGame.getCurrentPlayer().clientPort;
    }
}