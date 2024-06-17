package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientListenerInterface;
import it.polimi.ingsw.network.message.Message;
import org.json.simple.parser.ParseException;

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

public class Server extends UnicastRemoteObject implements RMIServerInterface{
    private static final int default_port = 1234;
    public static final String NAME = "Codex_server";
    private final InetAddress ip;
    private final int port;
    private volatile boolean running = true;
    private ServerSocket serverSocket;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private int preliminaryChoices = 0;

    //la chiave è il socket del player, il valore è il suo handler
    private final Map<Integer, ClientListenerInterface> idClientMap; //id - socket associated

    //la chiave è l'id del gioco, il valore è il gioco stesso
    private static Map<Integer, GameController> gameControllerMap; // gameId - controller
    private final Map<Lobby, int[]> lobbyPlayerMap; //lobby - playerIds
    private final Map<Integer, Player> idPlayerMap; //playerId - player

    private int numRMI = 70000;

    // prende in ingresso indirizzo di rete e porta, oppure usa la porta di default
    // e genero il server
    public Server(InetAddress ip, int port) throws IOException {
        this.lobbyPlayerMap = new HashMap<>();
        this.gameControllerMap = new HashMap<>();
        this.idClientMap = new HashMap<>();
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
        this.idClientMap = new HashMap<>();
        this.idPlayerMap = new HashMap<>();
        this.ip = InetAddress.getLocalHost();
        this.port = default_port;
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
        if (message.getMessageType() != REQUEST_LOGIN && idClientMap.get(message.getSenderID()) != socketHandler) {
            logger.log(Level.SEVERE, "Received message with invalid id");
            return false;
        }
        return true;
    }

    //TODO: JAVADOC...
    public synchronized int createSkeleton(ClientListenerInterface skelly){
        idClientMap.put(numRMI, skelly);
        return numRMI++;
    }

    /**
     * Handles the message received from the client with a switch case
     * @param message the message received
     */

    public void messageHandler(Message message) throws IOException, ParseException {
        logger.log(Level.INFO, message.getMessageType() + " sent by " + message.getSenderID());
        switch(message.getMessageType()){
            
            case TEST_MESSAGE:
                Object[] test = message.getObj();
                System.out.println((String) test[0]);

                //prendo l'handler corretto dal senderID del messaggio
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
                try {
                    gameControllerMap.get(message.getGameID()).placeCard(
                            40,
                            40,
                            (StartingCard) idPlayerMap.get(message.getSenderID()).getCardToChoose()[0],
                            idPlayerMap.get(message.getSenderID())
                    );
                }catch (IllegalAccessException ignored){};

                //notifico tutti i giocatori delle caselle disponibili
                //mando a tutti gli spazi disponibili per piazzare altre carte
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

    //notify all the player in the same lobby that a client disconnected
    //and terminates the match for all of them
    void notifyDisconnection(int clientPort) throws IOException, ParseException {
        System.out.println("Client on port " + clientPort + " disconnected");
        int gameID = -1;

        for(Lobby l: lobbyPlayerMap.keySet()){
            for(int socket: lobbyPlayerMap.get(l)){
                //ho trovato la lobby del player che ha mandato la request
                if(socket == clientPort){
                    //imposto il gameID (porta del primo giocatore che è entrato nella lobby)
                    gameID = lobbyPlayerMap.get(l)[0];
                    for(int id: lobbyPlayerMap.get(l)){
                        //questo if serve per evitare di iterare su socket per
                        //giocatori che ancora non sono entrati nella lobby
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
                        //rimuovo anche il player associato
                        idPlayerMap.remove(id);
                    }

                    //rimuovo il game controller con il game
                    gameControllerMap.remove(gameID);

                    //rimuovo la lobby
                    lobbyPlayerMap.remove(l);
                    return;
                }
            }

        }
    }

    /**
     * Handles the message which requests the viewable cards, sending the required information to the client.
     *
     * @param message the message received.
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

    //TODO: JAVADOC
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
                //uso un clientHandler per evitare azioni bloccanti dal client
                Socket clientSocket = serverSocket.accept();

                //porta del client
                System.out.println(clientSocket.getPort());

                logger.log(Level.INFO,"Client connected");
                ClientHandler clientHandler = new ClientHandler(this ,clientSocket);

                //associo alla porta del cliente il suo handler
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
     * Method to handle the login of the client
     * @param message the message received
     */
    //NOTA BENE: i giocatori di una lobby non hanno un proprio gameID fino a quando
    //la partita non ha inizio!!!
    public synchronized Message serverLogin(Message message) throws IOException, ParseException {
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

            //se non è presente lo registro nella prima lobby valida
            boolean found = false;

            for(Lobby l: lobbyPlayerMap.keySet()) {
                if(!l.isGameStarted() && !l.isLobbyFull()) {

                    //comunico il nome della lobby e il gameID
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
                        gameControllerMap.get(p.getGameID()).initializeGame();
                        gameControllerMap.get(p.getGameID()).getCurrentGame().setStartingPlayer();

                        Map<String, Integer> playersScores = new HashMap<>();

                        for(int pId : lobbyPlayerMap.get(l)){
                            playersScores.put(idPlayerMap.get(pId).getNickname(), idPlayerMap.get(pId).getScore());
                        }


                        //per ogni giocatore della lobby
                        for (int pID : lobbyPlayerMap.get(l)) {
                            //mando un messaggio per aggiornare l'interfaccia

                            idClientMap.get(pID).sendMessageToClient(
                                    new Message(
                                            REPLY_BEGIN_GAME,
                                            serverSocket.getLocalPort(),
                                            //mando a tutti il gameID come primo parametro per la prima volta
                                            lobbyPlayerMap.get(l)[0],

                                            new Object[]{

                                                    //mando a tutti l'UUID delle carte delle loro mani
                                                    Arrays.stream(gameControllerMap.get(lobbyPlayerMap.get(l)[0])
                                                                    .returnHand(idPlayerMap.get(pID)))
                                                            .map(PlayableCard::getUUID)
                                                            .collect(Collectors.toList()),

                                                    //mando a tutti l'UUID delle common goal cards
                                                    Arrays.stream(gameControllerMap.get(lobbyPlayerMap.get(l)[0])
                                                                    .getCurrentGame()
                                                                    .getCommonGoalCards())
                                                            .map(GoalCard::getUUID)
                                                            .collect(Collectors.toList()),

                                                    //mando a tutti la starting card e le secret goal cards da scegliere
                                                    Arrays.stream(gameControllerMap.get(lobbyPlayerMap.get(l)[0])
                                                            .cardToChoose(idPlayerMap.get(pID)))
                                                            .toList(),


                                                    //inizializzo per tutti il booleano che gestisce il loro turno
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

            //se non ho lobby gli chiedo di generarla
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
     * Method to handle the creation of a new lobby
     * @param message the message received
     */

    public synchronized Message requestNewLobby(Message message) throws IOException, ParseException {
        String nickName = (String) message.getObj()[0];
        String lobbyName = (String) message.getObj()[1];
        int lobbySize = (Integer) message.getObj()[2];
        System.out.println("Valori ricevuti: "+nickName+" "+lobbyName+" "+lobbySize);

        //se il nome non è valido gli mando un bad request
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
            //estraggo una carta
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


        //FOR DEBUGGING
        System.out.println("I DREW: "+replyCard.getUUID());

        //la metto nella mano del giocatore dove adesso ho un vuoto
        for(int z = 0; z < 3; z++){
            if(idPlayerMap.get(message.getSenderID()).getHand()[z] == null){
                idPlayerMap.get(message.getSenderID()).setHand(replyCard, z);
                break;
            }
        }

        //avanzo il turno al prossimo giocatore
        int currPID = gameControllerMap
                .get(message.getGameID()).advancePlayerTurn();


        //TODO: DA CAMBIARE!!!!
        //TODO: E SICCOME è FINITO IL TURNO DI QUESTO GIOCATORE MANDO A TUTTI IL SUO PUNTEGGIO AGGIORNATO
        //TODO: POI MANDO A TUTTI I GIOCATORI IL NOME DEL NUOVO GIOCATORE



        //mando al client la nuova carta pescata
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

        //imposto il lato corretto
        card.setFlipped((boolean)message.getObj()[1]);

        try {
            gameControllerMap.get(message.getGameID()).placeCard(positionx, positiony, card, idPlayerMap.get(message.getSenderID()));
        }catch(IllegalArgumentException | IllegalAccessException e){
            //se ho fatto una mossa non valida mando un messaggio di bad request

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

        //una volta piazzata la carta quello spazio rimane vuoto nella mano
        idPlayerMap.get(message.getSenderID()).setHand(null, index);





        idClientMap.get(message.getSenderID()).sendMessageToClient(
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


        //Avvio la fase finale del gioco
        if(gameControllerMap.get(message.getGameID()).checkEndGamePhase() && !gameControllerMap.get(message.getGameID()).getCurrentGame().isInLastPhase()){
            gameControllerMap.get(message.getGameID()).getCurrentGame().setLastPhase();

            //dobbiamo finire il giro e poi fare un ultimo giro
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
     * @param message the message received by the client
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


    /**
     * This class is used to handle the connection with the client using RMI
     */

    /*
    public class RMIServerImpl extends UnicastRemoteObject implements RMIServerInterface{
        private final Server server;
        private final List<ClientListenerInterface> clients;
        public RMIServerImpl(Server server) throws RemoteException {
            this.server = server;
            this.clients = new ArrayList<>();
        }
        @Override
        public void loginRequest(String nickname, ClientListenerInterface client, int clientID) throws IOException, ParseException {
            clients.add(client);
            Message result = server.serverLogin(new Message(null, clientID, -1, nickname));
            client.receiveMessage(result);
        }

        @Override
        public void newLobbyRequest(String nickname, String lobbyName, int lobbySize, ClientListenerInterface client, int clientID) throws IOException, ParseException {
            clients.add(client);
            Message result = server.requestNewLobby(new Message(null, clientID, -1, nickname, lobbyName, lobbySize));
            client.receiveMessage(result);
        }

        @Override
        public void cardRequest(boolean visible, int index, int clientID) throws IOException, ParseException {

            //PlayerId is the clientPort of the player
            if(visible){
                Message result = server.requestCard(new Message(null, clientID, getGameControllerMap()
                        .get(clientID)
                        .getCurrentGame().getGameID(), new Object[]{1,index}));
                clients.get(clientID).receiveMessage(result);
            }else{
                Message result = server.requestCard(new Message(null, clientID, getGameControllerMap()
                        .get(clientID)
                        .getCurrentGame().getGameID(), new Object[]{0,index}));
                clients.get(clientID).receiveMessage(result);
            }
        }

        @Override
        public void playerMove(int clientID, PlayableCard card, int x, int y) throws IOException, ParseException {
            Message result = server.playerMove(new Message(
                    null,
                    clientID,
                    server.getGameControllerMap().get(clientID).getCurrentGame().getGameID(),
                    new Object[]{card, x, y}
            ));

            clients.get(clientID).receiveMessage(result);
        }
        @Override
        public void requestinfoCard(int x, int y, int clientID) throws IOException, ParseException {
            Message result = server.requestInfoCard(new Message(null,clientID, -1, new Object[]{x, y}));
            clients.get(clientID).receiveMessage(result);
        }
    }
    */
}
