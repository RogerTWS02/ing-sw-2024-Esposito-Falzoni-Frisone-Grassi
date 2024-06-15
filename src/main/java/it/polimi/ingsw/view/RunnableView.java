package it.polimi.ingsw.view;

/**
 * Interface that represents a view that can be run.
 */
public interface RunnableView extends Runnable {
    /**
     * Updates the game view.
     *
     * @param gameView The viewable game elements.
     */
    void updateView(GameView gameView);
}
