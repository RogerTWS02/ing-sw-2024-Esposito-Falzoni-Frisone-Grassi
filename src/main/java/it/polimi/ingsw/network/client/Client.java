package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.server.RMIServerInterface;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.view.TUI.RMIGameFlow;
import it.polimi.ingsw.view.TUI.TUI;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client  {
    private final String ipServ;

    private final int port;

    private TUI tui = null;
    private Socket socket;
    private String lobbyName = "";

    private int lobbySize = -1;
    private int gameID = -1;
    protected ObjectOutputStream out;
    protected ObjectInputStream inp;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final int clientID; //Client identifier for RMI
    private static int lastID = 0;
    private final ClientListenerInterface clientListener;

    public Client(String ip, int port, TUI tui) throws RemoteException {
        this.tui = tui;
        this.ipServ = ip;
        this.port = port;
        this.clientListener = new ClientListener();
        this.clientID = generateNewClientID();
    }

    /**
     * This method is used to generate a new client ID.
     * @return the new client ID.
     */
    private synchronized static int generateNewClientID() {
        return ++lastID;
    }

    /**
     * This method is used to read messages from the client using socket.
     * @param socketInput the input stream from the socket.
     */
    public void readFromSocketAsync(ObjectInputStream socketInput){
        Thread t = new Thread(() -> {
            try{
                while(!Thread.currentThread().isInterrupted()){
                    try {
                        Message recievedMessage = (Message) socketInput.readObject();

                        //inoltro il messaggio al client estraendo dal tipo di interfaccia
                        tui.onMessageReceived(recievedMessage);
                        //per debugging
                        //System.out.println(recievedMessage.getObj().toString());
                    }catch (IOException | ClassNotFoundException e) {
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }finally {
                closeSocket();
            }
        });
        t.start();
    }


    /**
     * This method is used to send a message to the server.
     * @param message the message to be sent to the server.
     */
    public synchronized void sendMessage(Message message){
        new Thread(() -> {
            try{
                //logger.log(Level.INFO, "Sending message to server");
                out.reset();
                out.writeObject(message);
                out.flush();

            }catch(IOException e){
                logger.log(Level.SEVERE, "Error in sending message to server");
            };

        }).start();
    }

    public int getSocketPort() {
        return socket.getLocalPort();
    }

    /**
     * This method is used to connect to the server using either RMI or socket.
     * @param useSocket true if the client wants to connect using socket, false if the client wants to connect using RMI.
     */
    public void run(boolean useSocket){
        if(useSocket){
            try{
                this.socket = new Socket(ipServ, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                inp = new ObjectInputStream(socket.getInputStream());
                logger.log(Level.INFO, "Client has connected to the server");

                readFromSocketAsync(inp);
            }catch (IOException e){
                logger.log(Level.SEVERE, "Error in reading from socket");
                closeSocket();
            }
        }else{
            try{
                Registry registry = LocateRegistry.getRegistry();
                RMIServerInterface stub = (RMIServerInterface) registry.lookup(Server.NAME);
                logger.log(Level.INFO, "Client has connected to the server using RMI");

                RMIGameFlow play = new RMIGameFlow(stub, this);
                play.run();
            }catch (NotBoundException | IOException e) {
                logger.log(Level.SEVERE, "Error in connecting to server using RMI");
            }
        }

    }


    /**
     * This method is used to close the socket and the streams.
     */
    public synchronized void closeSocket(){
        try{
            inp.close();
            out.close();
            socket.close();
        }catch(IOException ignored){}
        System.exit(0);
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public int getLobbySize() {
        return lobbySize;
    }

    public void setLobbySize(int lobbySize) {
        this.lobbySize = lobbySize;
    }

    public int getClientID() {
        return clientID;
    }

    public ClientListenerInterface getClientListener() {
        return clientListener;
    }

    public class ClientListener extends UnicastRemoteObject implements ClientListenerInterface {

        public ClientListener() throws RemoteException {
            super();
        }


        public void receiveMessage(String message) {
            System.out.println(message);
        }
    }

    public static void main(String[] args) throws UnknownHostException, RemoteException {
        Client client = new Client(InetAddress.getLocalHost().getHostAddress(), 1234, null);
        client.run(false);
    }

}
