package it.polimi.ingsw.model;
import java.util.Random;
import java.util.Stack;
import java.util.ArrayList;

public class Game {
    private Player startingPlayer;
    private ArrayList<Player> players;
    public Stack<ResourceCard> resourceDeck;
    public Stack<GoldenCard> goldenDeck;
    private Stack<StartingCard> startingDeck;
    private Stack<GoalCard> goalDeck;
    public ResourceCard[] viewableResourceCards;
    public GoldenCard[] viewableGoldenCards;
    public GoalCard[] commonGoalCards;

    public Game(){ //PER ORA COSÌ, DOMANI VI SPIEGO!
        players = new ArrayList<>();
        resourceDeck = new Stack<>();
        goldenDeck = new Stack<>();
        startingDeck = new Stack<>();
        goalDeck = new Stack<>();
        viewableResourceCards = new ResourceCard[2];
        viewableGoldenCards = new GoldenCard[2];
        commonGoalCards = new GoalCard[2];
    }

   //Pick a random player and set it as the starting player
   public Player setFirstPlayer(){
       Random random = new Random();
       int randomNumber = random.nextInt(players.size());
       this.startingPlayer = players.get(randomNumber);
       return this.startingPlayer;
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


    public void setHand(Player x, PlayableCard y, PlayableCard z, PlayableCard k){ //Ma a cosa serve?
        x.hand[0] = y;
        x.hand[1] = z;
        x.hand[2] = k;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
