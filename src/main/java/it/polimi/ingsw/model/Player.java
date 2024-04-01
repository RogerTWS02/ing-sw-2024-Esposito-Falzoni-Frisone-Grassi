package it.polimi.ingsw.model;
import java.io.Serializable;

public class Player implements Serializable {
    protected String nickname;
    public PlayableCard[] hand = new PlayableCard[3];
    public int clientPort;
    private int score=0;
    private PlayerBoard playerBoard;
    private GoalCard secretGoalCard;


    public Player(String nickname, int clientPort) {
        this.nickname = nickname;
        this.clientPort = clientPort;
    }

    //Playerboard getter
    public PlayerBoard getPlayerBoard() {
        return playerBoard;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        if (score >29){this.score = 29;}
        else this.score = score;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public PlayableCard[] getHand() {
        return hand;
    }

    public GoalCard getSecretGoalCard() {
        return secretGoalCard;
    }

    //Secret goal card setter
    public void setSecretGoalCard(GoalCard secretGoalCard) {
        this.secretGoalCard = secretGoalCard;
    }
}
