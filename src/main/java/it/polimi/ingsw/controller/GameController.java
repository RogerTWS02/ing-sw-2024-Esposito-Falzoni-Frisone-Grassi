package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class GameController {
    private final Game currentGame;

    public GameController() throws FileNotFoundException {
        this.currentGame = new Game();
    }

    //Draws a card from the deck passed by parameter
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

    public GoalCard drawGoalFromDeck(){
        Random random = new Random();
        int randomIndex = random.nextInt(2);
        if(randomIndex == 0){
            if(!currentGame.resourcesGoalDeck.isEmpty()){
                JSONObject JSONcard = (JSONObject) currentGame.resourcesGoalDeck.get(randomIndex);
                currentGame.resourcesGoalDeck.remove(randomIndex);
                return craftResourcesGoalCard(JSONcard);
            } else {
                JSONObject JSONcard = (JSONObject) currentGame.patternGoalDeck.get(randomIndex);
                currentGame.patternGoalDeck.remove(randomIndex);
                return craftPatternGoalCard(JSONcard);
            }
        } else {
            if(!currentGame.patternGoalDeck.isEmpty()){
                JSONObject JSONcard = (JSONObject) currentGame.patternGoalDeck.get(randomIndex);
                currentGame.patternGoalDeck.remove(randomIndex);
                return craftPatternGoalCard(JSONcard);
            } else {
                JSONObject JSONcard = (JSONObject) currentGame.resourcesGoalDeck.get(randomIndex);
                currentGame.resourcesGoalDeck.remove(randomIndex);
                return craftResourcesGoalCard(JSONcard);
            }
        }
    }

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

    public PlayableCard craftResourceCard(JSONObject JSONcard){
        String UUID = (String) JSONcard.get("UUID");
        int points = ((Number) JSONcard.get("points")).intValue();
        Resource permRes = stringToResource((String) JSONcard.get("permRes"));
        JSONArray JSONCorners = (JSONArray) JSONcard.get("corners");
        PlayableCard card = new ResourceCard(new Resource[]{permRes}, null, points, UUID);
        card.setCorners(craftCornerArray(JSONCorners, card));
        return card;
    }

    public PlayableCard craftGoldenCard(JSONObject JSONcard){
        Object rule;
        String UUID = (String) JSONcard.get("UUID");
        int points = ((Number) JSONcard.get("points")).intValue();
        Resource permRes = stringToResource((String) JSONcard.get("permRes"));
        JSONArray JSONCorners = (JSONArray) JSONcard.get("corners");
        String temp = (String) JSONcard.get("rule");
        if(temp != null && temp.equals("CORNERS")){
            rule = "CORNERS";
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

    public Corner[] craftCornerArray(JSONArray JSONCorners, PlayableCard card){
        Corner[] corners = new Corner[4];
        for(int i = 0; i < 4; i++){
            if(JSONCorners.get(i) == null){
                corners[i] = null;
            } else if(JSONCorners.get(i).equals("EMPTY")){
                corners[i] = new Corner(i, card, Optional.empty());
            } else {
                corners[i] = new Corner(i, card, Optional.ofNullable(stringToResource((String) JSONCorners.get(i))));
            }
        }
        return corners;
    }

    //Draws a card from the two common cards on the table of the specified type,
    //or the card on the top of the specified deck, and replace it if possible
    //
    //type==0: resourceCard, type==1: goldenCard
    //card==0: "first" card, card==1: "second" card, card==2: card on the top of the deck
    public PlayableCard drawViewableCard(boolean whichType, int whichCard){
        if(whichCard < 0 || whichCard > 2){
            throw new IllegalArgumentException("Invalid card index!");
        }
        PlayableCard card;
        if(whichType){
                card = currentGame.getViewableGoldenCards()[whichCard];
                currentGame.getViewableGoldenCards()[whichCard] = null;
        } else {
                card = currentGame.getViewableResourceCards()[whichCard];
                currentGame.getViewableResourceCards()[whichCard] = null;
        }
        setNewViewableCard(whichType, whichCard);
        return card;
    }

    //type == 1: goldenCard, type == 0: resourceCard
    public void setNewViewableCard(boolean type, int index) {
        if(type){
            currentGame.viewableGoldenCards[index] = (GoldenCard) drawPlayableFromDeck(currentGame.goldenDeck);
        } else {
            currentGame.viewableResourceCards[index] = (ResourceCard) drawPlayableFromDeck(currentGame.resourceDeck);
        }
    }

    public void setCommonGoalCards(){
        currentGame.commonGoalCards[0] = drawGoalFromDeck();
        currentGame.commonGoalCards[1] = drawGoalFromDeck();

    }

    public void addPlayer(String nickname, int clientPort) throws SecurityException {
        /* check if player already exists */
        for(int p = 0; p < currentGame.getPlayers().size(); p++){
            if(currentGame.getPlayers().get(p) != null && currentGame.getPlayers().get(p).getNickname().equals(nickname)){
                throw new SecurityException("Player already exists!");
            }
        }
        ArrayList<Player> players = currentGame.getPlayers();
        for(int n = 0 ; n < currentGame.getPlayers().size(); n++) {
            if (currentGame.getPlayers().get(n) == null) {
                players.add(n, new Player(nickname, clientPort));
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

    public PlayableCard[] returnHand(Player player){
        if(player == null || !currentGame.getPlayers().contains(player)) {
            throw new IllegalArgumentException("Illegal player parameter!");
        }
        return player.getHand();
    }

    /*
    Checks if a position is available and the card is not null, then place it,
    check if the card contains some rule to obtain the points and then update the score
     */
    public void placeCard(int i,int j,PlayableCard card,Player player) throws SecurityException {
        try {
            if (i < 0 || i > 80 || j < 0 || j > 80) {
                throw new IllegalArgumentException("Invalid position!");
            }
            if (card instanceof StartingCard) {
                player.getPlayerBoard().placeCard(card, 40, 40);
            } else {
                if (player.getPlayerBoard().getCard(i, j).getState() == State.AVAILABLE) {

                    switch (card.getRule().toString()) {
                        case "NONE":
                            player.getPlayerBoard().placeCard(card, i, j);
                            player.addScore(card.getPoints());
                        case "CORNERS":
                            int covered = player.getPlayerBoard().placeCard(card, i, j);
                            player.addScore(covered * card.getPoints());
                        default:
                            String s = card.getRule().toString();
                            int occurency = 1;
                            for (Resource element : player.getPlayerBoard().getResources()) {
                                if (element == Resource.valueOf(s)) {
                                    occurency++;
                                }
                            }
                            player.getPlayerBoard().placeCard(card, i, j);
                            player.addScore(occurency * card.getPoints());

                    }

                }
            }
        }
        catch (IllegalArgumentException e){
            System.err.println(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }


    }

    /*
    iterate all the cell of the board, returns them if they are available
     */
    public List<int[]> showAvailableOnBoard(Player player){
        List<int[]> availablePosition = new ArrayList<>();
        int[] possiblePosition = new int[2];
        for (int i = 0; i <= 80; i++) {
            for (int j = 0; j <= 80; j++) {
                if (player.getPlayerBoard().getState(i,j) == State.AVAILABLE) {
                    possiblePosition[0] = i;
                    possiblePosition[1] = j;
                    availablePosition.add(possiblePosition);
                }
            }
        }
        return availablePosition;
    }

 // update score with goal points
    public void getPointsFromGoalCards(Player player){
        // firstly common goal
        System.out.println(currentGame);
        for (GoalCard gc: currentGame.getCommonGoalCards()) {
            if (gc != null) {
                player.setScore(player.getScore() + gc.checkGoal(player.getPlayerBoard()));
            }
        }
        //secondly secret goal
        player.setScore(player.getScore() + player.getSecretGoalCard().checkGoal(player.getPlayerBoard()));
    }

    public PlayerBoard viewPlayerBoard(Player p){
        return p.getPlayerBoard();
    }

    public void setNumberOfPlayers(int number) throws IllegalArgumentException{
        if(number < 5 && number > 1) {
            ArrayList<Player> list = new ArrayList<>(); //(number);
            for(int i = 0; i< number; i++){
                list.add(null);
            }
            currentGame.setPlayers(list);
        }else{
            System.err.println("Set a valid number of players!");
            throw new IllegalArgumentException();
        }
    }

    //Returns two goal cards
    public GoalCard[] drawGoalCardsToChoose(){
        GoalCard[] cards = new GoalCard[2];
        cards[0] = drawGoalFromDeck();
        cards[1] = drawGoalFromDeck();
        return cards;
    }

    public void beginGame() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Insert the number of players: ");
        setNumberOfPlayers(Integer.parseInt(reader.readLine()));
        System.out.println("\nWaiting for players to show up...\n");
        new Thread(() -> {
            while(currentGame.getPlayers().contains(null)){
                //We have to wait for everyone to connect
            }
        }).start();

        //TODO: initialize the game flow, and some other things to set up

        System.out.println("Everything is set up!\n");
    }

    //currentGame getter
    public Game getCurrentGame() {
        return currentGame;
    }

    //Check if it's time to begin the end game phase and begin if it's time; boolean in order to make it testable
    public boolean checkEndGamePhase(){
        if((currentGame.viewableResourceCards[2] == null && currentGame.viewableGoldenCards[2] == null) || checkPlayersScore()){
            endGamePhase();
            return true;
        }
        return false;
    }

    //Check players points for checking end game phase beginning
    public boolean checkPlayersScore(){
        for(int i = 0; i < currentGame.getPlayers().size(); i++){
            if(currentGame.getPlayers().get(i).getScore() >= 20){
                return true;
            }
        }
        return false;
    }

    //End game phase handler
    public void endGamePhase(){
        //TODO: Implement end game phase flow
        currentGame.setLastPhase();
        new Thread(() -> {
            //We have to wait until the turn of the last player ends
            while(currentGame.getCurrentPlayer() != currentGame.getStartingPlayer()){
                    Thread.onSpinWait();
            }
            for(Player p : currentGame.getPlayers()) {
                for(GoalCard gc : currentGame.getCommonGoalCards()) {
                    p.setScore(p.getScore() + gc.checkGoal(p.getPlayerBoard()));
                }
            }

            currentGame.gameOver();
            }).start();
    }
}