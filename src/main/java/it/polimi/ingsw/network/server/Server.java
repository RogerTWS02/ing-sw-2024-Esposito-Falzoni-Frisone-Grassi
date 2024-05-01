package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final int default_port = 1234;
    private volatile boolean running = true;
    private final ServerSocket serverSocket;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final Map<Integer, ClientHandler> idSocketMap; //id - socket associated
    private final Map<Integer, GameController> playerControllerMap; // id - controller
    private int playersCounter;

    // prende in ingresso indirizzo di rete e porta, oppure usa la porta di default
    // e genero il server
    public Server(InetAddress ip, int port) throws IOException {
        this.playerControllerMap = new HashMap<>();
        this.idSocketMap = new HashMap<>();
        this.serverSocket = new ServerSocket(port, 66, ip);

        playersCounter = 0;
    }

    public Server() throws IOException {
        this.playerControllerMap = new HashMap<>();
        this.idSocketMap = new HashMap<>();
        this.serverSocket = new ServerSocket(default_port);

        playersCounter = 0;
    }


    // funzione che permette al server di accettare connesioni dai client
    public void run(){
        logger.log(Level.INFO, "Server started on port " + serverSocket.getLocalPort() + " and is waiting for connections\n");
        try{
            while(running && !Thread.currentThread().isInterrupted()){
                    //uso un clientHandler per evitare azioni bloccanti dal client
                    Socket clientSocket = serverSocket.accept();
                    logger.log(Level.INFO,"Client connected");
                    ClientHandler clientHandler = new ClientHandler(this ,clientSocket);
                    Thread t = new Thread(clientHandler,"server");
                    t.start();
            }
        }catch (Exception e){
            logger.log(Level.SEVERE, "Exception in server run");
        }finally {
            stop();
        }
    }

    public void stop(){
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception while closing server socket");
        }
    }


    public boolean checkIdSocket(Message message, ClientHandler socketHandler) {
        if (message.getMessageType() != MessageType.LOGIN_REQUEST && idSocketMap.get(message.getSenderId()) != socketHandler) {
            logger.log(Level.SEVERE, "Received message with invalid id");
            return false;
        }
        return true;
    }

    public void messageHandler(Message message, ClientHandler clientHandler) {
        logger.log(Level.INFO, message.getMessageType() + " sent by " + message.getSenderId());
        switch(message.getMessageType()){
            case TEST_MESSAGE:
                Object[] test = message.getObj();
                for (Object obj : test) {
                    if(obj instanceof String){
                        String stringObject = (String) obj;
                        System.out.println(stringObject);
                    }
                }
                break;

            case LOGIN_REQUEST:
                Object[] nick = message.getObj();
                if(nick[0] instanceof String){
                    createPlayer((String) nick[0], clientHandler);
                }

                break;

            case NEW_LOBBY:
                //TODO: fix this
                //createLobby(LobbyCreationMessage.getSenderId(), LobbyCreationMessage.getLobby());
                break;

            case JOINABLE_LOBBY:
                //If the player wants to join an existing lobby
                break;

            case CHOOSE_LOBBY:
                //The lobby the player has chosen
                break;
            default:
                logger.log(Level.SEVERE, "Message type not recognized");
                break;
        }
    }

    public void createPlayer(String nickname, ClientHandler clientHandler){
            playersCounter++;
            Player player = new Player(nickname, serverSocket.getLocalPort());
            idSocketMap.put(playersCounter, clientHandler);
    }

    public void createLobby(int id, Lobby lobby){
    }
}
