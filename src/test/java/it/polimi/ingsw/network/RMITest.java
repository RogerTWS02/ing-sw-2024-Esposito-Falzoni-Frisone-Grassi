package it.polimi.ingsw.network;

import it.polimi.ingsw.network.client.Client;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Test for the RMI connection.
 */
public class RMITest {

    @Before
    public void setup() throws IOException {
    }


    @Test
    public void testMultipleClientsConnection() throws RemoteException, InterruptedException {
        // Numero di client da testare

        // Indirizzo IP e porta del server RMI
        String serverIP = "127.0.0.1";
        int serverPort = 1099; // Assicurati che corrisponda alla porta del tuo server RMI

        // Itera attraverso il numero di client da testare
        // Crea un nuovo client
        Thread serverThread = new Thread(() -> {
            try {
                Client client = new Client(false, serverIP, serverPort, null);
                client.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        serverThread.start();
        serverThread.join();
    }
}
