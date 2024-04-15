package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.util.*;

public class GameController {
    private Game currentGame;

    public GameController() {
        this.currentGame = null;
    }

    //Draws a card from the deck passed by parameter
    public PlayableCard drawPlayableFromDeck(JSONArray deck){
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
            JSONObject JSONcard = (JSONObject) currentGame.resourcesGoalDeck.get(randomIndex);
            currentGame.resourcesGoalDeck.remove(randomIndex);
            return craftResourcesGoalCard(JSONcard);
        } else {
            JSONObject JSONcard = (JSONObject) currentGame.patternGoalDeck.get(randomIndex);
            currentGame.patternGoalDeck.remove(randomIndex);
            return craftPatternGoalCard(JSONcard);
        }
    }

    public GoalCard craftResourcesGoalCard(JSONObject JSONcard){
        String UUID = (String) JSONcard.get("UUID");
        int points = (Integer) JSONcard.get("points");
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

    public GoalCard craftPatternGoalCard(JSONObject JSONCard){
        String UUID = (String) JSONCard.get("UUID");
        int points = (Integer) JSONCard.get("points");

        //TODO

        return null;
    }

    public PlayableCard craftResourceCard(JSONObject JSONcard){
        String UUID = (String) JSONcard.get("UUID");
        int points = (Integer) JSONcard.get("points");
        Resource permRes = stringToResource((String) JSONcard.get("permRes"));
        JSONArray JSONCorners = (JSONArray) JSONcard.get("corners");
        PlayableCard card = new ResourceCard(new Resource[]{permRes}, null, points, UUID);
        card.setCorners(craftCornerArray(JSONCorners, card));
        return card;
    }

    public PlayableCard craftGoldenCard(JSONObject JSONcard){
        String UUID = (String) JSONcard.get("UUID");
        int points = (Integer) JSONcard.get("points");
        Resource permRes = stringToResource((String) JSONcard.get("permRes"));
        JSONArray JSONCorners = (JSONArray) JSONcard.get("corners");
        Resource rule = stringToResource((String) JSONcard.get("rule"));
        JSONArray JSONRequire = (JSONArray) JSONcard.get("require");
        Resource[] require = new Resource[3];
        for(int i = 0; i < 3; i++){
            require[i] = stringToResource((String) JSONRequire.get(i));
        }
        PlayableCard card = new GoldenCard(new Resource[]{permRes}, null, points, require, rule, UUID);
        card.setCorners(craftCornerArray(JSONCorners, card));
        return card;
    }

    public PlayableCard craftStartingCard(JSONObject JSONcard){
        String UUID = (String) JSONcard.get("UUID");
        Resource permRes = stringToResource((String) JSONcard.get("permRes"));
        JSONArray JSONFrontCorners = (JSONArray) JSONcard.get("frontCorners");
        JSONArray JSONBackCorners = (JSONArray) JSONcard.get("backCorners");
        PlayableCard card = new StartingCard(new Resource[]{permRes}, null, null, UUID);
        ((StartingCard) card).setFrontCardCorners(craftCornerArray(JSONFrontCorners, card));
        ((StartingCard) card).setBackCardCorners(craftCornerArray(JSONBackCorners, card));
        return card;
    }

    public Resource stringToResource(String resource){
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
            } else if(JSONCorners.get(i) == "EMPTY"){
                corners[i] = new Corner(i, card, Optional.empty());
            } else {
                corners[i] = new Corner(i, card, Optional.ofNullable(stringToResource((String) JSONCorners.get(i))));
            }
        }
        return corners;
    }

    //Draws a card from the two common cards on the table of the specified type and set to null the related array cell
    //type==0: resourceCard, type==1: goldenCard
    //card==0: "first" card, card==1: "second" card
    public PlayableCard drawFromViewable(boolean whichType, boolean whichCard){
        PlayableCard card;
        if(whichType){
            if(whichCard){
                card = currentGame.getViewableGoldenCards()[1];
                currentGame.viewableGoldenCards[1] = null;
            } else {
                card = currentGame.getViewableGoldenCards()[0];
                currentGame.viewableGoldenCards[0] = null;
            }
        } else {
            if(whichCard){
                card = currentGame.getViewableResourceCards()[1];
                currentGame.viewableResourceCards[1] = null;
            } else {
                card = currentGame.getViewableResourceCards()[0];
                currentGame.viewableResourceCards[0] = null;
            }
        }
        return card;
    }

    /* not very clear how we can add a new player if the array is private and there's no setPlayer function,
    as implemented now it overwrites all the names,
     not sure how we want to implement the port now it`s a random 50 just to have it working
     we need to choose the exception
     */
    public void addPlayer(String nickname, int clientPort) throws SecurityException{

        /* check if player already exists */

        if (currentGame.getCurrentPlayer().clientPort != clientPort){
                throw new SecurityException("wrong clientPort");
        }
        if (currentGame.getPlayers().stream().map(Player::getNickname).toList().contains(nickname)){
            throw new SecurityException("player already present");
        }
        /* if does not exist adds it to the Arraylist */
        else {
            ArrayList<Player> players = currentGame.getPlayers();
            int n;
            for(n=0 ; n < currentGame.getPlayers().size(); n++) {
                if (currentGame.getPlayers().get(n) == null) {
                    players.add(n, new Player(nickname, clientPort));
                    currentGame.setPlayers(players);
                    break;
                }

            }
            if(n>=currentGame.getPlayers().size()){
                throw new SecurityException(" the lobby is full");
            }
        }
    }

    /*
    not very clear if there has to be some additional function in this method very useful?
     */
    public PlayableCard[] returnHand(Player player){
        if (player != null){
            return player.getHand();
        }
    return null;}


    /*
    checks if a position is available and the card is not null, then place it
     */
    public void placeCard(int i,int j,PlayableCard card,Player player) throws SecurityException {
        if (card.getClass().getName().equals("StartingCard")&& (i!=0 || j!=0)){
            throw new SecurityException("can't place starting card anywhere else from 0, 0");
        }
        if (card != null && player.getPlayerBoard().getCard(i,j).getState()==State.AVAILABLE){
            player.getPlayerBoard().placeCard(card, i, j);
        }
    }


    /*
    iterate all the cell of the board, returns them if they are available
     */
    public List<int[]> showAvailableOnBoard(Player player){
    List<int []> availablePosition = new ArrayList<>();
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
        for (GoalCard gc: currentGame.getCommonGoalCards()){
            player.setScore(player.getScore() + gc.checkGoal(player.getPlayerBoard()));
        }
        //secondly secret goal
        player.setScore(player.getScore() + player.getSecretGoalCard().checkGoal(player.getPlayerBoard()));
    }

    public PlayerBoard viewPlayerBoard(Player p){
        return p.getPlayerBoard();
    }

    public void setNumberOfPlayers(int number) throws IllegalArgumentException{
        if(number <5 && number > 0) {
            ArrayList<Player> list = new ArrayList<>(number);
            for(int i = 0; i< number; i++){
                list.add(i, null);
            }
            currentGame.setPlayers(list);
        }else{
            System.err.println("Set a valid number of players!");
            throw new IllegalArgumentException();
        }
    }

    /**
     * This method lets a player choose his secretGoalCard
     *
     * @param p the player
     * @param lis the list of goal cards he can choose from
     * @param number the card he chose
     */
    public void chooseSecretGoal(Player p, ArrayList<GoalCard> lis, int number){
        p.setSecretGoalCard(lis.get(number));
    }

    /**
     * The method beginGame initiates a new game by calling the Game constructor
     * and waits for the players to connect to the game
     * @throws FileNotFoundException In case the files of the old game have been lost
     */

    //Maybe it should do something else, right now I don't know
    public void beginGame() throws FileNotFoundException {
        this.currentGame = new Game();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert the number of players: ");
        setNumberOfPlayers(scanner.nextInt());
        scanner.close();
        while(currentGame.getPlayers().contains(null)){
            //We have to wait for everyone to connect
        }
        System.out.println("\nEverything is set up!");
    }

    //currentGame setter
    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }
}