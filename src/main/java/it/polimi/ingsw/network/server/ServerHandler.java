package it.polimi.ingsw.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class which handles the server side of the connection.
 */
public class ServerHandler implements Runnable {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private ServerSocket serverSocket;
    private final Server server;
    private final int port;

    /**
     * The constructor which creates a new ServerHandler with the given parameters.
     *
     * @param server The server which the handler is connected to.
     * @param port The port on which the server is listening.
     */
    public ServerHandler(Server server, int port){
        this.server = server;
        this.port = port;
    }

    /**
     * Starts the server process and listens for incoming connections.
     */
    public void run(){
        try{
            serverSocket = new ServerSocket(port);
            logger.log(Level.INFO, "Server created on port " + port);
        }catch (IOException e){
            logger.log(Level.SEVERE, "Server could not start!");
            return;
        }

        while(!Thread.currentThread().isInterrupted()){
            try{
                Socket client = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(server, client);
                Thread t = new Thread(clientHandler, "client_handler" +client.getInetAddress());
                t.start();
            }catch(IOException e ){
                logger.log(Level.SEVERE, "Connection dropped");
            }

        }
    }

}
