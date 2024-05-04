package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.message.Message;
import static it.polimi.ingsw.network.message.MessageType.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final int default_port = 1234;
    private volatile boolean running = true;
    private final ServerSocket serverSocket;
    private final Logger logger = Logger.getLogger(getClass().getName());

    //la chiave è il socket del player, il valore è il suo handler
    private final Map<Integer, ClientHandler> idSocketMap; //id - socket associated

    //la chiave è l'id del gioco, il valore è il gioco stesso
    private final Map<Integer, GameController> playerControllerMap; // id - controller
    private final Map<Lobby, int[]> lobbyPlayerMap; //lobby - playerIds
    private final Map<Integer, Player> idPlayerMap; //playerId - player

    // prende in ingresso indirizzo di rete e porta, oppure usa la porta di default
    // e genero il server
    public Server(InetAddress ip, int port) throws IOException {
        this.lobbyPlayerMap = new HashMap<>();
        this.playerControllerMap = new HashMap<>();
        this.idSocketMap = new HashMap<>();
        this.serverSocket = new ServerSocket(port, 66, ip);
        this.idPlayerMap = new HashMap<>();
    }

    /**
     * Default constructor
     * @throws IOException if the server cannot be created
     */
    public Server() throws IOException {
        this.lobbyPlayerMap = new HashMap<>();
        this.playerControllerMap = new HashMap<>();
        this.idSocketMap = new HashMap<>();
        this.serverSocket = new ServerSocket(default_port);
        this.idPlayerMap = new HashMap<>();
    }


    /**
     * Starts the server and waits for connections
     */
    // funzione che permette al server di accettare connesioni dai client
    public void run(){
        logger.log(Level.INFO, "Server started on port " + serverSocket.getLocalPort() + " and is waiting for connections\n");
        try{
            while(running && !Thread.currentThread().isInterrupted()){
                    //uso un clientHandler per evitare azioni bloccanti dal client
                    Socket clientSocket = serverSocket.accept();

                    //porta del client
                    System.out.println(clientSocket.getPort());

                    logger.log(Level.INFO,"Client connected");
                    ClientHandler clientHandler = new ClientHandler(this ,clientSocket);

                    //associo alla porta del cliente il suo handler
                    idSocketMap.put(clientSocket.getPort(), clientHandler);
                    Thread t = new Thread(clientHandler,"server");
                    t.start();
            }
        }catch (Exception e){
            logger.log(Level.SEVERE, "Exception in server run");
        }finally {
            stop();
        }
    }

    /**
     * Stops the server
     */

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
        if (message.getMessageType() != REQUEST_LOGIN && idSocketMap.get(message.getSenderID()) != socketHandler) {
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

    public void messageHandler(Message message, ClientHandler clientHandler) throws IOException {
        logger.log(Level.INFO, message.getMessageType() + " sent by " + message.getSenderID());
        switch(message.getMessageType()){
            
            case TEST_MESSAGE:
                Object[] test = message.getObj();
                System.out.println((String) test[0]);

                //prendo l'handler corretto dal senderID del messaggio
                idSocketMap.get(message.getSenderID()).sendMessage(
                        new Message(
                                TEST_MESSAGE,
                                this.serverSocket.getLocalPort(),
                                666,
                                "Risposta pazza del server"
                        )
                );
                break;
            
            //Client requires to log-in (al momento non tengo conto della persistenza)
            //Puts the client in an available lobby if the nickname is valid
            case REQUEST_LOGIN:
                Object[] request = message.getObj();
                String requestNick = (String) request[0];


                //controllo se il nome è già presente
                boolean duplicates = playerControllerMap.values().stream()
                        .flatMap(g -> g.getCurrentGame().getPlayers().stream())
                        .map(Player::getNickname)
                        .anyMatch(nick -> Objects.equals(nick, requestNick));


                if (duplicates || requestNick.isEmpty()){
                    //se è  presente o nullo gli dico di cambiare nick
                    idSocketMap.get(message.getSenderID()).sendMessage(
                            new Message(
                                    REPLY_BAD_REQUEST,
                                    this.serverSocket.getLocalPort(),
                                    message.getGameID(),
                                    "Nickname non valido"
                            )
                    );
                }else{
                    //se non è presente lo registro nella prima lobby valida
                    boolean found = false;
                    for(Lobby l: lobbyPlayerMap.keySet()) {
                        if(!l.isGameStarted() && !l.isLobbyFull()) {
                            lobbyPlayerMap.get(l)[l.getPlayersConnected()] = message.getSenderID();
                            l.incrementPlayersConnected();

                            //aggiungo il nuovo giocatore alla partita
                            Player p = new Player(requestNick, message.getSenderID());
                            playerControllerMap.get(message.getGameID()).getCurrentGame().addPlayer(p);
                            idPlayerMap.put(message.getSenderID(), p);

                            //se raggiungo il numero stabilito di giocatori, avvio la partita
                            if(l.isLobbyFull()){
                                l.setGameStarted(true);
                                playerControllerMap.get(message.getGameID()).beginGame();

                                //TODO: Messaggio per tutti i client per aggiornare il game id (Id di chi crea la lobby)
                                // il client per visualizzare mano, punteggio, colore pedina ecc...

                            }
                            found = true;
                            break;
                        }
                    }
                    //se non ho lobby gli chiedo di generarla
                    if(!found){
                        idSocketMap.get(message.getSenderID()).sendMessage(
                                new Message(
                                        REPLY_NEW_LOBBY,
                                        this.serverSocket.getLocalPort(),
                                        message.getGameID(),
                                        "Inserisci dimensione lobby (4 giocatori max)"
                                )
                        );
                    }
                }
                break;


            //Client requires to make a new lobby with nickname, lobbyName and lobbySize and to join it
            //N.B. questo tipo di request viene generato sempre dopo un REQUEST_LOGIN => REPLY_NEW_LOBBY
            //quindi si dà gia per scontato che il nick inviato sia valido (avrebbe generato prima REPLY_BAD_REQUEST)
            case REQUEST_NEW_LOBBY:
                String nickName = (String) message.getObj()[0];
                String lobbyName = (String) message.getObj()[1];
                int lobbySize = (Integer) message.getObj()[2];

                //se il nome non è valido gli mando un bad request
                if(lobbyName.isEmpty() || lobbyPlayerMap.keySet().stream().anyMatch(lobby -> lobby.getLobbyName().equals(lobbyName))){
                    idSocketMap.get(message.getSenderID()).sendMessage(
                            new Message(
                                    REPLY_BAD_REQUEST,
                                    this.serverSocket.getLocalPort(),
                                    message.getGameID(),
                                    "Nome Lobby non valido"
                            )
                    );
                }else{
                    //genero il nuovo player per il client
                    Player p = new Player(nickName, message.getSenderID());
                    idPlayerMap.put(message.getSenderID(), p);

                    //Genero il game controller, lo aggiungo alla map e gli metto il player
                    GameController gc = new GameController();
                    gc.getCurrentGame().addPlayer(p);
                    playerControllerMap.put(message.getSenderID(), gc);

                    //inizializzo la nuova lobby e gli metto il nuovo playerID
                    Lobby lobby = new Lobby(lobbySize,1, lobbyName);
                    int[] players = new int[lobbySize];
                    players[0] = message.getSenderID();
                    lobbyPlayerMap.put(lobby, players);
                }
                break;

            //Clients requires a PlayableCard
            case REQUEST_CARD:
                Object[] params = message.getObj();
                PlayableCard replyCard = playerControllerMap
                        .get(message.getGameID())
                        .drawViewableCard((Boolean) params[0], (Integer) params[1]);

                idSocketMap.get(message.getSenderID()).sendMessage(
                        ((Integer) params[1] < 0 || (Integer) params[1] > 2)?
                        new Message(
                                REPLY_HAND_UPDATE,
                                this.serverSocket.getLocalPort(),
                                message.getGameID(),
                                replyCard.getUUID()
                        )
                        :
                        new Message(
                                REPLY_BAD_REQUEST,
                                this.serverSocket.getLocalPort(),
                                message.getGameID(),
                                "Range given is out of bound!"
                        )
                );
                break;

            case PLAYER_MOVE:
                //Where the player wants to place the card
                int positionx = (int) message.getObj()[0];
                int positiony = (int) message.getObj()[1];
                //Card to place
                PlayableCard card = (PlayableCard) message.getObj()[2];

                playerControllerMap.get(message.getGameID()).placeCard(positionx, positiony, card, idPlayerMap.get(message.getSenderID()));


                idSocketMap.get(message.getSenderID()).sendMessage(
                        //TODO: A message with the new score should be sent to the player
                        new Message(
                                REPLY_UPDATED_SCORE,
                                this.serverSocket.getLocalPort(),
                                message.getGameID(),
                                "New score: " + idPlayerMap.get(message.getSenderID()).getScore()
                        )
                );

                playerControllerMap.get(message.getGameID()).checkEndGamePhase();
                break;
        }
    }
}
