package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.utils.GuiObservers;
import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.RunnableView;
import javafx.application.Application;

/**
 * The GUI view.
 */
public class Gui extends GuiObservers implements RunnableView {


    /**
     * Updates the view with the new game elements.
     *
     * @param gameView The viewable game elements.
     */
    public void updateView(GameView gameView) {
        //TODO
    }

    /**
     * Runs the GUI.
     */
    public void run() {
        Application.launch(GuiApp.class);
        //TODO
    }
}
