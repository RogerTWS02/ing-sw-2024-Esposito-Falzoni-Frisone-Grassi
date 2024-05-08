package it.polimi.ingsw.model;

import java.io.*;
import java.util.Random;
import java.util.ArrayList;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.network.server.Server;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Game implements Serializable{
    private Player startingPlayer;
    private ArrayList<Player> players;
    private int gameID;
    public JSONArray resourceDeck;
    public JSONArray goldenDeck;
    public JSONArray startingDeck;
    public JSONArray resourcesGoalDeck;
    public JSONArray patternGoalDeck;
    public ResourceCard[] viewableResourceCards;
    public GoldenCard[] viewableGoldenCards;
    public  GoalCard[] commonGoalCards;
    private Player currentPlayer;
    private boolean isOver = false;
    private boolean isInLastPhase = false;

    /**.
     * This is the constructor of the game, which gets to initialize a list of players,
     * it creates decks, and also sets the visible cards on the table.
     */
    public Game(int gameID) throws FileNotFoundException {
        this.gameID = gameID;
        if(checkOldGame(gameID)){
            Game oldGame = retrieveGame(gameID);
            if (oldGame == null) {
                throw new FileNotFoundException("Unable to retrieve old saving file!");
            }
            this.startingPlayer = oldGame.startingPlayer;
            this.players = oldGame.players;
            this.resourceDeck = oldGame.resourceDeck;
            this.goldenDeck = oldGame.goldenDeck;
            this.startingDeck = oldGame.startingDeck;
            this.resourcesGoalDeck = oldGame.resourcesGoalDeck;
            this.patternGoalDeck = oldGame.patternGoalDeck;
            this.viewableResourceCards = oldGame.viewableResourceCards;
            this.viewableGoldenCards = oldGame.viewableGoldenCards;
            this.commonGoalCards = oldGame.commonGoalCards;
            this.currentPlayer = oldGame.currentPlayer;
            this.gameID = gameID;
        } else {
            createDecks();
            viewableResourceCards = new ResourceCard[3];
            viewableGoldenCards = new GoldenCard[3];
            commonGoalCards = new GoalCard[2];
        }
    }

    /**
     * This method generates all the different decks as JSONArrays.
     */
    public void createDecks(){
        JSONParser parser = new JSONParser();
        String[] decksNames = {"resource", "golden", "starting", "resourcesGoal", "patternGoal"};
        for(int i = 0; i < 5; i++) {
            try {
                InputStream input = getClass().getResourceAsStream("/" + decksNames[i] + "Deck.json");
                if (input == null) {
                    throw new FileNotFoundException("Error retrieving " + decksNames[i] + "Deck.json file!");
                }
                BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
                Object JSONObject = parser.parse(buffer);
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
                    case "resourcesGoal":
                        resourcesGoalDeck = deckJSONArray;
                        break;
                    case "patternGoal":
                        patternGoalDeck = deckJSONArray;
                        break;
                }
            } catch (IOException | ParseException e) {
                System.err.println("Error in JSON files retrieving!");
            }
        }
    }

    //Implementing persistency

    public void saveGame() throws IOException {
        //Delete old saving if it exists
        File oldGameFile = new File("savings/" + gameID + "game.svs");
        if(oldGameFile.exists()){
            deleteOldSaving();
        }
        //Create a saving folder
        File gameSavingFolder = new File("savings");
        if(!gameSavingFolder.exists()){
            if(!gameSavingFolder.mkdir()){
                throw new RuntimeException("Error creating savings folder!");
            }
        }
        //Save game
        try(ObjectOutputStream gameSave = new ObjectOutputStream(new FileOutputStream("savings/" + gameID + "game.svs"))){
            gameSave.writeObject(this);
        } catch (IOException e){
            System.err.println("Error trying saving the game!");
        }
    }

    //Deletes old saving
    public void deleteOldSaving(){
        File old = new File("savings/" + gameID + "game.svs");
        if(old.exists()){
            if(!old.delete()){
                throw new RuntimeException("Error trying deleting old saving!");
            }
        }
    }

    //Retrieve a saved game
    public static Game retrieveGame(int gameID){
        try(ObjectInputStream saving = new ObjectInputStream(new FileInputStream("savings/" + gameID + "game.svs"))){
            return (Game) saving.readObject();
        } catch (IOException | ClassNotFoundException e){
            System.err.println("Error trying restoring the game!");
        }
        return null;
    }

    //Check if there's an old game saving
    public static boolean checkOldGame(int gameID){
        File old = new File("savings/" + gameID + "game.svs");
        if(old.exists()){
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method picks a random player and sets it as the first player in the game
     */
   public void setStartingPlayer(){
       if(players == null || players.isEmpty() || players.contains(null)){
           throw new RuntimeException("No players in the game / a player can't be null!");
       }
       Random random = new Random();
       int randomNumber = random.nextInt(players.size());
       this.startingPlayer = players.get(randomNumber);
       currentPlayer = this.startingPlayer;
   }
    public void addPlayer(Player p) {
       this.players.add(p);
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

    //startingPlayer getter
    public Player getStartingPlayer(){
        return startingPlayer;
    }

    /**
     *  A call to this method is made when someone wants to get the two common goal cards.
     * @return the array of goal cards which are in common
     */
    public GoalCard[] getCommonGoalCards(){
        return commonGoalCards;
    }


    public void setCommonGoalCards(GoalCard[] commonGoalCards) {
        this.commonGoalCards = commonGoalCards;
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

    public Player getWinner(){
        Player winner = players.getFirst();
        for(Player p : players){
            if(p.getScore() > winner.getScore()){
                winner = p;
            }
        }
        return winner;
    }

    public void gameOver(){
        isOver = true;
    }

    public boolean isInLastPhase(){
        return isInLastPhase;
    }

    public boolean isGameOver(){
        return isOver;
    }

    public void setLastPhase(){
        this.isInLastPhase = true;
    }

    public int getGameID(){
        return gameID;
    }
}
