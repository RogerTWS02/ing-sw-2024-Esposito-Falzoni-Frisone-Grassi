package it.polimi.ingsw;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.view.GUI.GuiApp;
import it.polimi.ingsw.view.TUI.TUI;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Main class of the Codex Naturalis application.
 */
public class CodexNaturalisTempApp2 {
    private static Scanner scanner = new Scanner(System.in);
    private static InetAddress ipAddr;

    /**
     * Main method of the Codex Naturalis application.
     *
     * @param args Command line arguments.
     * @throws IOException If an I/O error occurs.
     * @throws ParseException If a parse error occurs.
     */
    public static void main(String[] args) throws IOException, ParseException {
        //initial parameters: <gui/tui/server> <socket/rmi>
        String param = args.length > 0 ? args[0].toLowerCase() : "cli";
        String network = args.length > 1 ? args[1].toLowerCase() : "socket";
        System.out.println(param+" "+network);

        System.out.print("Insert the server IP, or press enter to connect to localHost: ");
        try{
            String temp = scanner.nextLine();
            if(!temp.isEmpty()) ipAddr = InetAddress.getByName(temp);
        }catch(UnknownHostException e){
            System.out.print("Ip entered not valid, trying connection on localHost");
        }

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
                break;
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
            GuiApp guiApp = new GuiApp();
            guiApp.main(null);
        } else {
            TUI tui = new TUI();
            try {
                //per il momento funziona solo su localHost con porta di default
                tui.cli  = new Client(hasSocket,
                        ((ipAddr == null)? InetAddress.getLocalHost(): ipAddr).getHostName()
                        , 1234, tui);
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
            server = (ipAddr == null)? new Server(): new Server(ipAddr, 1234);
            server.run();
        } catch (IOException e){
            System.exit(1);
        }
    }
}
