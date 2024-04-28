package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.message.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler extends Thread {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private Socket clientSocket;
    private final Server server;
    private ObjectOutputStream out;
    private ObjectInputStream inp;
    private Object inLock;
    private boolean isConnected;

    public ClientHandler(Server server, Socket clientSocket) {
        this.inLock = new Object();
        this.isConnected = true;
        this.clientSocket = clientSocket;
        this.server = server;
        try {
            // Inizializzazione degli stream di input/output
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            inp = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            handleClient();
        } catch (IOException e) {
            disconnect();
        }
    }

    public void handleClient() throws IOException{
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (inLock) {
                    Message message = (Message) inp.readObject();
                    manageReception(message);
                }
            }
        } catch (ClassCastException | ClassNotFoundException | NullPointerException e) {
            logger.log(Level.SEVERE, "error in reception, closing socket");
            e.printStackTrace();
        }
        disconnect();
    }

    private void manageReception(Message message) {
        if (message != null && server.checkIdSocket(message, this)) {
            if (message.isInitializationMessage()) {
                server.onInitializationMessage(message, this);
            } else {
                //We have to send the message to the controller
            }
        }
    }



    public void disconnect() {
        if (isConnected) {
            try {
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                    logger.log(Level.SEVERE, "The client disconnected");
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error in disconnecting");
                e.printStackTrace();
            }
            isConnected = false;
            Thread.currentThread().interrupt();
        }
    }
}
