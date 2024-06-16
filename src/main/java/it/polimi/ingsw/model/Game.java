package it.polimi.ingsw.model;

import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The class Game represents a game and manages the game's data.
 */
public class Game implements Serializable {
    private Player startingPlayer;
    private ArrayList<Player> players;
    private final int gameID;
    public JSONArray resourceDeck;
    public JSONArray goldenDeck;
    public JSONArray startingDeck;
    public JSONArray resourcesGoalDeck;
    public JSONArray patternGoalDeck;
    public ResourceCard[] viewableResourceCards;
    public GoldenCard[] viewableGoldenCards;
    public GoalCard[] commonGoalCards;
    private Player currentPlayer;
    private boolean isOver = false;
    private boolean isInLastPhase = false;

    /**
     * The constructor creates a new game with the given gameID, retrieving it from a previous saving if it exists.
     *
     * @param gameID The gameID of the game, which identifies it uniquely.
     */
    public Game(int gameID) {
        this.gameID = gameID;
        /*if(checkOldGame(gameID)){
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
        } else { */
            createDecks();
            viewableResourceCards = new ResourceCard[3];
            viewableGoldenCards = new GoldenCard[3];
            commonGoalCards = new GoalCard[2];
    }

    /**
     * Instantiate the JSON array decks of the game by reading JSON files.
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


    /**
     * Saves the game in a file named after the gameID.
     *
     * @throws IOException If the file can't be saved.
     */
    /*public void saveGame() throws IOException {
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
    }*/

    /**
     * Deletes the old saving file of the game.
     */
    /*public void deleteOldSaving(){
        File old = new File("savings/" + gameID + "game.svs");
        if(old.exists()){
            if(!old.delete()){
                throw new RuntimeException("Error trying deleting old saving!");
            }
        }
    }*/

    /**
     * Retrieves the game from the file named after the gameID.
     *
     * @param gameID The gameID of the game to retrieve.
     * @return The game retrieved from the file.
     */
    /*public static Game retrieveGame(int gameID){
        try(ObjectInputStream saving = new ObjectInputStream(new FileInputStream("savings/" + gameID + "game.svs"))){
            return (Game) saving.readObject();
        } catch (IOException | ClassNotFoundException e){
            System.err.println("Error trying restoring the game!");
        }
        return null;
    }*/

    /**
     * Checks if an old game with the given gameID exists.
     *
     * @param gameID The gameID of the game to check.
     * @return true if the game exists, false otherwise.
     */
    /*public static boolean checkOldGame(int gameID){
        File old = new File("savings/" + gameID + "game.svs");
        if(old.exists()){
            return true;
        } else {
            return false;
        }
    }*/

    /**
     * Sets a random player as the starting player.
     */
    public void setStartingPlayer(){
       Random random = new Random();
       int randomNumber = random.nextInt(players.size());
       this.startingPlayer = players.get(randomNumber);
       currentPlayer = this.startingPlayer;

       //After choosing at random a starting player it's placed at the beginning of the list
       Collections.swap(players, 0, randomNumber);
   }

    /**
     * Sets an array of players as the players of the game.
     *
     * @param players The array of players to set.
     */
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    /**
     * Returns the player who is currently playing.
     *
     * @return The player who is currently playing.
     */
    public Player getCurrentPlayer(){
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Returns the starting player of the game.
     *
     * @return The starting player of the game.
     */
    public Player getStartingPlayer(){
        return startingPlayer;
    }

    /**
     * Returns the two common goal cards of the game.
     *
     * @return The two common goal cards of the game.
     */
    public GoalCard[] getCommonGoalCards(){
        return commonGoalCards;
    }

    /**
     * Returns the players of the game.
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Returns the viewable resource cards of the game.
     *
     * @return The viewable resource cards of the game.
     */
    public ResourceCard[] getViewableResourceCards() {
        return viewableResourceCards;
    }

    /**
     * Returns the viewable golden cards of the game.
     *
     * @return The viewable golden cards of the game.
     */
    public GoldenCard[] getViewableGoldenCards() {
        return viewableGoldenCards;
    }

    /**
     * Calculates the player's score, considering the common goal cards.
     */
    public void getPointsFromGoalCards() {
        for(GoalCard gc : commonGoalCards){
            for(Player p : players)
                p.setScore(p.getScore() + gc.checkGoal(p.getPlayerBoard()));
        }
        for(Player p : players)
            p.setScore(p.getScore() + p.getSecretGoalCard().checkGoal(p.getPlayerBoard()));
    }

    /**
     * Returns the player (or the players) who won the game.
     *
     * @return The player who won the game.
     */
    public Player[] getWinner(){
        getPointsFromGoalCards();
        List<Player> winners = new ArrayList<>();
        Player topPlayer = players.getFirst();

        //Find the player with the highest score
        for(Player p : players){
            if(p.getScore() > topPlayer.getScore()){
                topPlayer = p;
            }
        }

        //Check if there are more players with the same score
        for(Player p : players){
            if(p.getScore() == topPlayer.getScore()){
                winners.add(p);
            }
        }

        //If there are more players with the same score, check the number of objectives reached
        if(winners.size() > 1){
            Map<Player, Integer> objectivesReached = new HashMap<>();
            for (Player p : winners){
                int objectives = 0;
                for (GoalCard gc: getCommonGoalCards()) {
                    if (gc != null) {
                        objectives += gc.checkGoal(p.getPlayerBoard()) / gc.getPoints();
                    }
                }
                objectives += p.getSecretGoalCard().checkGoal(p.getPlayerBoard()) / p.getSecretGoalCard().getPoints();
                objectivesReached.put(p, objectives);
            }

            //Max number of objectives reached
            int maxObjectives = Collections.max(objectivesReached.values());

            winners = winners.stream()
                    .filter(p -> objectivesReached.get(p) == maxObjectives)
                    .toList();
        }

        return winners.toArray(new Player[0]);
    }

    /**
     * Sets the state of the game to "over".
     */
    public void gameOver(){
        isOver = true;
    }

    /**
     * Returns if the game is in the last phase.
     *
     * @return true if the game is in the last phase, false otherwise.
     */
    public boolean isInLastPhase(){
        return isInLastPhase;
    }

    /**
     * Returns if the game is over.
     *
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver(){
        return isOver;
    }

    /**
     * Sets the game state as "in the last phase".
     */
    public void setLastPhase(){
        this.isInLastPhase = true;
    }

    /**
     * Returns the gameID of the game.
     *
     * @return The gameID of the game.
     */
    public int getGameID(){
        return gameID;
    }

    public int getStartingPlayerId(){
        return startingPlayer.getClientPort();
    }
}
