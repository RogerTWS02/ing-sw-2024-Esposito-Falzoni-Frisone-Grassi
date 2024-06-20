package it.polimi.ingsw.network;

import it.polimi.ingsw.model.GoalCard;
import it.polimi.ingsw.model.PatternGoalCard;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.ResourcesGoalCard;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.server.Server;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

/**
 * Test for the Client-Server communication.
 */
public class ClientServerTest {
    GoalCard card, cardResource;
    int[] patternPosition = null;
    Resource[] patternResources = null;
    Message messaggio;

    Message msg;
    Client cli;
    Server ser;

    @Before
    public void setup() throws IOException {
        patternResources= new Resource[3];
        patternResources[0]=Resource.WOLF;
        patternResources[1]=Resource.LEAF;
        patternResources[2]=Resource.MUSHROOM;
        patternPosition = new int[6];
        for (int x=0; x<3; x++){
            patternPosition[2*x]=x;
            patternPosition[2*x+1]=x;
        }
        //ho creato la carta da mandare al server come messaggio
        card = new PatternGoalCard(3, patternPosition,patternResources, "PGC_0");
        cardResource = new ResourcesGoalCard(666, Map.of( Resource.LEAF, 2, Resource.WOLF, 3), "RGC_666");

        //messaggio = new Message(TEST_MESSAGE, cli.getSocketPort(),-1, "Messaggio dal Client");
        //msg = new Message( REQUEST_CARD, cli.getSocketPort(), -1, card);

        //ser = new Server(InetAddress.getByName("127.0.0.1"), 12345);
        ser = new Server();
        cli = new Client(true, InetAddress.getLocalHost().getHostAddress(), 1234, null);
    }

    @Test
    public void sendMessageToServer() throws IOException, InterruptedException {
        Thread serverThread = new Thread(() -> {
            ser.run();
        });
        serverThread.start();
        Thread.sleep(1000);
        //avvio la lettura/scrittura al server
        cli.run();
        //mando il messaggio e chiudo la connessione
        cli.sendMessage(messaggio);

        Thread.sleep(1000);
        cli.sendMessage(msg);

        Thread.sleep(1000);
        //cli.sendMessage(new Message( REQUEST_GOAL_CARD, cli.getSocketPort(), -1, cardResource));
        serverThread.join();
        cli.closeConnection();

    }

    /*@Test
    public void RMIserverconnection() throws IOException, InterruptedException {
        try {
            ser.run(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/


        //avvio la lettura/scrittura al server
        /*
        //mando il messaggio e chiudo la connessione
        cli.sendMessage(messaggio);

        Thread.sleep(1000);
        cli.sendMessage(msg);

        Thread.sleep(1000);
        //cli.sendMessage(new Message( REQUEST_GOAL_CARD, cli.getSocketPort(), -1, cardResource));
        serverThread.join();
        cli.closeSocket();

    }*/
}

