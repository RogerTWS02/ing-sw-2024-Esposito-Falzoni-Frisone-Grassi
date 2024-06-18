package it.polimi.ingsw.network;

import it.polimi.ingsw.model.GoalCard;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.view.TUI.TUI;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Test for the TUI with Socket connection.
 */
public class AppSocketTUITest {
    GoalCard card, cardResource;
    int[] patternPosition = null;
    Resource[] patternResources = null;
    Message messaggio;

    Message msg;
    TUI tui;
    Server ser;

    @Before
    public void setup() throws IOException {
        //msg = new Message( REQUEST_CARD, cli.getSocketPort(), -1, card);

        //ser = new Server(InetAddress.getByName("127.0.0.1"), 12345);
        ser = new Server();
        try {
            tui = new TUI();
        }catch(Exception e){
            System.out.println("c'è un problema con la TUI: "+e);
        }
    }

    @Test
    public void startTUIonSocket() throws IOException, InterruptedException {
        Thread serverThread = new Thread(() -> {
            ser.run();
        });
        serverThread.start();
        Thread.sleep(1000);
        //avvio la lettura/scrittura al server
        try {
            //per il momento funziona solo su localHost con porta di default
            tui.cli  = new Client(true, InetAddress.getLocalHost().getHostName(), 1234, tui);
            tui.cli.run();
            tui.start();
        }catch(Exception e){
            System.out.println("c'è un problema col client: "+e);
        }
    }
}
