package it.polimi.ingsw.model;
import java.io.*;
import java.util.Random;
import java.util.Stack;
import java.util.ArrayList;

public class Game implements Serializable{
    private Player startingPlayer;
    private ArrayList<Player> players;
    public Stack<ResourceCard> resourceDeck;
    public Stack<GoldenCard> goldenDeck;
    private Stack<StartingCard> startingDeck;
    private Stack<GoalCard> goalDeck;
    public ResourceCard[] viewableResourceCards;
    public GoldenCard[] viewableGoldenCards;
    public GoalCard[] commonGoalCards;
    private Player currentPlayer;

    public Game(){
        players = new ArrayList<>();
        resourceDeck = new Stack<>();
        goldenDeck = new Stack<>();
        startingDeck = new Stack<>();
        goalDeck = new Stack<>();
        viewableResourceCards = new ResourceCard[2];
        viewableGoldenCards = new GoldenCard[2];
        commonGoalCards = new GoalCard[2];
    }

    //Generates the decks
    public void createDecks(){
        //TODO
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
    public static void checkOldGame(){
        File old = new File("savings/game.svs");
        if(old.exists()){
            restoreGame();
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
    public Player getCurrentPlayerTurn(){
         return currentPlayer;
    }

   //Draw a starting card
    public StartingCard drawStartingCard(){
         return startingDeck.pop();
    }

   //Set a secret goal card to a player
    public void setSecretGoalCard(Player player){
        player.setSecretGoalCard(goalDeck.pop());
    }

   //Set common goal cards
    public void setCommonGoalCards(){
        commonGoalCards[0] = goalDeck.pop();
        commonGoalCards[1] = goalDeck.pop();
    }

    //Common goal cards getter
    public GoalCard[] getCommonGoalCards(){
        return commonGoalCards;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
