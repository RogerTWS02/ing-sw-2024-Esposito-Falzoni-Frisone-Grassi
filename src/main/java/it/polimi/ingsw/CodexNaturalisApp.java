package it.polimi.ingsw;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.view.GUI.GuiApp;
import it.polimi.ingsw.view.TUI.TUI;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;




/**
 * Main class of the Codex Naturalis application.
 */
public class CodexNaturalisApp {
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
        //initial parameters: <gui/cli/server> <socket/rmi>
        String param = "cli", network = "socket";
        for(int i = 0; i < args.length; i++)
            args[i] = args[i].toLowerCase();

        handleInput: {
            if(args.length == 0) {
                param = "cli";
                network = "socket";
                break handleInput;
            }
            if(args[0].equals("server")) {
                param = "server";
                network = "socket"; //Not a true socket obv, just for the code flow
                    ipAddr = InetAddress.getLocalHost();
                break handleInput;
            }
            if(!args[0].equals("cli") && !args[0].equals("gui")) {
                System.out.println("Syntax: <server> / <cli/gui> <socket/rmi> <server-IP>");
                System.exit(1);
            } else {
                param = args[0];
                network = "socket";
            }
            if(args.length > 1 && !args[1].equals("socket") && !args[1].equals("rmi")) {
                System.out.println("Syntax: <server> / <cli/gui> <socket/rmi> <server-IP>");
                System.exit(1);
            } else {
                if(args.length > 1)
                    network = args[1];
            }
            if(args.length > 2) {
                try {
                    ipAddr = InetAddress.getByName(args[2]);
                } catch (Exception e) {
                    System.out.println("IP not valid, 'localhost' selected\n");
                }
            }
        }

        //Debug
        System.out.print(param + " " + network + " ");
        if(ipAddr != null)
            System.out.println(ipAddr + "\n");
        else
            System.out.println(InetAddress.getLocalHost() + "\n");

        switch(network) {
            case "rmi":
                        switch (param) {
                            case "cli" -> launchClient(false, false);
                            case "gui" -> launchClient(true, false);
                        }
                        break;
            case "socket":
                        switch (param) {
                            case "cli" -> launchClient(false, true);
                            case "gui" -> launchClient(true, true);
                            case "server" -> launchServer();
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
            //TODO: connessione. E se non c'è server, devo far sì che non si apra la GUI.
            GuiApp guiApp = new GuiApp();
            guiApp.main(null);
        } else {
            TUI tui = new TUI();
            try {
                tui.cli  = new Client(hasSocket,
                        ((ipAddr == null) ? InetAddress.getLocalHost() : ipAddr).getHostName(),
                        1234, tui);
                tui.cli.run();
                tui.start();
            }catch(Exception e){
                System.out.println("Error: " + e);
            }
        }
    }

    /**
     * Initializes and launches the Codex Naturalis server app.
     */
    private static void launchServer() {
        Server server;
        try{
            server = new Server(ipAddr, 1234);
            server.run();
        } catch (IOException e){
            System.exit(1);
        }
    }
}
