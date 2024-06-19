package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Pawn;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents the viewable elements of the game.
 */
public class GameView {
    protected String[] viewableResourceCards;
    protected String[] viewableGoldenCards;
    protected String[] commonGoalCards;
    protected ArrayList<String> playerNicknames;
    protected String currentPlayer;
    protected String secretGoalCard;
    protected Map<String, Integer> playersScores;
    protected Map<String, Resource[][]> playersResourcesOnBoard;
    protected PlayerBoard playerBoard;

    /**
     * Constructor for the GameView class.
     */
    public GameView() {
        viewableResourceCards = new String[2];
        viewableGoldenCards = new String[2];
        commonGoalCards = new String[2];
        playerNicknames = new ArrayList<>();
        playersScores = new HashMap<>();
        playersResourcesOnBoard = new HashMap<>();
        playerBoard = new PlayerBoard(Pawn.BLUE);
    }

    /**
     * Constructor for the GameView class.
     *
     * @param viewableResourceCards The viewable resource cards.
     * @param viewableGoldenCards The viewable golden cards.
     * @param commonGoalCards The common goal cards.
     * @param playerNicknames The player nicknames.
     * @param currentPlayer The current player.
     * @param secretGoalCard The secret goal card.
     * @param playersScores The player's scores.
     * @param playersResourcesOnBoard The resources on the board of each player.
     * @param playerBoard The player's board.
     */
    public GameView(String[] viewableResourceCards, String[] viewableGoldenCards, String[] commonGoalCards, ArrayList<String> playerNicknames, String currentPlayer, String secretGoalCard, Map<String, Integer> playersScores, Map<String, Resource[][]> playersResourcesOnBoard, PlayerBoard playerBoard) {
        this.viewableResourceCards = viewableResourceCards;
        this.viewableGoldenCards = viewableGoldenCards;
        this.commonGoalCards = commonGoalCards;
        this.playerNicknames = playerNicknames;
        this.currentPlayer = currentPlayer;
        this.secretGoalCard = secretGoalCard;
        this.playersScores = playersScores;
        this.playersResourcesOnBoard = playersResourcesOnBoard;
        this.playerBoard = playerBoard;
    }
}