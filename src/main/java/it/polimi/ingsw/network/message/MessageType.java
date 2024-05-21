package it.polimi.ingsw.network.message;

/**
 * Enumeration which contains all the possible types of messages that can be sent between client and server.
 */
public enum MessageType {

    REPLY_BAD_REQUEST, //Per ogni richiesta del client che non va bene
    REPLY_OK, //Per ogni notifica semplice andata a buon fine

    //messaggi per richiedere/ricevere playable cards
    REQUEST_INFO_CARD,
    REPLY_INFO_CARD,
    REQUEST_CARD, //richiedo una carta tra quelle visibili o dai mazzi
    REPLY_HAND_UPDATE, //ottengo la carta richiesta

    //messaggi per entrare in lobby/creare una nuova lobby
    REQUEST_LOGIN,
    REQUEST_NEW_LOBBY,
    REPLY_LOBBY_INFO,
    REPLY_NEW_LOBBY,

    //messaggi per iniziare il gioco
    REPLY_BEGIN_GAME,
    NOTIFY_CHOICES_MADE,
    REPLY_END_GAME,
    REPLY_STARTING_PLAYER,

    //messaggi per richiedere/ricevere il punteggio dei giocatori
    REQUEST_PLAYERS_POINT,
    REPLY_POINTS_UPDATE,
    TEST_MESSAGE,
    DISCONNECTION, //generic message

    //messages to make a move
    REQUEST_PLAYER_MOVE,
    REPLY_UPDATED_SCORE,

    WINNER, REPLY_CHOICES_MADE, REPLY_YOUR_TURN,
}
