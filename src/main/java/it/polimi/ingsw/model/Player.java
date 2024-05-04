package it.polimi.ingsw.model;
import java.io.Serializable;

public class Player implements Serializable {
    private String nickname;
    public PlayableCard[] hand = new PlayableCard[3];
    public int clientPort;
    private int score = 0;
    private PlayerBoard playerBoard;
    private GoalCard secretGoalCard;
    private Pawn pawn;


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
}
