package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.server.RMIServerInterface;
import it.polimi.ingsw.network.server.Server;
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

public class Client extends UnicastRemoteObject implements ClientListenerInterface, Serializable {
    private final String ipServ;
    private final int port; //porta del server
    private TUI tui = null;
    private Socket socket;
    private String lobbyName = "";
    private int lobbySize = -1;
    private int gameID = -1;
    protected ObjectOutputStream out;
    protected ObjectInputStream inp;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private int clientID; //Client identifier for RMI
    private static int lastID = 0;
    private RMIServerInterface stub;
    final boolean hasSocket;

    public Client(boolean hasSocket, String ip, int port, TUI tui) throws RemoteException {
        this.tui = tui;
        this.ipServ = ip;
        this.port = port;
        this.hasSocket = hasSocket;
        this.stub = null;
    }

    /**
     * This method is used to read messages from the client using socket.
     * @param socketInput the input stream from the socket.
     */
    public void readFromSocketAsync(ObjectInputStream socketInput){
        Thread t = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Message recievedMessage = (Message) socketInput.readObject();

                        //inoltro il messaggio al client estraendo dal tipo di interfaccia
                        tui.onMessageReceived(recievedMessage);
                        //per debugging
                        //System.out.println(recievedMessage.getObj().toString());
                    } catch (IOException | ClassNotFoundException e) {
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                closeSocket();
            }
        });
        t.start();
    }

    public void sendMessageToClient(Message message) throws IOException, ParseException {
        tui.onMessageReceived(message);
    }

    /**
     * This method is used to send a message to the server.
     * @param message the message to be sent to the server.
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
                closeSocket();
            }
        }else{
            try{
                //cerco nel registry il nome del server e mi connetto
                //al suo stub (simulazione in locale del server tramite dei metodi definiti)
                Registry registry = LocateRegistry.getRegistry();
                stub = (RMIServerInterface) registry.lookup(Server.NAME);
                logger.log(Level.INFO, "Client has connected to the server using RMI");

                //TODO: DA CONTROLLARE POI...
                clientID = stub.createSkeleton(this);

                //RMIGameFlow play = new RMIGameFlow(stub, this);
                //play.run();
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

    /*
    public ClientListenerInterface getClientListener() {
        return clientListener;
    }
    public class ClientListener extends UnicastRemoteObject implements ClientListenerInterface {
        public ClientListener() throws RemoteException {
            super();
        }

        public void receiveMessage(Message message) throws IOException, ParseException {
            tui.onMessageReceived(message);
        }
    }
    */

}
