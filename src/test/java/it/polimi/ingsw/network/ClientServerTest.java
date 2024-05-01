package it.polimi.ingsw.network;

import it.polimi.ingsw.model.GoalCard;
import it.polimi.ingsw.model.PatternGoalCard;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageType;
import it.polimi.ingsw.network.server.Server;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class ClientServerTest {
    GoalCard card = null;
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
        card = new PatternGoalCard(3, patternPosition,patternResources, "GC_0");

        messaggio = new Message(MessageType.TEST_MESSAGE, 666,"Messaggio dal Client");
        msg = new Message(MessageType.CARD_REQUEST, 666, card);

        //ser = new Server(InetAddress.getByName("127.0.0.1"), 12345);
        ser = new Server();
        cli = new Client(InetAddress.getLocalHost().getHostName(), 1234);
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
        serverThread.join();
        cli.closeSocket();

    }
}

