package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.message.Message;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class which handles the connection with a client.
 */
public class ClientHandler extends Thread {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final Socket clientSocket;
    private final Server server;
    private ObjectOutputStream out;
    private ObjectInputStream inp;
    private final Object inLock, outLock;
    private boolean isConnected;

    /**
     * The constructor initializes the client handler with the server and the client socket.
     *
     * @param server The server which the client is connected to.
     * @param clientSocket The socket of the client.
     */
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

    /**
     * Runs the client handler process.
     */
    public void run(){
        try {
            handleClient();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred handling the client: "+e);
            disconnect();
        }
    }

    /**
     * Sends the message sent from the client to the server.
     *
     * @throws IOException if an error occurs while sending the message.
     */
    public void handleClient() throws IOException{
        try {
            while (isConnected && !Thread.currentThread().isInterrupted()) {
                synchronized (inLock) {
                    //messaggio che il server deve ricevere
                    Message msg = (Message) inp.readObject();

                    //PER DEBUGGING
                    System.out.println(msg.getMessageType());

                    //lo mando al server
                    server.messageHandler(msg);
                }
            }
        } catch (ClassCastException | NullPointerException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error in reception, closing socket because: "+e);
            disconnect();
        }
    }

    /**
     * Sends a message to the client.
     *
     * @param msg The message to be sent.
     */
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

    /**
     * Disconnects the client from the server.
     */
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

            //NOTIFICO IL SERVER DELLA DISCONNESSIONE
            server.notifyDisconnection(clientSocket.getPort());
        }
    }
}
