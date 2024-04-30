package it.polimi.ingsw.network;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.server.Server;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class ClientServerTest {
    String messaggio = "Messaggio dal Client";
    Client cli;
    Server ser;

    @Before
    public void setup() throws IOException {
        //ser = new Server(InetAddress.getByName("127.0.0.1"), 12345);
        ser = new Server();
        cli = new Client(InetAddress.getLocalHost().getHostName(), 1234);
    }

    @Test
    public void startServer() throws IOException {
        ser.run();
    }


    @Test
    public void sendMessageToServer() throws IOException, InterruptedException {
        new Thread(() -> {
            ser.run();
        }).start();
        Thread.sleep(500);
        //avvio la lettura/scrittura al server
        cli.run();
        //mando il messaggio e chiudo la connessione
        cli.sendMessage(messaggio);
        cli.closeSocket();
    }
}
