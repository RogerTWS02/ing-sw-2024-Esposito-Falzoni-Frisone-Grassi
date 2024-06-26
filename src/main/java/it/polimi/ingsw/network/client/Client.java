package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.server.RMIServerInterface;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.view.GUI.Gui;
import it.polimi.ingsw.view.TUI.TUI;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the client in the client-server architecture.
 */
public class Client extends UnicastRemoteObject implements ClientListenerInterface, Serializable {

    /**
     * The IP address of the server.
     */
    private final String ipServ;

    /**
     * The port of the server.
     */
    private final int port;

    /**
     * The TUI of the client.
     */
    private Object view;

    /**
     * The socket of the client.
     */
    private Socket socket;

    /**
     * The name of the lobby.
     */
    private String lobbyName = "";

    /**
     * The number of players which can join the lobby.
     */
    private int lobbySize = -1;

    /**
     * The ID of the game.
     */
    private int gameID = -1;

    /**
     * The output stream of the client.
     */
    protected ObjectOutputStream out;

    /**
     * The input stream of the client.
     */
    protected ObjectInputStream inp;

    /**
     * The logger of the client.
     */
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * The ID of the client for RMI communication.
     */
    private int clientID;

    /**
     * The stub of the server for RMI communication.
     */
    private RMIServerInterface stub;

    /**
     * The flag which indicates if the client has a socket.
     */
    final boolean hasSocket;

    /**
     * The flag which indicates if the client has a GUI.
     */
    private boolean hasGui;

    /**
     * This constructor is used to create a client.
     *
     * @param hasSocket The flag which indicates if the client has a socket.
     * @param ip The IP address of the server.
     * @param port The port of the server.
     * @param view The TUI of the client.
     * @throws RemoteException If there is an error in the remote operation.
     */
    public Client(boolean hasSocket, String ip, int port, Object view, boolean hasGui) throws RemoteException {
        this.view = view;
        this.ipServ = ip;
        this.port = port;
        this.hasSocket = hasSocket;
        this.stub = null;
        this.hasGui = hasGui;
    }

    /**
     * This method is used to read messages from the client using socket.
     *
     * @param socketInput The input stream from the socket.
     */
    public void readFromSocketAsync(ObjectInputStream socketInput){
        Thread t = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Message recievedMessage = (Message) socketInput.readObject();
                        //Forward the message to the client by extracting from the type of interface
                        if(hasGui)
                            ((Gui) view).onMessageReceived(recievedMessage);
                        else
                            ((TUI) view).onMessageReceived(recievedMessage);
                    } catch (IOException | ClassNotFoundException e) {
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                closeConnection();
            }
        });
        t.start();
    }

    /**
     * This method is used to send a message to the client.
     *
     * @param message The message to be sent.
     * @throws IOException If there is an error in the I/O operation.
     * @throws ParseException If there is an error in the parsing operation.
     */
    public void sendMessageToClient(Message message) throws IOException, ParseException {
        if(hasGui)
            ((Gui) view).onMessageReceived(message);
        else
            ((TUI) view).onMessageReceived(message);
    }

    /**
     * This method is used to send a message to the server.
     *
     * @param message The message to be sent to the server.
     */
    public synchronized void sendMessage(Message message){
        new Thread(() -> {
            if(hasSocket) {
                try {
                    //logger.log(Level.INFO, "Sending message to server");
                    out.reset();
                    out.writeObject(message);
                    out.flush();

                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error in sending message to server");
                }
            }else{
                try {
                    stub.messageHandler(message);
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
            }

        }).start();
    }

    /**
     * This method is used to get the port of the socket.
     *
     * @return The port of the socket.
     */
    public int getSocketPort() {
        return socket.getLocalPort();
    }

    /**
     * This method is used to connect to the server using either RMI or socket.
     */
    public void run(){
        if(hasSocket){
            try{
                this.socket = new Socket(ipServ, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                inp = new ObjectInputStream(socket.getInputStream());
                clientID = socket.getLocalPort();
                logger.log(Level.INFO, "Client has connected to the server");

                readFromSocketAsync(inp);
            }catch (IOException e){
                logger.log(Level.SEVERE, "Error in reading from socket");
                closeConnection();
            }
        }else{
            try{
                //Search for the server in the registry and connect to its stub
                Registry registry = LocateRegistry.getRegistry(ipServ,1099);
                stub = (RMIServerInterface) registry.lookup(Server.NAME);
                logger.log(Level.INFO, "Client has connected to the server using RMI");

                clientID = stub.createSkeleton(this);
            }catch (NotBoundException | IOException e) {
                logger.log(Level.SEVERE, "Error in connecting to server using RMI");
                System.exit(1);
            }
        }

    }

    /**
     * This method is used to disconnect the client.
     */
    @Override
    public void disconnect(){

        //Disconnect the client using RMI
        System.exit(0);
    }

    /**
     * This method is used to close the socket and the streams.
     */
    public synchronized void closeConnection(){
        if(hasSocket){
            try{
                inp.close();
                out.close();
                socket.close();
            }catch(Exception e){
                System.exit(1);
            }
        }
        System.exit(0);
    }

    /**
     * This method is used to get the ID of the client.
     *
     * @return The ID of the client.
     */
    public int getGameID() {
        return gameID;
    }

    /**
     * This method is used to set the ID of the game.
     *
     * @param gameID The ID of the game.
     */
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    /**
     * This method is used to get the name of the lobby.
     *
     * @return The name of the lobby.
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * This method is used to set the name of the lobby.
     *
     * @param lobbyName The name of the lobby.
     */
    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * This method is used to get the size of the lobby.
     *
     * @return The size of the lobby.
     */
    public int getLobbySize() {
        return lobbySize;
    }

    /**
     * This method is used to set the size of the lobby.
     *
     * @param lobbySize The size of the lobby.
     */
    public void setLobbySize(int lobbySize) {
        this.lobbySize = lobbySize;
    }

    /**
     * This method is used to get the ID of the client.
     *
     * @return The ID of the client.
     */
    public int getClientID() {
        return clientID;
    }

    /**
     * Returns the IP address of the server.
     *
     * @return The IP address of the server.
     */
    public String getIpServ() {
        return ipServ;
    }
}
