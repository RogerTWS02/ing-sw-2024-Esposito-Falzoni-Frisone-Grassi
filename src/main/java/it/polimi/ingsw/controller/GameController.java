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
    public PlayableCard drawFromDeck(JSONArray deck){
        Random random = new Random();
        int randomIndex = random.nextInt(deck.size());
        JSONObject JSONcard = (JSONObject) deck.get(randomIndex);
        deck.remove(randomIndex);

        //TODO: parse the JSON object and craft the card

        return null; //So IDE doesn't give error
    }

    //Draws a card from the two common cards on the table of the specified type
    //type==0: resourceCard, type==1: goldenCard
    //card==0: "first" card, card==1: "second" card
    public PlayableCard drawFromViewable(boolean whichType, boolean whichCard){
        PlayableCard card;
        if(whichType){
            if(whichCard){
                card = currentGame.getViewableGoldenCards()[1];
            } else {
                card = currentGame.getViewableGoldenCards()[0];
            }
        } else {
            if(whichCard){
                card = currentGame.getViewableResourceCards()[1];
            } else {
                card = currentGame.getViewableResourceCards()[0];
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
            players.add(new Player(nickname,clientPort));
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
 // we already have this method in Game, so I don't think so
    public PlayableCard[] showViewable(boolean type){
        if(type){return currentGame.getViewableGoldenCards();
        }
        else{ return currentGame.getViewableResourceCards();
        }
    }

    //Doesn't the view already show the top card of the deck?
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

    //Not sure that we need this one, already present in Player class
    public PlayerBoard viewPlayerBoard(Player p){
        return p.getPlayerBoard();
    }

    public void setNumberOfPlayers(int number){
        if(number <5 && number > 0) {
            ArrayList<Player> list = new ArrayList<>(number);
            for(int i = 0; i< number; i++){
                list.add(i, null);
            }
            currentGame.setPlayers(list);
        }else{
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

}


