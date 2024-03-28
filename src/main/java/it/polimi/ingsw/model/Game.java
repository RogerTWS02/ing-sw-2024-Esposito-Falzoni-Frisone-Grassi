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
    private GoalCard[] commonGoalCards;
    private Player currentPlayer;

    public Game(){
        players = new ArrayList<>();
        createDecks();
        viewableResourceCards = new ResourceCard[2];
        viewableGoldenCards = new GoldenCard[2];
        commonGoalCards = new GoalCard[2];
    }

    //Generates the decks
    public void createDecks(){
        JSONParser parser = new JSONParser();
        String[] decksNames = {"resource", "golden", "starting", "goal"};
        for(int i = 0; i < decksNames.length; i++) {
            try {
                Object JSONObject = parser.parse(new FileReader("resources/" + decksNames[i] + "Deck.json"));
                JSONArray deckJSONArray = (JSONArray) JSONObject;

                //TODO

            } catch (IOException | ParseException e) {
                System.err.println("Error in JSON file parsing!");
            }
        }
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

    //Restore a saved game
    public static Game restoreGame(){
        try(ObjectInputStream saving = new ObjectInputStream(new FileInputStream("savings/game.svs"))){
            return (Game) saving.readObject();
        } catch (IOException | ClassNotFoundException e){
            System.err.println("Error trying restoring the game!");
        }
        return null;

        //TODO: setup the game state

    }

    //Check if there's an old game saving and restore it if exists
    public static boolean checkOldGame(){
        File old = new File("savings/game.svs");
        if(old.exists()){
            return true;
        } else {
            return false;
        }
    }

   //Pick a random player and set it as the starting player
   public void setFirstPlayer(){
       Random random = new Random();
       int randomNumber = random.nextInt(players.size());
       this.startingPlayer = players.get(randomNumber);
       currentPlayer = this.startingPlayer;
   }

   //Current player getter
    public Player getCurrentPlayer(){
         return currentPlayer;
    }

   //Set common goal cards passed by parameter
    public void setCommonGoalCards(GoalCard card1, GoalCard card2){
        commonGoalCards[0] = card1;
        commonGoalCards[1] = card2;
    }

    //Common goal cards getter
    public GoalCard[] getCommonGoalCards(){
        return commonGoalCards;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
