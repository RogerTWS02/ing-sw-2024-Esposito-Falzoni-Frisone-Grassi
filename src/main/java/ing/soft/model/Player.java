package ing.soft.model;

public class Player {
    protected String nickname;
    public PlayableCard[] hand = new PlayableCard[3];
    public int clientPort;
    private int score;
    private GoalCard secretGoalCard;


    public Player(String nickname, int clientPort) {
        this.nickname = nickname;
        this.clientPort = clientPort;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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
}
