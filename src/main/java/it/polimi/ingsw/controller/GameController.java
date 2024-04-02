package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController {
    private final Game currentGame;

    public GameController(Game currentGame) {
        this.currentGame = currentGame;
    }

    //Draws a card from the deck passed by parameter
    public Object drawFromDeck(JSONArray deck){
        Random random = new Random();
        int randomIndex = random.nextInt(deck.size());
        JSONObject JSONcard = (JSONObject) deck.get(randomIndex);
        deck.remove(randomIndex);

        //TODO: parse the JSON object and craft the card

        return null; //So IDE doesn't give error
    }

    /**
     * This method draws a card from the ones which are positioned on the table with their faces uncovered,
     * and it substitutes the viewable card picked up with a new card from the deck
     *
     * @param i which deck to choose from
     * @param chosen which card the player wants to pick up between the 2 available
     * @return the card
     */

    //We might have to add a method getDeck in Game
    public PlayableCard drawFromViewable(boolean i, int chosen){
        PlayableCard card;
        if(i){
            //draw from resource cards
            card = currentGame.viewableResourceCards[chosen];
            ResourceCard[] newviewable = currentGame.getViewableResourceCards();
            newviewable[chosen] = drawFromDeck(resourceDeck);
            currentGame.setViewableResourceCards(newviewable);
        }else{
            //draw from golden cards
            card = currentGame.viewableGoldenCards[chosen];
            GoldenCard[] newviewable = currentGame.getViewableGoldenCards();
            newviewable[chosen] = drawFromDeck(goldenDeck);
            currentGame.setViewableGoldenCards(newviewable);
        }
        return card;
    }

    /* not very clear how we can add a new player if the array is private and there's no setPlayer function,
    as implemented now it overwrites all the names,
     not sure how we want to implement the port now it`s a random 50 just to have it working
     we need to choose the exception
     */
    public void addPlayer(String nickname) throws SecurityException{
        /* check if player already exists */
        if (currentGame.getPlayers().stream().map(Player::getNickname).toList().contains(nickname)){
            throw new SecurityException("player already present");
        }
        /* if does not exist adds it to the Arraylist */
        else {
            ArrayList<Player> players = currentGame.getPlayers();
            players.add(new Player(nickname,50));
            currentGame.setPlayers(players);
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
    public void placeCard(int i,int j,PlayableCard card,Player player) {
        if (player.getPlayerBoard().getCard(i,j).getState()==State.AVAILABLE && card != null){
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

 // needs some more detail, really useful?
    public PlayableCard[] showViewable(boolean type){
        if(type){return currentGame.getViewableGoldenCards();
        }
        else{ return currentGame.getViewableResourceCards();
        }
    }

    public Resource showResourceFromDeck (boolean type){
     return null;
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



}


