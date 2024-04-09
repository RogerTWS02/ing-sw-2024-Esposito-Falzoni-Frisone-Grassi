package it.polimi.ingsw.model;

import java.io.*;
import java.util.Random;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Game implements Serializable{
    private Player startingPlayer;
    private ArrayList<Player> players;
    public JSONArray resourceDeck;
    public JSONArray goldenDeck;
    public JSONArray startingDeck;
    public JSONArray goalDeck;
    public ResourceCard[] viewableResourceCards;
    public GoldenCard[] viewableGoldenCards;
    private final GoalCard[] commonGoalCards;
    private Player currentPlayer;

    /**
     * This is the constructor of the game, which gets to initialize a list of players,
     * it creates decks, and also sets the visible cards on the table.
     */
    public Game(){
        if(checkOldGame()){
            Game oldGame = retrieveGame();
            assert oldGame != null;
            this.startingPlayer = oldGame.startingPlayer;
            this.players = oldGame.players;
            this.resourceDeck = oldGame.resourceDeck;
            this.goldenDeck = oldGame.goldenDeck;
            this.startingDeck = oldGame.startingDeck;
            this.goalDeck = oldGame.goalDeck;
            this.viewableResourceCards = oldGame.viewableResourceCards;
            this.viewableGoldenCards = oldGame.viewableGoldenCards;
            this.commonGoalCards = oldGame.commonGoalCards;
            this.currentPlayer = oldGame.currentPlayer;
        } else {
            createDecks();
            viewableResourceCards = new ResourceCard[3];
            viewableGoldenCards = new GoldenCard[3];
            commonGoalCards = new GoalCard[2];
        }
    }

    /**
     * This method generates all the 4 different decks.
     */
    public void createDecks(){
        JSONParser parser = new JSONParser();
        String[] decksNames = {"resource", "golden", "starting", "goal"};
        for(int i = 0; i < 4; i++) {
            try {
                Object JSONObject = parser.parse(new FileReader("resources/" + decksNames[i] + "Deck.json"));
                JSONArray deckJSONArray = (JSONArray) JSONObject;
                switch (decksNames[i]) {
                    case "resource":
                        resourceDeck = deckJSONArray;
                        break;
                    case "golden":
                        goldenDeck = deckJSONArray;
                        break;
                    case "starting":
                        startingDeck = deckJSONArray;
                        break;
                    case "goal":
                        goalDeck = deckJSONArray;
                        break;
                }
            } catch (IOException | ParseException e) {
                System.err.println("Error in JSON file retrieving!");
            }
        }
    }

    //Draws a goalCard
    public GoalCard drawGoalCard(){

        //TODO: draws a goalCard and removes it from the ArrayList

        return null; //So IDE doesn't give error
    }

    //Implements persistency
    public void saveGame() throws IOException {
        //Create a saving folder
        File gameSavingFolder = new File("savings");
        if(!gameSavingFolder.exists()){
            if(!gameSavingFolder.mkdir()){
                throw new RuntimeException("Error creating savings folder!");
            }
        }
        //Save game
        try(ObjectOutputStream gameSave = new ObjectOutputStream(new FileOutputStream("savings/game.svs"))){
            gameSave.writeObject(this);
        } catch (IOException e){
            System.err.println("Error trying saving the game!");
        }
    }

    //Deletes old saving
    public void deleteOldSaving(){
        File old = new File("savings/game.svs");
        if(old.exists()){
            if(!old.delete()){
                throw new RuntimeException("Error trying deleting old saving!");
            }
        }
    }

    //Retrieve a saved game
    public static Game retrieveGame(){
        try(ObjectInputStream saving = new ObjectInputStream(new FileInputStream("savings/game.svs"))){
            return (Game) saving.readObject();
        } catch (IOException | ClassNotFoundException e){
            System.err.println("Error trying restoring the game!");
        }
        return null;
    }

    //Check if there's an old game saving
    public static boolean checkOldGame(){
        File old = new File("savings/game.svs");
        if(old.exists()){
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method picks a random player and sets it as the first player in the game
     */
   public void setFirstPlayer(){
       Random random = new Random();
       int randomNumber = random.nextInt(players.size());
       this.startingPlayer = players.get(randomNumber);
       currentPlayer = this.startingPlayer;
   }


    public void setPlayers(ArrayList<Player> players) {

       this.players = players;
    }

    /**
     * This method returns the active player on this turn.
     * @return the player
     */
    public Player getCurrentPlayer(){
        return currentPlayer;
    }

    /**
     * This method sets the common goal cards on the table.
     *
     * @param card1 the first card on the table
     * @param card2 the second card on the table
     */
    public void setCommonGoalCards(GoalCard card1, GoalCard card2){
        commonGoalCards[0] = card1;
        commonGoalCards[1] = card2;
    }

    /**
     *  A call to this method is made when someone wants to get the two common goal cards.
     * @return the array of goal cards which are in common
     */
    public GoalCard[] getCommonGoalCards(){
        return commonGoalCards;
    }

    /**
     * A call to this method is made when someone wants to get all the players in the game.
     *
     * @return the list of players who are connected to the game
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ResourceCard[] getViewableResourceCards() {
        return viewableResourceCards;
    }

    public GoldenCard[] getViewableGoldenCards() {
        return viewableGoldenCards;
    }

    //type==1: viewable card from the top of the deck; type==0: common card on the table
    public void setViewableResourceCards(ResourceCard card, boolean type) {
        if(type){
            viewableResourceCards[2] = card;
        } else {
            if(viewableResourceCards[0] == null){
                viewableResourceCards[0] = card;
            } else {
                viewableResourceCards[1] = card;
            }
        }
    }

    public void setViewableGoldenCards(GoldenCard card, boolean type) {
        if(type){
            viewableGoldenCards[2] = card;
        } else {
            if(viewableGoldenCards[0] == null){
                viewableGoldenCards[0] = card;
            } else {
                viewableGoldenCards[1] = card;
            }
        }
    }
}
