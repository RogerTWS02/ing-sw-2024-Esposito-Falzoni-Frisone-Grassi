package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.client.ClientListenerInterface;
import it.polimi.ingsw.network.message.Message;
import org.json.simple.parser.ParseException;

import static it.polimi.ingsw.network.message.MessageType.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class is used to create the server and handle the connection with the clients.
 */
public class Server extends UnicastRemoteObject implements RMIServerInterface{

    /**
     * The default port of the server.
     */
    private static final int default_port = 1234;

    /**
     * The name of the server.
     */
    public static final String NAME = "Codex_server";

    /**
     * The IP address of the server.
     */
    private final InetAddress ip;

    /**
     * The port of the server.
     */
    private final int port;

    /**
     * The boolean used to check if the server is running.
     */
    private volatile boolean running = true;

    /**
     * The server socket used to handle the connection with the clients.
     */
    private ServerSocket serverSocket;

    /**
     * The logger used by the server.
     */
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * The number of how many players have made their preliminary choices.
     */
    private int preliminaryChoices = 0;

    //Key is the player's socket, value is the player's handler
    /**
     * The map used to associate the id of the client with the socket or the RMI client.
     */
    private final Map<Integer, ClientListenerInterface> idClientMap; //id - socket associated

    //Key is the gameID, value is the game
    /**
     * The map used to associate the id of the game with the game controller.
     */
    private static Map<Integer, GameController> gameControllerMap; // gameId - controller

    /**
     * The map used to associate the lobby with the players.
     */
    private final Map<Lobby, int[]> lobbyPlayerMap; //lobby - playerIds

    /**
     * The map used to associate the player id with the player.
     */
    private final Map<Integer, Player> idPlayerMap; //playerId - player

    /**
     * The number which will be used for generating the RMI port.
     */
    private int numRMI = 70000;


    /**
     * The scheduler used to send the heartbeat messages (to check if a player in the lobby disconnected).
     */
    private ScheduledExecutorService heartbeatScheduler;

    /**
     * The timeout of the heartbeat.
     */
    private final long HEARTBEAT_TIMEOUT = 5000;

    /**
     * This map is used to associate the player id with the last heartbeat received.
     */
    private final Map<Integer, Long> lastHeartbeat = new ConcurrentHashMap<>();

    //Takes the network address and port as input, or uses the default port and generates the server

    /**
     * Constructor of the server.
     *
     * @param ip The IP address of the server.
     * @param port The port of the server.
     * @throws IOException If the server cannot be created.
     */
    public Server(InetAddress ip, int port) throws IOException {
        this.lobbyPlayerMap = new HashMap<>();
        this.gameControllerMap = new HashMap<>();
        this.idClientMap = new HashMap<>();
        this.ip = ip;
        this.port = port;
        this.idPlayerMap = new HashMap<>();
        heartbeatScheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * Returns the map of the game controllers.
     *
     * @return The map of the game controllers.
     */
    public static Map<Integer, GameController> getGameControllerMap() {
        return gameControllerMap;
    }

    /**
     * Default constructor of the server, it also starts the heartbeat of the server.
     *
     * @throws IOException If the server cannot be created.
     */
    public Server() throws IOException {
        this.lobbyPlayerMap = new HashMap<>();
        this.gameControllerMap = new HashMap<>();
        this.idClientMap = new HashMap<>();
        this.idPlayerMap = new HashMap<>();
        this.ip = InetAddress.getLocalHost();
        this.port = default_port;
        heartbeatScheduler = Executors.newScheduledThreadPool(1);
        startHeartBeat();
    }

    /**
     * Stops the server.
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
     * Checks if the id of the socket is valid.
     *
     * @param message The message received.
     * @param socketHandler The socketHandler that received the message.
     * @return True if the id is valid, false otherwise.
     */
    public boolean checkIdSocket(Message message, ClientHandler socketHandler) {
        if (message.getMessageType() != REQUEST_LOGIN && idClientMap.get(message.getSenderID()) != socketHandler) {
            logger.log(Level.SEVERE, "Received message with invalid id");
            return false;
        }
        return true;
    }

    /**
     * Creates a new skeleton for RMI communication.
     *
     * @param skelly The client listener interface.
     * @return The id of the client.
     */
    public synchronized int createSkeleton(ClientListenerInterface skelly){
        idClientMap.put(numRMI, skelly);
        return numRMI++;
    }

    /**
     * Handles the message received from the client with a switch case.
     *
     * @param message The message received.
     */
    public void messageHandler(Message message) throws IOException, ParseException {
        logger.log(Level.INFO, message.getMessageType() + " sent by " + message.getSenderID());
        switch(message.getMessageType()){
            
            case TEST_MESSAGE:
                Object[] test = message.getObj();
                System.out.println((String) test[0]);

                //Get the correct handler from the senderID of the message
                idClientMap.get(message.getSenderID()).sendMessageToClient(
                        new Message(
                                TEST_MESSAGE,
                                this.serverSocket.getLocalPort(),
                                666,
                                "Risposta pazza del server"
                        )
                );
                break;

            case NEW_CHAT_MESSAGE:
                for(int id: gameControllerMap.get(message.getGameID())
                        .getCurrentGame()
                        .getPlayers()
                        .stream()
                        .map(Player::getClientPort)
                        .toArray(Integer[]::new)){
                    idClientMap.get(id).sendMessageToClient(
                            new Message(
                                    REPLY_CHAT_MESSAGE,
                                    this.serverSocket.getLocalPort(),
                                    message.getGameID(),
                                    message.getObj()
                            )
                    );
                }
                break;
            
            //Client requires to log-in (al momento non tengo conto della persistenza)
            //Puts the client in an available lobby if the nickname is valid
            case REQUEST_LOGIN:
                serverLogin(message);
                break;

            case REQUEST_VIEWABLE_CARDS:
                requestViewableCards(message);
                break;

            //Client requires to make a new lobby with nickname, lobbyName and lobbySize and to join it
            //This type of request is always generated after a REQUEST_LOGIN => REPLY_NEW_LOBBY
            //so it is already assumed that the nickname sent is valid (it would have generated REPLY_BAD_REQUEST first)
            case REQUEST_NEW_LOBBY:
                requestNewLobby(message);
                break;

            //Clients requires a PlayableCard
            case REQUEST_CARD:
                requestCard(message);
                break;

            //Client requests info of a card on the playerBoard
            case REQUEST_INFO_CARD:
                requestInfoCard(message);
                break;

            case REQUEST_PLAYER_MOVE:
                playerMove(message);
                break;

            //based on the choices made by the player, I update the game
            case NOTIFY_CHOICES_MADE:
                String startingUUID = (String) message.getObj()[0];
                String secretUUID = (String) message.getObj()[2];
                boolean side = (boolean) message.getObj()[1];

                //set the side of the card before placing it
                ((StartingCard) idPlayerMap.get(message.getSenderID()).getCardToChoose()[0]).setFlipped(side);

                //set the secret goal card for the player
                idPlayerMap.get(message.getSenderID()).setSecretGoalCard(secretUUID);

                //set the starting card for the player
                try {
                    gameControllerMap.get(message.getGameID()).placeCard(
                            40,
                            40,
                            (StartingCard) idPlayerMap.get(message.getSenderID()).getCardToChoose()[0],
                            idPlayerMap.get(message.getSenderID())
                    );
                }catch (IllegalAccessException ignored){};

                //notify all the players of the same game of the available spaces
                idClientMap.get(message.getSenderID()).sendMessageToClient(
                        new Message(
                                REPLY_CHOICES_MADE,
                                this.serverSocket.getLocalPort(),
                                message.getGameID(),
                                new Object[]{
                                        gameControllerMap.get(message.getGameID())
                                                .showAvailableOnBoard(idPlayerMap.get(message.getSenderID()))
                                }
                        )
                );
                preliminaryChoices++;
                if(preliminaryChoices == gameControllerMap.get(message.getGameID()).getCurrentGame().getPlayers().size())
                    notifyGameFlowStarting(message.getGameID());
                break;

            case REQUEST_INTERRUPT_GAME:
                notifyDisconnection(message.getSenderID());
                break;

            case NOTIFY_LAST_TURN:
                notifyTurnPass(message);
                break;

            case TEST_END_GAME:
                for(int i = 0; i < 2; i++)
                    gameControllerMap.get(message.getGameID()).getCurrentGame().getPlayers().get(i).setScore(19);
                break;

            case HEARTBEAT_ACK:
                lastHeartbeat.put(message.getSenderID(), System.currentTimeMillis());
                break;
        }
    }

    /**
     * Sends a message to clients in order to notify the start of the game, after all preliminary choices have been made by all players.
     */
    public void notifyGameFlowStarting(int gameID) throws IOException, ParseException {
        for(int id: gameControllerMap.get(gameID)
                .getCurrentGame()
                .getPlayers()
                .stream()
                .map(Player::getClientPort)
                .toArray(Integer[]::new)){
            idClientMap.get(id).sendMessageToClient(
                    new Message(
                            NOTIFY_GAME_STARTING,
                            this.serverSocket.getLocalPort(),
                            gameID,
                            new Object[] {
                                    true
                            }
                    )
            );
        }
    }

    /**
     * This method handles the disconnection of the clients.
     * @param clientPort the client who disconnected.
     */
    public void handleDisconnection(int clientPort){
        try{
            notifyDisconnection(clientPort);
        }catch (IOException | ParseException e){
            System.out.println("Error while handling disconnection");
        }
    }

    /**
     * Notifies all the players in the same lobby that a client has disconnected and terminates the match for all of them.
     *
     * @param clientPort The port of the client that has disconnected.
     * @throws IOException If an I/O error occurs.
     * @throws ParseException If a parse error occurs.
     */
    public void notifyDisconnection(int clientPort) throws IOException, ParseException {
        System.out.println("Client on port " + clientPort + " disconnected");
        int gameID = -1;

        for(Lobby l: lobbyPlayerMap.keySet()){
            for(int socket: lobbyPlayerMap.get(l)){
                //Lobby found
                if(socket == clientPort){
                    //Set the gameID (port of the first player that entered the lobby)
                    gameID = lobbyPlayerMap.get(l)[0];
                    for(int id: lobbyPlayerMap.get(l)){
                        //This id is used to avoid iterating on sockets of players that have not yet entered the lobby
                        if(idClientMap.get(id) == null) continue;
                        idClientMap.get(id).sendMessageToClient(
                                new Message(
                                        REPLY_INTERRUPT_GAME,
                                        this.serverSocket.getLocalPort(),
                                        gameID,
                                        new Object[] {
                                                "\nA player disconnected! The game is ending..."
                                        }
                                )
                        );

                        //disconnetto l'handler e lo rimuovo dal server una volta notificato della fine della partita

                        //TODO: DA SISTEMARE PER FAVORE
                        //idClientMap.get(id).disconnect();

                        idClientMap.remove(id);
                        //remove the player associated also
                        idPlayerMap.remove(id);
                    }
                    //remove the game controller also
                    gameControllerMap.remove(gameID);
                    //remove the lobby
                    lobbyPlayerMap.remove(l);
                    return;
                }
            }

        }
    }

    /**
     * Handles the message which requests the viewable cards, sending the required information to the client.
     *
     * @param message The message received.
     */
    private void requestViewableCards(Message message) throws IOException, ParseException {
        String[] rUUID = new String[3];
        String[] gUUID = new String[3];
        for(int i = 0; i < 3; i++)
            rUUID[i] = gameControllerMap.get(message.getGameID()).getCurrentGame().viewableResourceCards[i].getUUID();
        for(int i = 0; i < 3; i++)
            gUUID[i] = gameControllerMap.get(message.getGameID()).getCurrentGame().viewableGoldenCards[i].getUUID();

        idClientMap.get(message.getSenderID()).sendMessageToClient(
                new Message(
                        REPLY_VIEWABLE_CARDS,
                        this.serverSocket.getLocalPort(),
                        message.getGameID(),
                        new Object[]{
                                rUUID,
                                gUUID
                        }
                )
        );
    }

    /**
     * Method to send a message to all the players of the same notifying with the winner.
     *
     * @param gameID The ID of the game.
     */
    public void sendWinnerMessage(int gameID) throws IOException, ParseException {
        Player[] winners = gameControllerMap.get(gameID).getCurrentGame().getWinner();
        ArrayList <String> winnersNickname = new ArrayList<>();
        for(int i = 0; i < winners.length; i++){
            if(winners[i] != null)
                winnersNickname.add(winners[i].getNickname());
        }
        //Send a message to all the players of the same game with the winner
        for(int id: gameControllerMap.get(gameID)
                .getCurrentGame()
                .getPlayers()
                .stream()
                .map(Player::getClientPort)
                .toArray(Integer[]::new)){
            idClientMap.get(id).sendMessageToClient(
                    new Message(
                            REPLY_END_GAME,
                            this.serverSocket.getLocalPort(),
                            gameID,
                            //The winners might be multiple because there could be a draw,
                            //if winners[1] is null it means that there is only one winner
                            new Object[] {
                                    winnersNickname
                            }
                    )
            );
        }
    }

    /**
     * Method to start the server.
     */
    public void run(){
        try{
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(NAME, this);

            logger.log(Level.INFO, "Server started on port " + 1099 + " and is waiting for connections\n");
        }catch (RemoteException e) {
            logger.log(Level.SEVERE, "Exception while creating RMI server");
            throw new RuntimeException(e);
        }

        try{
            this.serverSocket = new ServerSocket(port, 66, ip);
        }catch (IOException e){
            logger.log(Level.SEVERE, "Exception while creating server socket");
        }

        logger.log(Level.INFO, "Server started on port " + serverSocket.getLocalPort() + " and is waiting for connections\n");
        try{
            while(running && !Thread.currentThread().isInterrupted()){
                //Use a clientHandler to avoid blocking actions from the client
                Socket clientSocket = serverSocket.accept();
                //client port
                System.out.println(clientSocket.getPort());
                logger.log(Level.INFO,"Client connected");
                ClientHandler clientHandler = new ClientHandler(this ,clientSocket);
                //match the client port with the client handler
                idClientMap.put(clientSocket.getPort(), clientHandler);
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
     * This method is used to send a heartbeat message to the clients every 5 seconds,
     * if the client doesn't respond in (at max) 5 seconds the server declares that client as disconnected.
     */

    public void startHeartBeat(){
        heartbeatScheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            for(Map.Entry<Integer, ClientListenerInterface> entry: idClientMap.entrySet()){
                int client = entry.getKey();
                ClientListenerInterface clientInterface = entry.getValue();
                try {
                    clientInterface.sendMessageToClient(new Message(HEARTBEAT, serverSocket.getLocalPort(), entry.getKey(), (Object) null));
                    lastHeartbeat.put(client, currentTime);

                    if (currentTime - lastHeartbeat.get(client) > HEARTBEAT_TIMEOUT) {
                        System.err.println("No heartbeat response from client: " + client);
                        handleDisconnection(client);
                    }
                } catch (IOException | ParseException e) {
                    System.err.println("Failed to send heartbeat to client");
                    handleDisconnection(client);
                }
            }
        }, 0, 5, java.util.concurrent.TimeUnit.SECONDS);
    }

    //Players in a lobby do not have a gameID until the game starts
    /**
     * Method to handle the login of the client.
     *
     * @param message The message received.
     */
    public synchronized Message serverLogin(Message message) throws IOException, ParseException {
        String requestNick = (String) message.getObj()[0];

        //check if the nickname is already present
        boolean duplicates = gameControllerMap.values().stream()
                .filter(Objects::nonNull)
                .map(GameController::getCurrentGame)
                .filter(Objects::nonNull)
                .flatMap(game -> game.getPlayers().stream())
                .filter(Objects::nonNull)
                .map(Player::getNickname)
                .anyMatch(nick -> Objects.equals(nick, requestNick));

        if (duplicates || requestNick.isEmpty() ) {
            //if it's already present or null I ask to change the nickname
            idClientMap.get(message.getSenderID()).sendMessageToClient(
                    new Message(
                            REPLY_BAD_REQUEST,
                            this.serverSocket.getLocalPort(),
                            message.getGameID(),
                            "Invalid nickname, please try a different one!"
                    )
            );
            return null;
        }else{
            //if the nickname is valid I register the player in the first valid lobby
            boolean found = false;

            for(Lobby l: lobbyPlayerMap.keySet()) {
                if(!l.isGameStarted() && !l.isLobbyFull()) {
                    //send the lobby name and the gameID
                    idClientMap.get(message.getSenderID()).sendMessageToClient(
                            new Message(
                                    REPLY_LOBBY_INFO,
                                    this.serverSocket.getLocalPort(),
                                    //In the gameController constructor a new game is created with the gameID,
                                    //so also the gameID is the gameID of the first player
                                    //We cannot assign a gameID to the player when the game starts otherwise
                                    //the file saving will not work
                                    message.getSenderID(),
                                    new Object[]{
                                            l.getLobbyName(),
                                            l.getSize()
                                    })
                    );
                    found = true;
                    //add the player to the lobby
                    lobbyPlayerMap.get(l)[l.getPlayersConnected()] = message.getSenderID();
                    l.incrementPlayersConnected();

                    //add the player to the game
                    Player p = new Player(requestNick, message.getSenderID());
                    gameControllerMap.get(lobbyPlayerMap.get(l)[0]).addPlayer(p);
                    idPlayerMap.put(message.getSenderID(), p);
                    p.setGameID(lobbyPlayerMap.get(l)[0]);

                    //if the lobby is full, I start the game
                    if(l.isLobbyFull()){
                        l.setGameStarted(true);

                        //initialize the hands of all the players and set the common GoalCards
                        gameControllerMap.get(p.getGameID()).initializeGame();
                        gameControllerMap.get(p.getGameID()).getCurrentGame().setStartingPlayer();

                        Map<String, Integer> playersScores = new HashMap<>();

                        for(int pId : lobbyPlayerMap.get(l)){
                            playersScores.put(idPlayerMap.get(pId).getNickname(), idPlayerMap.get(pId).getScore());
                        }

                        //for each player in the lobby
                        for (int pID : lobbyPlayerMap.get(l)) {
                            //update the interface
                            idClientMap.get(pID).sendMessageToClient(
                                    new Message(
                                            REPLY_BEGIN_GAME,
                                            serverSocket.getLocalPort(),
                                            //send the gameID as the first parameter for the first time
                                            lobbyPlayerMap.get(l)[0],

                                            new Object[]{
                                                    //send the UUID of the cards in the player's hand
                                                    Arrays.stream(gameControllerMap.get(lobbyPlayerMap.get(l)[0])
                                                                    .returnHand(idPlayerMap.get(pID)))
                                                            .map(PlayableCard::getUUID)
                                                            .collect(Collectors.toList()),

                                                    //send the UUID of the common goal cards
                                                    Arrays.stream(gameControllerMap.get(lobbyPlayerMap.get(l)[0])
                                                                    .getCurrentGame()
                                                                    .getCommonGoalCards())
                                                            .map(GoalCard::getUUID)
                                                            .collect(Collectors.toList()),

                                                    //send the UUID of the starting card and the secret goal cards to choose
                                                    Arrays.stream(gameControllerMap.get(lobbyPlayerMap.get(l)[0])
                                                            .cardToChoose(idPlayerMap.get(pID)))
                                                            .toList(),

                                                    //initialize the boolean that manages the turn of the player
                                                    gameControllerMap.get(lobbyPlayerMap.get(l)[0]).getCurrentGame()
                                                            .getStartingPlayer().getNickname().equals(idPlayerMap.get(pID).getNickname()),


                                                    //send all the nicknames
                                                    playersScores,


                                                    //return the current player
                                                    gameControllerMap.get(lobbyPlayerMap.get(l)[0])
                                                            .getCurrentGame()
                                                            .getCurrentPlayer()
                                                            .getNickname(),

                                                    //return the resources of the player
                                                    idPlayerMap.get(pID)
                                                            .getPlayerBoard()
                                                            .getResources()
                                            }
                                    )
                            );
                        }
                    }
                }
            }
            //if there's no lobby I ask to generate one
            if(!found) {
                    idClientMap.get(message.getSenderID()).sendMessageToClient(
                            new Message(
                                    REPLY_NEW_LOBBY,
                                    this.serverSocket.getLocalPort(),
                                    message.getGameID(),
                                    new Object[]{
                                            UUID.randomUUID().toString(), //nome della nuova lobby
                                            "Insert lobby size (4 players max): "
                                    }
                            )
                    );
            }
        }
        return null;
    }

    /**
     * Method to handle the creation of a new lobby.
     *
     * @param message The message received.
     */
    public synchronized Message requestNewLobby(Message message) throws IOException, ParseException {
        String nickName = (String) message.getObj()[0];
        String lobbyName = (String) message.getObj()[1];
        int lobbySize = (Integer) message.getObj()[2];
        System.out.println("Valori ricevuti: "+nickName+" "+lobbyName+" "+lobbySize);

        //If nickname is invalid I send a bad request
        if(lobbyName.isEmpty()){
            idClientMap.get(message.getSenderID()).sendMessageToClient(
                    new Message(
                            REPLY_BAD_REQUEST,
                            this.serverSocket.getLocalPort(),
                            message.getGameID(),
                            "Invalid lobby name or size!!" +
                                    "REMINDER: If you're already in a lobby you cannot join another one"
                    )
            );
        }else{
            //generate the new player for the client
            Player p = new Player(nickName, message.getSenderID());
            idPlayerMap.put(message.getSenderID(), p);

            //If the player generates the lobby it becomes the gameID
            GameController gc = new GameController(message.getSenderID());
            gc.setNumberOfPlayers(lobbySize);
            gc.addPlayer(p);
            gameControllerMap.put(message.getSenderID(), gc);
            Lobby lobby = new Lobby(lobbySize,1, lobbyName);
            int[] players = new int[lobbySize];
            players[0] = message.getSenderID();
            lobbyPlayerMap.put(lobby, players);
        }
            idClientMap.get(message.getSenderID()).sendMessageToClient(
                    new Message(
                            REPLY_LOBBY_INFO,
                            message.getSenderID(),
                            message.getGameID(),
                            new Object[]{
                                    lobbyName,
                                    lobbySize,
                                    "You just joined the lobby "+lobbyName
                            })
            );
        return null;
    }

    /**
     * Method to handle the request of a card.
     *
     * @param message The message received.
     */
    public Message requestCard(Message message) throws IOException, ParseException {
        //First I need to check if it's actually the turn of the player making the request
        if(!gameControllerMap.get(message.getGameID()).getCurrentGame().getCurrentPlayer()
                .equals(idPlayerMap.get(message.getSenderID()))){

            idClientMap.get(message.getSenderID()).sendMessageToClient(
                    new Message(
                            REPLY_BAD_REQUEST,
                            this.serverSocket.getLocalPort(),
                            message.getGameID(),
                            "Invalid request, it's not your turn!"
                    )
            );
            return null;
        }
        Object[] params = message.getObj();
        if((Integer) params[1] < 0 || (Integer) params[1] > 2){
            idClientMap.get(message.getSenderID()).sendMessageToClient(
                        new Message(
                                REPLY_BAD_REQUEST,
                                this.serverSocket.getLocalPort(),
                                message.getGameID(),
                                "Range given is out of bound!"
                        )
                );
            return null;
        }
        PlayableCard replyCard = null;
        try{
            //draw a card
            replyCard = gameControllerMap
                    .get(message.getGameID())
                    .drawViewableCard((Boolean) params[0], (Integer) params[1]);
        }catch (IllegalArgumentException e){
            idClientMap.get(message.getSenderID()).sendMessageToClient(
                    new Message(
                            REPLY_EMPTY_DECK,
                            this.serverSocket.getLocalPort(),
                            message.getGameID(),
                            new Object[]{}
                    )
            );

        }
        //DEBUGGING
        System.out.println("I DREW: "+replyCard.getUUID());
        //put the card in the player's hand where there is a null
        for(int z = 0; z < 3; z++){
            if(idPlayerMap.get(message.getSenderID()).getHand()[z] == null){
                idPlayerMap.get(message.getSenderID()).setHand(replyCard, z);
                break;
            }
        }
        //advance the turn to the next player
        int currPID = gameControllerMap
                .get(message.getGameID()).advancePlayerTurn();


        //TODO: DA CAMBIARE!!!!
        //TODO: E SICCOME è FINITO IL TURNO DI QUESTO GIOCATORE MANDO A TUTTI IL SUO PUNTEGGIO AGGIORNATO
        //TODO: POI MANDO A TUTTI I GIOCATORI IL NOME DEL NUOVO GIOCATORE

        //send the new card to the client
        idClientMap.get(message.getSenderID()).sendMessageToClient(
                new Message(
                        REPLY_HAND_UPDATE,
                        this.serverSocket.getLocalPort(),
                        message.getGameID(),
                        replyCard.getUUID()
                )
        );

        for(int id: gameControllerMap.get(message.getGameID())
                .getCurrentGame()
                .getPlayers()
                .stream()
                .map(Player::getClientPort)
                .toArray(Integer[]::new)) {

            boolean myTurn = gameControllerMap.get(message.getGameID())
                    .getCurrentGame()
                    .getCurrentPlayer()
                    .equals(idPlayerMap.get(id));

            idClientMap.get(id).sendMessageToClient(
                    new Message(
                            REPLY_YOUR_TURN,
                            this.serverSocket.getLocalPort(),
                            message.getGameID(),
                            new Object[]{
                                    gameControllerMap.get(message.getGameID())
                                            .getCurrentGame()
                                            .getCurrentPlayer()
                                            .getNickname(),
                                    myTurn
                            }
                    )
            );
        }

        return null;
    }

    /**
     * Method to handle the player move.
     *
     * @param message The message received.
     */
    public Message playerMove(Message message) throws IOException, ParseException {
        //First I need to check if it's actually the turn of the player making the request
        if(!gameControllerMap.get(message.getGameID()).getCurrentGame().getCurrentPlayer()
            .equals(idPlayerMap.get(message.getSenderID()))){

            idClientMap.get(message.getSenderID()).sendMessageToClient(
                    new Message(
                            REPLY_BAD_REQUEST,
                            this.serverSocket.getLocalPort(),
                            message.getGameID(),
                            "Invalid request: it's not your turn!"
                    )
            );
            return null;
        }
        //Where the player wants to place the card
        int positionx = (int) message.getObj()[2];
        int positiony = (int) message.getObj()[3];

        //Card to place
        int index = (int) message.getObj()[0];
        PlayableCard card = idPlayerMap.get(message.getSenderID())
                .getHand()[index];

        //set the correct side of the card
        card.setFlipped((boolean)message.getObj()[1]);

        try {
            gameControllerMap.get(message.getGameID()).placeCard(positionx, positiony, card, idPlayerMap.get(message.getSenderID()));
        }catch(IllegalArgumentException | IllegalAccessException e){
            //if the move is invalid I send a bad request message
            idClientMap.get(message.getSenderID()).sendMessageToClient(
                    new Message(
                            REPLY_BAD_REQUEST,
                            this.serverSocket.getLocalPort(),
                            message.getGameID(),
                            new Object[]{e.getMessage()}
                    )
            );
            return null;
        }
        //once the card is placed, that space remains empty in the hand
        idPlayerMap.get(message.getSenderID()).setHand(null, index);
        idClientMap.get(message.getSenderID()).sendMessageToClient(
                //TODO: A message with the new score should be sent to the player
                new Message(
                        REPLY_UPDATED_SCORE,
                        this.serverSocket.getLocalPort(),
                        message.getGameID(),
                        new Object[]{
                                //send the new available spaces
                                gameControllerMap.get(message.getGameID())
                                        .showAvailableOnBoard(idPlayerMap.get(message.getSenderID())),
                                //send the permanent resource of the card
                                card.getPermResource()[0],
                                //return the new score of the player
                                idPlayerMap.get(message.getSenderID()).getNickname(),
                                idPlayerMap.get(message.getSenderID()).getScore(),
                                //return the resources of the player
                                gameControllerMap.get(message.getGameID())
                                        .getCurrentGame()
                                        .getCurrentPlayer()
                                        .getPlayerBoard()
                                        .getResources()
                        }
                )
        );
        for(int id: gameControllerMap.get(message.getGameID())
                .getCurrentGame()
                .getPlayers()
                .stream()
                .map(Player::getClientPort)
                .toArray(Integer[]::new)) {
            if (id == message.getSenderID()) continue;
            idClientMap.get(id).sendMessageToClient(
                    new Message(
                            REPLY_POINTS_UPDATE,
                            this.serverSocket.getLocalPort(),
                            message.getGameID(),
                            new Object[]{
                                    //Player name
                                    idPlayerMap.get(message.getSenderID()).getNickname(),
                                    //Player score
                                    idPlayerMap.get(message.getSenderID()).getScore(),
                            }
                    )
            );
        }
        //start the end game phase
        if(gameControllerMap.get(message.getGameID()).checkEndGamePhase() && !gameControllerMap.get(message.getGameID()).getCurrentGame().isInLastPhase()){
            gameControllerMap.get(message.getGameID()).getCurrentGame().setLastPhase();
            //end the turn loop and start the last turn loop
            //Send a message to all the players of the same game with the winner
            ArrayList<Player> localPlayers = gameControllerMap.get(message.getGameID())
                                                              .getCurrentGame()
                                                              .getPlayers();
            int currIndex = localPlayers.indexOf(idPlayerMap.get(message.getSenderID()));
            for(int id: localPlayers.stream().map(Player::getClientPort).toArray(Integer[]::new)){
                idClientMap.get(id).sendMessageToClient(
                        new Message(
                                NOTIFY_END_GAME,
                                this.serverSocket.getLocalPort(),
                                message.getGameID(),
                                new Object[] {
                                        (localPlayers.indexOf(idPlayerMap.get(id)) > currIndex)? 2 : 1
                                }
                        )
                );
            }
        }
        return null;
    }

    /**
     * Method to handle the request of the information of a card.
     *
     * @param message The message received.
     * @return The message which contains the information of the card.
     * @throws IOException If an I/O error occurs.
     * @throws ParseException If a parse error occurs.
     */
    public Message requestInfoCard(Message message) throws IOException, ParseException {
        //Where the player wants to place the card
        int posX = (int) message.getObj()[0];
        int posY = (int) message.getObj()[1];

        PlayableCard card = idPlayerMap.get(message.getSenderID()).getPlayerBoard().getCard(posX, posY);
        if(card == null || card.getUUID().equals("PLACEHOLDER")){
            idClientMap.get(message.getSenderID()).sendMessageToClient(
                    //TODO: A message with the new score should be sent to the player
                    new Message(
                            REPLY_BAD_REQUEST,
                            this.serverSocket.getLocalPort(),
                            message.getGameID(),
                            new Object[]{
                                    "there is no card on the board at the given coordinates!"
                            }
                    )
            );
            return null;
        }
        System.out.println("INFO ON CARD: "+card.getUUID());

        Boolean[] coveredCorners = new Boolean[4];
        for(int i = 0; i < 4; i++){
            if(card.getCardCorners()[i] != null && card.getCardCorners()[i].isCovered()) {
                coveredCorners[i] = true;
            } else {
                coveredCorners[i] = false;
            }
        }
        idClientMap.get(message.getSenderID()).sendMessageToClient(
                //TODO: A message with the new score should be sent to the player
                new Message(
                        REPLY_INFO_CARD,
                        this.serverSocket.getLocalPort(),
                        message.getGameID(),
                        new Object[]{
                                card.getUUID(),
                                card.isFlipped(),
                                coveredCorners
                        })
        );
        return null;
    }

    /**
     * This method is used when a player is on his last turn and has only to update the turn of the players,
     * if the next player is the starting player, the game is over.
     *
     * @param message The message received by the client.
     */
    public void notifyTurnPass(Message message) throws IOException, ParseException {
        int playerNumber = gameControllerMap.get(message.getGameID()).advancePlayerTurn(); //advance the player turn
        if(playerNumber == gameControllerMap.get(message.getGameID()).getCurrentGame().getStartingPlayerId()){
            //if the next player is the starting player the game has to end
            sendWinnerMessage(message.getGameID());

            for(int id: gameControllerMap.get(message.getGameID())
                    .getCurrentGame()
                    .getPlayers()
                    .stream()
                    .map(Player::getClientPort)
                    .toArray(Integer[]::new)) {

                idClientMap.get(id).sendMessageToClient(
                        new Message(
                                REPLY_LAST_TURN,
                                this.serverSocket.getLocalPort(),
                                message.getGameID(),
                                true
                        )
                );
            }

        }else{
            //if the next player is not the starting player, the game continues for the remaining turns
            for(int id: gameControllerMap.get(message.getGameID())
                    .getCurrentGame()
                    .getPlayers()
                    .stream()
                    .map(Player::getClientPort)
                    .toArray(Integer[]::new)) {

                boolean myTurn = gameControllerMap.get(message.getGameID())
                        .getCurrentGame()
                        .getCurrentPlayer()
                        .equals(idPlayerMap.get(id));

                idClientMap.get(id).sendMessageToClient(
                        new Message(
                                REPLY_YOUR_TURN,
                                this.serverSocket.getLocalPort(),
                                message.getGameID(),
                                new Object[]{
                                        gameControllerMap.get(message.getGameID())
                                                .getCurrentGame()
                                                .getCurrentPlayer()
                                                .getNickname(),
                                        myTurn
                                }
                        )
                );
            }
        }

    }
}
