package ing.soft.model;
import java.util.Random;

public class Game {
    private int startingPlayer;
    private Player[] players;

    public Player getRandomPlayer(){
        Random number = new Random();
        return players[number.nextInt(players.length)];
    }

    public void setHand(Player x, PlayableCard y, PlayableCard z, PlayableCard k){
        x.getCards[0] = y;
        x.getCards[1] = z;
        x.getCards[2] = k;
    }

    public Player[] getPlayers() {
        return players;
    }

}
