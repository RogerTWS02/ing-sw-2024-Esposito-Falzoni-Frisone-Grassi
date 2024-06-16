package it.polimi.ingsw;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.view.TUI.TUI;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.InetAddress;

public class CodexNaturalisApp {

    public static void main(String[] args) throws IOException, ParseException {

        //parametri iniziali: <gui/tui/server> <socket/rmi>
        String param = args.length > 0 ? args[0].toLowerCase() : "cli";
        String network = args.length > 1 ? args[1].toLowerCase() : "socket";
        System.out.println(param+" "+network);

        switch(network) {
            case "rmi":
                        switch (param) {
                            case "cli" -> launchClient(false, false);
                            case "gui" -> launchClient(true, false);
                            case "server" -> launchServer(false);
                        }
                        break;
            case "socket":
                        switch (param) {
                            case "cli" -> launchClient(false, true);
                            case "gui" -> launchClient(true, true);
                            case "server" -> launchServer(true);
                        }
            default:
                launchClient(false, true);
        }
    }

    /**
     * Initializes and launches the Codex Naturalis client app.
     *
     * @param hasGUI Boolean value to determine if the client must be launched with a GUI.
     */
    private static void launchClient(boolean hasGUI, boolean hasSocket) throws IOException, ParseException {
        if (hasGUI) {
            //TODO: tutta la parte della GUI con JavaFX
        } else {
            TUI tui = new TUI();
            try {
                //per il momento funziona solo su localHost con porta di default
                tui.cli  = new Client(hasSocket, InetAddress.getLocalHost().getHostName(), 1234, tui);
                tui.cli.run();
                tui.start();
            }catch(Exception e){
                System.out.println("c'è un problema col client: "+e);
            }
        }
    }

    /**
     * Initializes and launches the Codex Naturalis server app.
     */
    private static void launchServer(Boolean hasSocket) {
        Server server;
        try{
            server = new Server();
            server.run();
        } catch (IOException e){
            System.exit(1);
        }
    }
}
