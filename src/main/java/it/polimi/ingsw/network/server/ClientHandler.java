package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.client.ClientListenerInterface;
import it.polimi.ingsw.network.message.Message;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class which handles the connection with a client.
 */
public class ClientHandler extends Thread implements ClientListenerInterface {

    /**
     * The logger of the connection handler.
     */
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * The socket of the client.
     */
    private final Socket clientSocket;

    /**
     * The server which the client is connected to.
     */
    private final Server server;

    /**
     * The output stream of the client.
     */
    private ObjectOutputStream out;

    /**
     * The input stream of the client.
     */
    private ObjectInputStream inp;

    /**
     * The locks used to synchronize the input and output streams.
     */
    private final Object inLock, outLock;

    /**
     * The boolean which indicates if the client is connected.
     */
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
            //Initializes the input/output streams
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
     * @throws IOException If an error occurs while sending the message.
     */
    public void handleClient() throws IOException{
        try {
            while (isConnected && !Thread.currentThread().isInterrupted()) {
                synchronized (inLock) {
                    //Message the server has to receive
                    Message msg = (Message) inp.readObject();

                    //DEBUGGING
                    System.out.println(msg.getMessageType());

                    //Send to server
                    server.messageHandler(msg);
                }
            }
        } catch (ClassCastException | NullPointerException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error in reception, closing socket because: "+e);
            disconnect();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a message to the client.
     *
     * @param msg The message to be sent.
     */
    public void sendMessageToClient(Message msg){
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

            //Notify the server of the disconnection
            try {
                server.notifyDisconnection(clientSocket.getPort());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
