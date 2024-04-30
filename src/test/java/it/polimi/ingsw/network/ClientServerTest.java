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

    InetAddress addr;
    ServerSocket serverSocket;
    int port;

    @Before
    public void setup() throws IOException {
        ser = new Server(InetAddress.getByName("127.0.0.1"), 12345);

        //Ottengo una porta libera
        ServerSocket serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
        serverSocket.close();

        cli = new Client("127.0.0.1", port);
    }

    @Test
    public void startServer() throws UnknownHostException {
        addr = InetAddress.getLocalHost();
        ser.run();
    }


    @Test
    public void sendMessagetoServer() throws IOException{
        //avvio la lettura/scrittura al server
        cli.run();
        //mando il messaggio e chiudo la connessione
        cli.sendMessage(messaggio);
        cli.closeSocket();
    }
}
