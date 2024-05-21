package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.client.ClientListenerInterface;
import it.polimi.ingsw.network.message.Message;
import static it.polimi.ingsw.network.message.MessageType.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Server extends UnicastRemoteObject {
    private static final int default_port = 1234;
    public static final String NAME = "Codex_server";
    private final InetAddress ip;
    private final int port;
    private volatile boolean running = true;
    private ServerSocket serverSocket;
    private final Logger logger = Logger.getLogger(getClass().getName());

    //la chiave è il socket del player, il valore è il suo handler
    private final Map<Integer, ClientHandler> idSocketMap; //id - socket associated

    //la chiave è l'id del gioco, il valore è il gioco stesso
    private static Map<Integer, GameController> gameControllerMap; // gameId - controller
    private final Map<Lobby, int[]> lobbyPlayerMap; //lobby - playerIds
    private final Map<Integer, Player> idPlayerMap; //playerId - player
    private boolean hasSocket = false;

    // prende in ingresso indirizzo di rete e porta, oppure usa la porta di default
    // e genero il server
    public Server(InetAddress ip, int port) throws IOException {
        this.lobbyPlayerMap = new HashMap<>();
        this.gameControllerMap = new HashMap<>();
        this.idSocketMap = new HashMap<>();
        this.ip = ip;
        this.port = port;
        this.idPlayerMap = new HashMap<>();
    }

    //gameControllerMap getter
    public static Map<Integer, GameController> getGameControllerMap() {
        return gameControllerMap;
    }

    /**
     * Default constructor
     * @throws IOException if the server cannot be created
     */
    public Server() throws IOException {
        this.lobbyPlayerMap = new HashMap<>();
        this.gameControllerMap = new HashMap<>();
        this.idSocketMap = new HashMap<>();
        this.idPlayerMap = new HashMap<>();
        this.ip = InetAddress.getLocalHost();
        this.port = default_port;
    }


    /**
     * Starts the server and waits for connections
     */
    // funzione che permette al server di accettare connesioni dai client
    public void run(Boolean hasSocket) throws IOException {
        //Do we have to choose if we want to use socket or RMI?
        this.hasSocket = hasSocket;
        if(hasSocket){
            startSocket(this.ip, this.port);
        }else{
            startRMI(this.port);
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
     */

    public void messageHandler(Message message) throws IOException {
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
                serverLogin(message);
                break;


            //Client requires to make a new lobby with nickname, lobbyName and lobbySize and to join it
            //N.B. questo tipo di request viene generato sempre dopo un REQUEST_LOGIN => REPLY_NEW_LOBBY
            //quindi si dà gia per scontato che il nick inviato sia valido (avrebbe generato prima REPLY_BAD_REQUEST)
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

            //in base alle scelte fatte dal giocatore, aggiorno il game
            case NOTIFY_CHOICES_MADE:
                String startingUUID = (String) message.getObj()[0];
                String secretUUID = (String) message.getObj()[2];
                boolean side = (boolean) message.getObj()[1];

                //imposto il lato della carta prima di piazzarla
                ((StartingCard) idPlayerMap.get(message.getSenderID()).getCardToChoose()[0]).setFlipped(side);

                //imposto la secret goal card per il player
                idPlayerMap.get(message.getSenderID()).setSecretGoalCard(secretUUID);

                //piazzo la carta iniziale nella playerBoard
                gameControllerMap.get(message.getGameID()).placeCard(
                        40,
                        40,
                        (StartingCard) idPlayerMap.get(message.getSenderID()).getCardToChoose()[0],
                        idPlayerMap.get(message.getSenderID())
                );

                //notifico tutti i giocatori delle caselle disponibili
                //mando a tutti gli spazi disponibili per piazzare altre carte
                idSocketMap.get(message.getSenderID()).sendMessage(
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

                break;
        }
    }

    /**
     * Method to send a message to all the players of the same game with the winner
     * @param gameID the id of the game
     */

    public void sendWinnerMessage(int gameID) throws IOException {
        Player[] winners = gameControllerMap.get(gameID).getCurrentGame().getWinner();

        //Send a message to all the players of the same game with the winner
        for(int id: gameControllerMap.get(gameID)
                .getCurrentGame()
                .getPlayers()
                .stream()
                .map(Player::getClientPort)
                .toArray(Integer[]::new)){
            idSocketMap.get(id).sendMessage(
                    new Message(
                            REPLY_END_GAME,
                            this.serverSocket.getLocalPort(),
                            gameID,
                            //The winners might be multiple because there could be a draw,
                            //if winners[1] is null it means that there is only one winner
                            new Object[] {
                                    Stream.of(winners)
                                            .map(Player::getNickname)
                                            .toList()
                            }
                    )
            );
        }

    }

    /**
     * Method to start the socket
     * @param ip the ip address of the server
     * @param port the port of the server
     */

    public void startSocket(InetAddress ip, int port){

        try{

            this.serverSocket = new ServerSocket(port, 66, ip);

        }catch (IOException e){
            logger.log(Level.SEVERE, "Exception while creating server socket");
        }

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
     * Method to start the RMI
     * @param port the port of the server
     */
    public void startRMI(int port){

        Thread rmiThread = new Thread(() -> {
            try {
                Registry registry = LocateRegistry.createRegistry(1099);
                RMIServerImpl rmiServer = new RMIServerImpl(this);

                registry.rebind(NAME, rmiServer);

                logger.log(Level.INFO, "Server started on port " + 1099 + " and is waiting for connections\n");
            }catch (RemoteException e) {
                logger.log(Level.SEVERE, "Exception while creating RMI server");
                throw new RuntimeException(e);
            }
        });

        rmiThread.start();

    }

    /**
     * Method to handle the login of the client
     * @param message the message received
     */
    //NOTA BENE: i giocatori di una lobby non hanno un proprio gameID fino a quando
    //la partita non ha inizio!!!
    public synchronized Message serverLogin(Message message) throws IOException {
        String requestNick = (String) message.getObj()[0];

        //controllo se il nome è già presente
        boolean duplicates = gameControllerMap.values().stream()
                .filter(Objects::nonNull)
                .map(GameController::getCurrentGame)
                .filter(Objects::nonNull)
                .flatMap(game -> game.getPlayers().stream())
                .filter(Objects::nonNull)
                .map(Player::getNickname)
                .anyMatch(nick -> Objects.equals(nick, requestNick));



        if (duplicates || requestNick.isEmpty() ) {
            //se è presente o nullo gli dico di cambiare nick
            if(hasSocket){
                idSocketMap.get(message.getSenderID()).sendMessage(
                        new Message(
                                REPLY_BAD_REQUEST,
                                this.serverSocket.getLocalPort(),
                                message.getGameID(),
                                "Invalid nickname, please try a different one!"
                        )
                );
                return null;
            }else{
                return new Message(
                        REPLY_BAD_REQUEST,
                        message.getSenderID(),
                        message.getGameID(),
                        "Invalid nickname, please try a different one!"
                );
            }
        }else{

            //se non è presente lo registro nella prima lobby valida
            boolean found = false;

            for(Lobby l: lobbyPlayerMap.keySet()) {
                if(!l.isGameStarted() && !l.isLobbyFull()) {
                    if(hasSocket) {
                        //comunico il nome della lobby e il gameID
                        idSocketMap.get(message.getSenderID()).sendMessage(
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
                                        }
                                )
                        );
                        found = true;
                    }

                    //aggiungo il playerID alla lobby
                    lobbyPlayerMap.get(l)[l.getPlayersConnected()] = message.getSenderID();
                    l.incrementPlayersConnected();

                    //aggiungo il nuovo giocatore alla partita
                    Player p = new Player(requestNick, message.getSenderID());
                    gameControllerMap.get(lobbyPlayerMap.get(l)[0]).addPlayer(p);
                    idPlayerMap.put(message.getSenderID(), p);
                    p.setGameID(lobbyPlayerMap.get(l)[0]);


                    //se raggiungo il numero stabilito di giocatori, avvio la partita
                    if(l.isLobbyFull()){
                        l.setGameStarted(true);

                        //inizializza le mani di tutti i giocatori e imposta le GoalCards comuni
                        gameControllerMap.get(p.getGameID()).inizializeHandsAndCommons();
                        gameControllerMap.get(p.getGameID()).getCurrentGame().setStartingPlayer();

                        //per ogni giocatore della lobby
                        for (int pID : lobbyPlayerMap.get(l)) {
                            //mando un messaggio per aggiornare l'interfaccia
                            if (hasSocket) {

                                idSocketMap.get(pID).sendMessage(
                                        new Message(
                                                REPLY_BEGIN_GAME,
                                                serverSocket.getLocalPort(),
                                                //mando a tutti il gameID come primo parametro per la prima volta
                                                lobbyPlayerMap.get(l)[0],

                                                new Object[]{

                                                    //mando a tutti l'UUID delle carte delle loro mani
                                                    gameControllerMap.get(lobbyPlayerMap.get(l)[0])
                                                            .returnHand(idPlayerMap.get(pID)).stream()
                                                            .map(PlayableCard::getUUID)
                                                            .toList(),

                                                    //mando a tutti l'UUID delle common goal cards
                                                    Arrays.stream(gameControllerMap.get(lobbyPlayerMap.get(l)[0])
                                                            .getCurrentGame()
                                                            .getCommonGoalCards())
                                                            .map(GoalCard::getUUID)
                                                            .collect(Collectors.toList()),

                                                    //mando a tutti la starting card e le secret goal cards da scegliere
                                                    Arrays.stream(gameControllerMap.get(lobbyPlayerMap.get(l)[0])
                                                            .cardToChoose(idPlayerMap.get(pID)))
                                                            .toList()
                                                }
                                        )
                                );

                                idSocketMap.get(pID).sendMessage(
                                        new Message(
                                                REPLY_STARTING_PLAYER,
                                                serverSocket.getLocalPort(),
                                                lobbyPlayerMap.get(l)[0],
                                                gameControllerMap.get(lobbyPlayerMap.get(l)[0])
                                                        .getCurrentGame()
                                                        .getStartingPlayer()
                                                        .getNickname()
                                        )
                                );

                            }else{
                                return new Message(
                                        REPLY_BEGIN_GAME,
                                        pID,
                                        message.getGameID(),
                                        new Object[]{}
                                );
                            }
                        }
                    }
                }
            }

            //se non ho lobby gli chiedo di generarla
            if(!found) {
                if (hasSocket) {

                    idSocketMap.get(message.getSenderID()).sendMessage(
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

                    return null;
                }else{
                    return new Message(
                            REPLY_NEW_LOBBY,
                            message.getSenderID(),
                            message.getGameID(),
                            "No lobbies available, please create a new one!"
                    );
                }
            }
        }
        return null;
    }

    /**
     * Method to handle the creation of a new lobby
     * @param message the message received
     */

    public synchronized Message requestNewLobby(Message message) throws FileNotFoundException {
        String nickName = (String) message.getObj()[0];
        String lobbyName = (String) message.getObj()[1];
        int lobbySize = (Integer) message.getObj()[2];
        System.out.println("Valori ricevuti: "+nickName+" "+lobbyName+" "+lobbySize);

        //se il nome non è valido gli mando un bad request
        if(lobbyName.isEmpty()){
            if(hasSocket) {
                idSocketMap.get(message.getSenderID()).sendMessage(
                        new Message(
                                REPLY_BAD_REQUEST,
                                this.serverSocket.getLocalPort(),
                                message.getGameID(),
                                "Invalid lobby name or size!!" +
                                        "REMINDER: If you're already in a lobby you cannot join another one"
                        )
                );
            }else{
                return new Message(
                        REPLY_BAD_REQUEST,
                        message.getSenderID(),
                        message.getGameID(),
                        "Invalid lobby name or size!!\n " +
                                "REMINDER: If you're already in a lobby you cannot join another one"
                );
            }
        }else{

            //genero il nuovo player per il client
            Player p = new Player(nickName, message.getSenderID());
            idPlayerMap.put(message.getSenderID(), p);

            //Genero il game controller, lo aggiungo alla map e gli metto il player
            //If the player generates the lobby it becomes the gameID
            GameController gc = new GameController(message.getSenderID());
            gc.setNumberOfPlayers(lobbySize);
            gc.addPlayer(p);
            gameControllerMap.put(message.getSenderID(), gc);

            //inizializzo la nuova lobby e gli metto il nuovo playerID
            Lobby lobby = new Lobby(lobbySize,1, lobbyName);
            int[] players = new int[lobbySize];
            players[0] = message.getSenderID();
            lobbyPlayerMap.put(lobby, players);
        }

        if(hasSocket) {
            idSocketMap.get(message.getSenderID()).sendMessage(
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

        }else{
            return new Message(
                    REPLY_LOBBY_INFO,
                    message.getSenderID(),
                    message.getGameID(),
                    new Object[]{
                            lobbyName,
                            lobbySize,
                            "You just joined the lobby "+lobbyName
                    }

            );
        }
        return null;
    }

    /**
     * Method to handle the request of a card
     * @param message the message received
     */

    public Message requestCard(Message message) {
        //First I need to check if it's actually the turn of the player making the request
        if(!gameControllerMap.get(message.getGameID()).getCurrentGame().getCurrentPlayer()
                .equals(idPlayerMap.get(message.getSenderID()))){

            idSocketMap.get(message.getSenderID()).sendMessage(
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
            if(hasSocket) {
                idSocketMap.get(message.getSenderID()).sendMessage(
                        new Message(
                                REPLY_BAD_REQUEST,
                                this.serverSocket.getLocalPort(),
                                message.getGameID(),
                                "Range given is out of bound!"
                        )
                );
            }else{
                return new Message(
                        REPLY_BAD_REQUEST,
                        message.getSenderID(),
                        message.getGameID(),
                        "Range given is out of bound!"
                );
            }
            return null;
        }

        PlayableCard replyCard = gameControllerMap
                .get(message.getGameID())
                .drawViewableCard((Boolean) params[0], (Integer) params[1]);

        if(hasSocket) {

            idSocketMap.get(message.getSenderID()).sendMessage(
                    new Message(
                            REPLY_HAND_UPDATE,
                            this.serverSocket.getLocalPort(),
                            message.getGameID(),
                            replyCard.getUUID()
                    )
            );
        }else{
            return new Message(
                    REPLY_HAND_UPDATE,
                    message.getSenderID(),
                    message.getGameID(),
                    replyCard.getUUID()
            );
        }
        return null;
    }

    /**
     * Method to handle the player move
     * @param message the message received
     */

    public Message playerMove(Message message) throws IOException {

        //First I need to check if it's actually the turn of the player making the request
        if(!gameControllerMap.get(message.getGameID()).getCurrentGame().getCurrentPlayer()
            .equals(idPlayerMap.get(message.getSenderID()))){

            idSocketMap.get(message.getSenderID()).sendMessage(
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
                .getHand().get(index);

        //TODO: una volta piazzata la tolgo dalla mano(???) oppure sovrascrivo con i comandi del client?
        //TODO: in questo caso sposterei il controllo dell'indice direttamente al client (più facile)

        //imposto il lato corretto
        card.setFlipped((boolean)message.getObj()[1]);


        try {
            gameControllerMap.get(message.getGameID()).placeCard(positionx, positiony, card, idPlayerMap.get(message.getSenderID()));
        }catch(SecurityException e){
            //se ho fatto una mossa non valida mando un messaggio di bad request
            if(hasSocket) {
                idSocketMap.get(message.getSenderID()).sendMessage(
                        new Message(
                                REPLY_BAD_REQUEST,
                                this.serverSocket.getLocalPort(),
                                message.getGameID(),
                                new Object[]{e}
                        )
                );
            }else{
                return new Message(
                        REPLY_BAD_REQUEST,
                        message.getSenderID(),
                        message.getGameID(),
                        new Object[]{e}
                );
            }
            return null;
        }

        if(hasSocket) {

            idSocketMap.get(message.getSenderID()).sendMessage(
                    //TODO: A message with the new score should be sent to the player
                    new Message(
                            REPLY_UPDATED_SCORE,
                            this.serverSocket.getLocalPort(),
                            message.getGameID(),
                            new Object[]{
                                    //restituisco i nuovi posti disponibili
                                    gameControllerMap.get(message.getGameID())
                                            .showAvailableOnBoard(idPlayerMap.get(message.getSenderID())),
                                    //restituisco la risorsa permanente della carta
                                    card.getPermResource()[0],
                                    //restituisco il punteggio
                                    idPlayerMap.get(message.getSenderID()).getScore()
                            }
                    )
            );
        }

        if(idPlayerMap.get(message.getSenderID()).getScore() > 20 && !gameControllerMap.get(message.getGameID()).getCurrentGame().isInLastPhase()){
            gameControllerMap.get(message.getGameID()).checkEndGamePhase();
            gameControllerMap.get(message.getGameID()).getCurrentGame().setLastPhase();
        }

        if(gameControllerMap.get(message.getGameID()).getCurrentGame().isGameOver()){
            sendWinnerMessage(message.getGameID());
        }
        return null;
    }


    public Message requestInfoCard(Message message){
        //Where the player wants to place the card
        int posX = (int) message.getObj()[0];
        int posY = (int) message.getObj()[1];

        PlayableCard card = idPlayerMap.get(message.getSenderID()).getPlayerBoard().getCard(posY, posX);

        if(hasSocket) {
            idSocketMap.get(message.getSenderID()).sendMessage(
                    //TODO: A message with the new score should be sent to the player
                    new Message(
                            REPLY_INFO_CARD,
                            this.serverSocket.getLocalPort(),
                            message.getGameID(),
                            new Object[]{
                                    card.getUUID(),
                                    card.isFlipped()
                            }
                    )
            );
        }else{
            return new Message(
                    REPLY_INFO_CARD,
                    message.getSenderID(),
                    message.getGameID(),
                    new Object[]{
                            card.getUUID(),
                            card.isFlipped()
                    }
            );
        }
        return null;
    }


    /**
     * This class is used to handle the connection with the client using RMI
     */
    public class RMIServerImpl extends UnicastRemoteObject implements RMIServerInterface{
        private final Server server;
        private final List<ClientListenerInterface> clients;

        /**
         * Constructor
         * @param server the server it refers to
         * @throws RemoteException
         */
        public RMIServerImpl(Server server) throws RemoteException {
            this.server = server;
            this.clients = new ArrayList<>();
        }

        /**
         * Method to handle the login request
         * @param nickname the nickname of the player
         * @param client the client that has requested the login
         * @param clientID the id of the player
         * @throws IOException if the operation fails
         */
        @Override
        public void loginRequest(String nickname, ClientListenerInterface client, int clientID) throws IOException {
            clients.add(client);
            Message result = server.serverLogin(new Message(null, clientID, -1, nickname));
            switch (result.getMessageType()){
                case REPLY_NEW_LOBBY, REPLY_BAD_REQUEST:
                    client.receiveMessage(result.getObj()[0].toString());
                    break;
            }
        }

        /**
         * Method to handle the request of a new lobby
         * @param nickname the nickname of the player
         * @param lobbyName the name of the lobby
         * @param lobbySize the size of the lobby
         * @param client the client that has requested the new lobby
         * @param clientID the id of the player
         * @throws RemoteException if the operation fails
         * @throws FileNotFoundException if the file is not found
         */
        @Override
        public void newLobbyRequest(String nickname, String lobbyName, int lobbySize, ClientListenerInterface client, int clientID) throws RemoteException, FileNotFoundException {
            clients.add(client);
            Message result = server.requestNewLobby(new Message(null, clientID, -1, nickname, lobbyName, lobbySize));
            switch (result.getMessageType()){
                case REPLY_NEW_LOBBY:
                    client.receiveMessage(result.getObj()[0].toString());
                    client.receiveMessage("Waiting for other players to join the lobby...");
                    break;
                case REPLY_BAD_REQUEST:
                    client.receiveMessage(result.getObj()[0].toString());
                    break;
            }
        }

        /**
         * Method to handle the request of a card
         * @param visible true if the card is visible, false otherwise
         * @param index the index of the card
         * @param clientID the id of the player
         * @throws RemoteException if the operation fails
         */
        @Override
        public void cardRequest(boolean visible, int index, int clientID) throws RemoteException {

            //PlayerId is the clientPort of the player
            if(visible){
                Message result = server.requestCard(new Message(null, clientID, server.getGameControllerMap()
                        .get(clientID)
                        .getCurrentGame().getGameID(), new Object[]{1,index}));
                switch (result.getMessageType()){
                    case REPLY_HAND_UPDATE:
                        clients.get(clientID).receiveMessage(result.getObj()[0].toString());
                        //The hand of the player should be updated
                        break;
                    case REPLY_BAD_REQUEST:
                        clients.get(clientID).receiveMessage(result.getObj()[0].toString());
                        break;
                }
            }else{
                Message result = server.requestCard(new Message(null, clientID, server.getGameControllerMap()
                        .get(clientID)
                        .getCurrentGame().getGameID(), new Object[]{0,index}));
                switch (result.getMessageType()){
                    case REPLY_HAND_UPDATE:
                        clients.get(clientID).receiveMessage(result.getObj()[0].toString());
                        //The hand of the player should be updated
                        break;
                    case REPLY_BAD_REQUEST:
                        clients.get(clientID).receiveMessage(result.getObj()[0].toString());
                        break;
                }
            }
        }

        /**
         * Method to handle the player move
         * @param clientID the id of the client
         * @param card the card to place
         * @param x the x position
         * @param y the y position
         * @throws IOException if the operation fails
         */
        @Override
        public void playerMove(int clientID, PlayableCard card, int x, int y) throws IOException {
            Message result = server.playerMove(new Message(null, clientID, server.getGameControllerMap().get(clientID).getCurrentGame().getGameID(), new Object[]{card, x, y}));
            switch (result.getMessageType()){
                case REPLY_UPDATED_SCORE:
                    clients.get(clientID).receiveMessage(result.getObj()[0].toString());
                    //The new score and the new playerboard should be updated
                    break;
                case REPLY_BAD_REQUEST:
                    clients.get(clientID).receiveMessage(result.getObj()[0].toString());
                    break;
            }
        }

        /**
         * Method to handle the request of a card info
         * @param x the x position
         * @param y the y position
         * @param clientID the id of the client
         * @throws RemoteException if the operation fails
         */
        @Override
        public void requestinfoCard(int x, int y, int clientID) throws RemoteException {
            Message result = server.requestInfoCard(new Message(null,clientID, -1, new Object[]{x, y}));
            switch (result.getMessageType()){
                case REPLY_INFO_CARD, REPLY_BAD_REQUEST:
                    clients.get(clientID).receiveMessage(result.getObj()[0].toString());
                    break;
            }
        }
    }
}
