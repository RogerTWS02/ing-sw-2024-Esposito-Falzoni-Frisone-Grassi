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
    private final Object inLock, outLock;
    private boolean isConnected;

    public ClientHandler(Server server, Socket clientSocket) {
        this.inLock = new Object();
        this.outLock = new Object();
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
            logger.log(Level.SEVERE, "boh è successo qualcosa");
            disconnect();
        }
    }

    //metodo che manda il messaggio spedito dal client al server
    public void handleClient() throws IOException{
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (inLock) {
                    logger.log(Level.SEVERE, "pipipupu");
                    //messaggio che il server deve ricever
                    Message msg = (Message) inp.readObject();
                    //lo mando al server
                    server.messageHandler(msg, this);
                }
            }
        } catch (ClassCastException | ClassNotFoundException | NullPointerException e) {
            logger.log(Level.SEVERE, "error in reception, closing socket");
            e.printStackTrace();
        }
        disconnect();
    }

    public void sendMessage(Message msg){
        try{
            synchronized (outLock){
                out.writeObject(msg);
                out.flush();
                out.reset();
            }

        }catch(IOException e){
            e.printStackTrace();
            disconnect();
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
