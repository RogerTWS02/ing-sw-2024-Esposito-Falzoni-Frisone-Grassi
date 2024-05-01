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
    private final Map<Lobby, int[]> lobbyPlayerMap; //lobby - playerIds
    private int playersCounter;

    // prende in ingresso indirizzo di rete e porta, oppure usa la porta di default
    // e genero il server
    public Server(InetAddress ip, int port) throws IOException {
        this.lobbyPlayerMap = new HashMap<>();
        this.playerControllerMap = new HashMap<>();
        this.idSocketMap = new HashMap<>();
        this.serverSocket = new ServerSocket(port, 66, ip);

        playersCounter = 0;
    }

    public Server() throws IOException {
        this.lobbyPlayerMap = new HashMap<>();
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


    /**
     * Checks if the id of the socket is valid
     * @param message the message received
     * @param socketHandler the socketHandler that received the message
     * @return true if the id is valid, false otherwise
     */

    public boolean checkIdSocket(Message message, ClientHandler socketHandler) {
        if (message.getMessageType() != MessageType.LOGIN_REQUEST && idSocketMap.get(message.getSenderId()) != socketHandler) {
            logger.log(Level.SEVERE, "Received message with invalid id");
            return false;
        }
        return true;
    }

    /**
     * Handles the message received from the client with a switch case
     * @param message the message received
     * @param clientHandler the clientHandler that received the message
     */

    public void messageHandler(Message message, ClientHandler clientHandler) {
        logger.log(Level.INFO, message.getMessageType() + " sent by " + message.getSenderId());
        switch(message.getMessageType()){
            case TEST_MESSAGE:
                Object[] test = message.getObj();
                System.out.println((String) test[0]);
                break;

            case LOGIN_REQUEST:
                Object[] nick = message.getObj();

                if(nick[0] instanceof String){
                    createPlayer((String) nick[0], clientHandler);
                }

                break;

            case NEW_LOBBY:
                Object[] lobby = message.getObj();
                createLobby(message.getSenderId(), (String) lobby[0], (int) lobby[1]);
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

    /**
     * Creates a new player and adds it to the idSocketMap
     * @param nickname the nickname of the player
     * @param clientHandler the clientHandler associated to the player
     */

    public void createPlayer(String nickname, ClientHandler clientHandler){
            playersCounter++;
            Player player = new Player(nickname, serverSocket.getLocalPort());
            idSocketMap.put(playersCounter, clientHandler);
    }

    /**
     * Creates a new lobby and a new controller. Adds them to the playerControllerMap
     * @param id the id of the player that created the lobby
     * @param lobbyName the name of the lobby
     * @param size the size of the lobby
     */

    public void createLobby(int id, String lobbyName, int size){
        GameController controller = new GameController();
        playerControllerMap.put(id, controller);
        Lobby lobby= new Lobby(size,1, lobbyName);
        lobbyPlayerMap.put(lobby, new int[]{id});
    }

    /**
     * Adds a player to an existing lobby
     * @param id the id of the player
     * @param lobby the lobby to add the player to
     */

    public void addPlayerToLobby(int id, Lobby lobby){
        int[] currentPlayers = lobbyPlayerMap.get(lobby);
        int[] updatedPlayers = new int[currentPlayers.length + 1];

        System.arraycopy(currentPlayers, 0, updatedPlayers, 0, currentPlayers.length);

        updatedPlayers[currentPlayers.length] = id;
        lobbyPlayerMap.put(lobby, updatedPlayers);
    }
}
