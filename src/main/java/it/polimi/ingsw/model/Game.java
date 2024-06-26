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

    /**
     * The player which starts the game.
     */
    private Player startingPlayer;

    /**
     * The list of players in the game.
     */
    private ArrayList<Player> players;

    /**
     * The gameID of the game, which identifies it uniquely.
     */
    private final int gameID;

    /**
     * The JSON array of the game's resource card deck.
     */
    public JSONArray resourceDeck;

    /**
     * The JSON array of the game's golden card deck.
     */
    public JSONArray goldenDeck;

    /**
     * The JSON array of the game's starting card deck.
     */
    public JSONArray startingDeck;

    /**
     * The JSON array of the game's resources goal card deck.
     */
    public JSONArray resourcesGoalDeck;

    /**
     * The JSON array of the game's pattern goal card deck.
     */
    public JSONArray patternGoalDeck;

    /**
     * The viewable resource cards of the game.
     */
    public ResourceCard[] viewableResourceCards;

    /**
     * The viewable golden cards of the game.
     */
    public GoldenCard[] viewableGoldenCards;

    /**
     * The common goal cards of the game.
     */
    public GoalCard[] commonGoalCards;

    /**
     * The current player of the game.
     */
    private Player currentPlayer;


    /**
     * The boolean value that indicates if the game is in the last phase.
     */
    private boolean isInLastPhase = false;

    /**
     * The constructor creates a new game with the given gameID, retrieving it from a previous saving if it exists.
     *
     * @param gameID The gameID of the game, which identifies it uniquely.
     */
    public Game(int gameID) {
        this.gameID = gameID;
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

    /**
     * Sets the new current player.
     *
     * @param currentPlayer The new current player.
     */
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
     * Returns if the game is in the last phase.
     *
     * @return True if the game is in the last phase, false otherwise.
     */
    public boolean isInLastPhase(){
        return isInLastPhase;
    }


    /**
     * Sets the game state as "in the last phase".
     */
    public void setLastPhase(){
        this.isInLastPhase = true;
    }

    /**
     * Returns the starting player's id.
     *
     * @return The starting player's id.
     */
    public int getStartingPlayerId(){
        return startingPlayer.getClientPort();
    }
}
