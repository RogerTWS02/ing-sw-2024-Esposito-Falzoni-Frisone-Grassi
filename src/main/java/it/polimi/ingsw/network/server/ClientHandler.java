package it.polimi.ingsw.network.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader inp;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            // Inizializzazione degli stream di input/output
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            inp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            // Riceve e invia messaggi al client
            String inputLine;

            //leggo fino a quando non arrivo alla fine del buffer
            while ((inputLine = inp.readLine()) != null) {
                out.println("Messaggio ricevuto: " + inputLine);

                //per debugging
                System.out.println("Messaggio ricevuto dal client: " + inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Chiude le risorse una volta letto il messaggio
                inp.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
