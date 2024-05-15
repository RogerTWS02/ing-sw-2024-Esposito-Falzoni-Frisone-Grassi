package it.polimi.ingsw;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.view.TUI.TUI;

import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;

public class CodexNaturalisTempApp1 {

    public static void main(String[] args) throws IOException, ParseException, org.json.simple.parser.ParseException {
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
        }
    }

    /**
     * Initializes and launches the Codex Naturalis client app.
     *
     * @param hasGUI
     */
    private static void launchClient(boolean hasGUI, boolean hasSocket) throws IOException, ParseException, org.json.simple.parser.ParseException {
        if (hasGUI) {
            //TODO: tutta la parte della GUI con JavaFX
        } else {
            TUI tui = new TUI();
            try {
                //per il momento funziona solo su localHost con porta di default
                tui.cli  = new Client(InetAddress.getLocalHost().getHostName(), 1234, tui);
                tui.cli.run(hasSocket);
                tui.start();

            }catch(Exception e){
                System.out.println("c'è un problema col clienttttt: "+e);
            }
        }
    }

    /**
     * Initializes and launches the Codex Naturalis server app
     */
    private static void launchServer(Boolean hasSocket) {
        Server server;
        try{
            server = new Server();
            server.run(hasSocket);
        } catch (IOException e){
            System.exit(1);
        }
    }
}
