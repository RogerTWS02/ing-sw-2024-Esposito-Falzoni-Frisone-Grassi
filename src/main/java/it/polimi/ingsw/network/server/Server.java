package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.message.LobbyCreationMessage;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageType;
import it.polimi.ingsw.network.message.NickMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final int default_port = 12345;
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
        System.out.println("Il server è avviato e attende che i client si connettano...");
        try{
            while(true){
                    //uso un clientHandler per evitare azioni bloccanti dal client
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(this ,clientSocket);
                    System.out.println("La porta del client è: "+clientSocket);
                    Thread t = new Thread(clientHandler,"server");
                    t.start();
            }
        }catch (Exception e){
            System.out.println("Ha crashato perchè: "+e);
        }
    }

    public boolean checkIdSocket(Message message, ClientHandler socketHandler) {
        if (message.getMessageType() != MessageType.LOGIN_REQUEST && idSocketMap.get(message.getSenderId()) != socketHandler) {
            logger.log(Level.SEVERE, "Received message with invalid id");
            return false;
        }
        return true;
    }

    public void onInitializationMessage(Message message, ClientHandler clientHandler) {
        logger.log(Level.INFO, message.getMessageType() + " sent by " + message.getSenderId());
        switch(message.getMessageType()){
            case LOGIN_REQUEST -> {
                NickMessage nick = (NickMessage) message;
                createPlayer(nick.getName(), clientHandler);
            }

            case NEW_LOBBY -> {
                //If the player wants to create a new lobby
                LobbyCreationMessage lobby = (LobbyCreationMessage) message;

                //TODO: fix this
                //createLobby(LobbyCreationMessage.getSenderId(), LobbyCreationMessage.getLobby());
            }

            case JOINABLE_LOBBY -> {
                //If the player wants to join an existing lobby
            }

            case CHOOSE_LOBBY -> {
                //The lobby the player has chosen
            }
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
