package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.client.Client;
import javafx.application.Application;

/**
 * The GUI view.
 */
public class Gui {
    public Client client;

    /**
     * Runs the GUI.
     */
    public void run() {
        Application.launch(GuiApp.class);
        //TODO
    }
}
