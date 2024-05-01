package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageType;

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
            logger.log(Level.SEVERE, "Error in initializing streams");
            disconnect();
        }
    }

    public void run() {
        try {
            handleClient();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred handling the client");
            disconnect();
        }
    }

    //metodo che manda il messaggio spedito dal client al server
    public void handleClient() throws IOException{
        try {
            while (isConnected && !Thread.currentThread().isInterrupted()) {
                synchronized (inLock) {

                    //messaggio che il server deve ricevere
                    Message msg = (Message) inp.readObject();
                    //lo mando al server
                    server.messageHandler( msg, this);
                }
            }
        } catch (ClassCastException | NullPointerException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error in reception, closing socket");
            disconnect();
        }
    }

    public void sendMessage(Message msg){
        try{
            synchronized (outLock){
                out.writeObject(msg);
                out.flush();
                out.reset();
            }
        }catch(IOException e){
            logger.log(Level.SEVERE, "Error in sending message");
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
