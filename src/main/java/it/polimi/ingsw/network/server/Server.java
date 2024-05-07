package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;
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

public class Server extends UnicastRemoteObject {
    private static final int default_port = 1234;
    private final InetAddress ip;
    private final int port;
    private volatile boolean running = true;
    private ServerSocket serverSocket;
    private final Logger logger = Logger.getLogger(getClass().getName());

    //la chiave è il socket del player, il valore è il suo handler
    private Map<Integer, ClientHandler> idSocketMap; //id - socket associated

    //la chiave è l'id del gioco, il valore è il gioco stesso
    private static Map<Integer, GameController> gameControllerMap; // gameId - controller
    private final Map<Lobby, int[]> lobbyPlayerMap; //lobby - playerIds
    private final Map<Integer, Player> idPlayerMap; //playerId - player

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
                //Where the player wants to place the card
                int posX = (int) message.getObj()[0];
                int posY = (int) message.getObj()[1];

                String cardID = idPlayerMap.get(message.getSenderID()).getPlayerBoard().getCard(posX, posY).getUUID();

                idSocketMap.get(message.getSenderID()).sendMessage(
                        //TODO: A message with the new score should be sent to the player
                        new Message(
                                REPLY_INFO_CARD,
                                this.serverSocket.getLocalPort(),
                                message.getGameID(),
                                cardID
                        )
                );

                break;
            case PLAYER_MOVE:
                playerMove(message);
                break;
        }
    }

    public void sendWinnerMessage(int gameID) throws IOException {
        Player winner = gameControllerMap.get(gameID).getCurrentGame().getWinner();

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
                            winner.getNickname()
                    )
            );
        }

    }

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

    public void startRMI(int port){

        Thread rmiThread = new Thread(() -> {
            try {
                RMIServerImpl rmiServer = new RMIServerImpl(this);
                RMIServerInterface stub = (RMIServerInterface) UnicastRemoteObject.exportObject(rmiServer, 1099);
                LocateRegistry.createRegistry(port);
                Registry registry = LocateRegistry.getRegistry(port);
                registry.rebind("Codex_server", stub);

                logger.log(Level.INFO, "Server started on port " + port + " and is waiting for connections\n");
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

    public void serverLogin(Message message) throws IOException {
        Object[] request = message.getObj();
        String requestNick = (String) request[0];


        //controllo se il nome è già presente
        boolean duplicates = gameControllerMap.values().stream()
                .flatMap(g -> g.getCurrentGame().getPlayers().stream())
                .map(Player::getNickname)
                .anyMatch(nick -> Objects.equals(nick, requestNick));


        if (duplicates || requestNick.isEmpty()){
            //se è presente o nullo gli dico di cambiare nick
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
                    gameControllerMap.get(message.getGameID()).getCurrentGame().addPlayer(p);
                    //idSocketMap.put(message.getSenderID(), clientHandler); non so come fare, ma dovremmo metterlo
                    idPlayerMap.put(message.getSenderID(), p);

                    //se raggiungo il numero stabilito di giocatori, avvio la partita
                    if(l.isLobbyFull()){
                        l.setGameStarted(true);
                        //inizializza le mani di tutti
                        gameControllerMap.get(message.getGameID()).beginGame();


                        //TODO: Messaggio per tutti i client per aggiornare il game id (Id di chi crea la lobby)
                        // il client per visualizzare mano, punteggio, colore pedina ecc...

                        //per ogni giocatore della lobby
                        for(int pID : lobbyPlayerMap.get(l)){
                            //mando un messaggio per aggiornare l'interfaccia
                            idSocketMap.get(message.getSenderID()).sendMessage(
                                    new Message(
                                            REPLY_BEGIN_GAME,
                                            this.serverSocket.getLocalPort(),
                                            message.getGameID(),

                                            new Object[]{
                                                    //mando a tutti il gameID come primo parametro
                                                    message.getSenderID(),

                                                    //mando a tutti l'UUID delle carte delle loro mani
                                                    Arrays.stream(gameControllerMap.get(message.getGameID())
                                                            .returnHand(idPlayerMap.get(pID)))
                                                            .map(PlayableCard::getUUID)
                                            }


                                    )
                            );

                        }

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
    }

    /**
     * Method to handle the creation of a new lobby
     * @param message the message received
     */

    public void requestNewLobby(Message message) throws FileNotFoundException {
        String nickName = (String) message.getObj()[0];
        String lobbyName = (String) message.getObj()[1];
        int lobbySize = (Integer) message.getObj()[2];

        //se il nome non è valido gli mando un bad request
        if(lobbyName.isEmpty() || lobbyPlayerMap.keySet().stream().anyMatch(lobby -> lobby.getLobbyName().equals(lobbyName)) || lobbySize > 4 || lobbySize < 2){
            idSocketMap.get(message.getSenderID()).sendMessage(
                    new Message(
                            REPLY_BAD_REQUEST,
                            this.serverSocket.getLocalPort(),
                            message.getGameID(),
                            "Invalid lobby name or size!!"
                    )
            );
        }else{
            //genero il nuovo player per il client
            Player p = new Player(nickName, message.getSenderID());
            idPlayerMap.put(message.getSenderID(), p);

            //Genero il game controller, lo aggiungo alla map e gli metto il player
            GameController gc = new GameController(message.getGameID());
            gc.setNumberOfPlayers(lobbySize);
            gc.getCurrentGame().addPlayer(p);
            gameControllerMap.put(message.getGameID(), gc);

            //inizializzo la nuova lobby e gli metto il nuovo playerID
            Lobby lobby = new Lobby(lobbySize,1, lobbyName);
            int[] players = new int[lobbySize];
            players[0] = message.getSenderID();
            lobbyPlayerMap.put(lobby, players);
        }
    }

    /**
     * Method to handle the request of a card
     * @param message the message received
     */

    public void requestCard(Message message) {
        Object[] params = message.getObj();
        PlayableCard replyCard = gameControllerMap
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
    }

    /**
     * Method to handle the player move
     * @param message the message received
     */

    public void playerMove(Message message) throws IOException {
        //Where the player wants to place the card
        int positionx = (int) message.getObj()[0];
        int positiony = (int) message.getObj()[1];
        //Card to place
        PlayableCard card = (PlayableCard) message.getObj()[2];

        gameControllerMap.get(message.getGameID()).placeCard(positionx, positiony, card, idPlayerMap.get(message.getSenderID()));


        idSocketMap.get(message.getSenderID()).sendMessage(
                //TODO: A message with the new score should be sent to the player
                new Message(
                        REPLY_UPDATED_SCORE,
                        this.serverSocket.getLocalPort(),
                        message.getGameID(),
                        "New score: " + idPlayerMap.get(message.getSenderID()).getScore()
                )
        );

        if(idPlayerMap.get(message.getSenderID()).getScore() > 20 && !gameControllerMap.get(message.getGameID()).getCurrentGame().isInLastPhase()){
            gameControllerMap.get(message.getGameID()).checkEndGamePhase();
            gameControllerMap.get(message.getGameID()).getCurrentGame().setLastPhase();
        }

        if(gameControllerMap.get(message.getGameID()).getCurrentGame().isGameOver()){
            sendWinnerMessage(message.getGameID());
        }
    }
}
