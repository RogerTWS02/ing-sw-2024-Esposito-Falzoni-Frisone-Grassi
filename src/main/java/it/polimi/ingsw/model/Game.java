package it.polimi.ingsw.model;
import java.util.Random;

public class Game {
    private int startingPlayer;
    private Player[] players;

    public Player getRandomPlayer(){
        Random number = new Random();
        return players[number.nextInt(players.length)];
    }

    public void setHand(Player x, PlayableCard y, PlayableCard z, PlayableCard k){
        x.hand[0] = y;
        x.hand[1] = z;
        x.hand[2] = k;
    }

    public Player[] getPlayers() {
        return players;
    }

}
