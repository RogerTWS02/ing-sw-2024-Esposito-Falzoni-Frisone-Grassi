package it.polimi.ingsw;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.server.Server;

import java.io.IOException;

public class CodexNaturalisApp {

    public static void main(String[] args) {

        String param = args.length > 0 ? args[0].toLowerCase() : "gui";

        switch (param) {
            case "cli" -> launchClient(false);
            case "gui" -> launchClient(true);
            case "server" -> launchServer();
        }
    }

    /**
     * Initializes and launches the Codex Naturalis client app.
     *
     * @param hasGUI whether to launch the GUI version of the client
     */
    private static void launchClient(boolean hasGUI) {
        if (hasGUI) {
        }
        else {
        }
        try {
            Client client = new Client("", 0);
            client.run();
        }catch(Exception ignored){}
    }

    /**
     * Initializes and launches the Codex Naturalis server app
     */
    private static void launchServer() {
        Server server;
        try{
            server = new Server();
            server.run();
        } catch (IOException e){
            System.exit(1);
        }
    }
}
