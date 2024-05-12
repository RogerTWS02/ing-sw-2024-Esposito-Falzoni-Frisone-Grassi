package it.polimi.ingsw.model;
import java.io.Serializable;

/**
 * This class represents a player in the game.
 */
public class Player implements Serializable {
    private String nickname;
    public PlayableCard[] hand = new PlayableCard[3];
    public int clientPort;
    private int score = 0;
    private PlayerBoard playerBoard;
    private GoalCard secretGoalCard;
    private Pawn pawn;
    private int gameID;

    /**
     * The constructor crates a player with a nickname and a client port, passed as parameters.
     *
     * @param nickname The nickname of the player.
     * @param clientPort The client port of the player.
     */
    public Player(String nickname, int clientPort) {
        this.nickname = nickname;
        this.clientPort = clientPort;
    }

    //Player-board getter
    public PlayerBoard getPlayerBoard() {
        return playerBoard;
    }

    //Pawn getter
    public Pawn getPawn() {
        return pawn;
    }

    //Pawn setter
    public void setPawn(Pawn pawn) {
        this.pawn = pawn;
        this.playerBoard = new PlayerBoard(pawn);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = Math.min(score, 29);
    }

    public String getNickname() {
        return nickname;
    }

    public PlayableCard[] getHand() {
        return hand;
    }

    public GoalCard getSecretGoalCard() {
        return secretGoalCard;
    }
    public  void  addScore(int score){this.score += score;}

    //Secret goal card setter
    public void setSecretGoalCard(GoalCard secretGoalCard) {
        this.secretGoalCard = secretGoalCard;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
    public int getGameID() {
        return gameID;
    }
}
