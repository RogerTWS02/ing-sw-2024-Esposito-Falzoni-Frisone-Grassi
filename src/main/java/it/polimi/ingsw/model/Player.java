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

    /**
     * Returns the player's board.
     *
     * @return The player's board.
     */
    //Player-board getter
    public PlayerBoard getPlayerBoard() {
        return playerBoard;
    }

    /**
     * Returns the player's pawn.
     *
     * @return The player's pawn.
     */
    public Pawn getPawn() {
        return pawn;
    }

    /**
     * Sets the player's pawn.
     *
     * @param pawn The player's pawn to set.
     */
    public void setPawn(Pawn pawn) {
        this.pawn = pawn;
        this.playerBoard = new PlayerBoard(pawn);
    }

    /**
     * Returns the player's score.
     *
     * @return The player's score.
     */
    public int getScore() {
        return score;
    }


    /**
     * Sets the player's score.
     *
     * @param score The player's score to set.
     */
    public void setScore(int score) {
        this.score = Math.min(score, 29);
    }


    /**
     * Returns the player's nickname.
     *
     * @return The player's nickname.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns the player's hand.
     *
     * @return The player's hand.
     */
    public PlayableCard[] getHand() {
        return hand;
    }

    /**
     * Returns the player's secret goal card.
     *
     * @return The player's secret goal card.
     */
    public GoalCard getSecretGoalCard() {
        return secretGoalCard;
    }

    /**
     * Adds points to the player's score.
     *
     * @param score The points to add to the player's score.
     */
    public void addScore(int score){this.score += score;}

    /**
     * Sets the player's secret goal card.
     *
     * @param secretGoalCard The player's secret goal card to set.
     */
    public void setSecretGoalCard(GoalCard secretGoalCard) {
        this.secretGoalCard = secretGoalCard;
    }

    /**
     * Returns the player's client port.
     *
     * @return The player's client port.
     */
    public int getClientPort() {
        return clientPort;
    }

    /**
     * Sets the game identifier in which the player is playing.
     *
     * @param gameID The game identifier to set.
     */
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    /**
     * Returns the game identifier in which the player is playing.
     *
     * @return The game identifier.
     */
    public int getGameID() {
        return gameID;
    }
}
