package it.polimi.ingsw;

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
        UI ui;
        if (hasGUI) {
            ui = new GUI();
        }
        else {
            ui = new CLI();
        }
        Client client = new Client(ui);
        client.run();
    }

    /**
     * Initializes and launches the Santorini server app
     */
    private static void launchServer() {
        Server server;
        try{
            server = new Server();
            server.start();
        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "Could not initialize server", e);
            System.exit(1);
        }
    }
}
