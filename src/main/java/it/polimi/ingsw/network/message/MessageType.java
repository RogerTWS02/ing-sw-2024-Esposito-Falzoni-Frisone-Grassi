package it.polimi.ingsw.network.message;

public enum MessageType {

    REPLY_BAD_REQUEST, //Per ogni richiesta del client che non va bene
    REPLY_OK, //Per ogni richiesta semplice andata a buon fine

    //messaggi per richiedere/ricevere playable clards
    REQUEST_DRAW_FROM_DECK,
    REPLY_DRAW_FROM_DECK,
    REQUEST_DRAW_FROM_VIEWABLE,
    REPLY_DRAW_FROM_VIEWABLE,
    REQUEST_INFO_CARD,
    REPLY_INFO_CARD,
    REQUEST_CARD,
    REPLY_HAND_UPDATE,

    //messaggi per entrare in lobby/creare una nuova lobby
    REQUEST_LOGIN,
    REQUEST_NEW_LOBBY,
    REQUEST_SECRET_GC,
    REPLY_LOBBY_NAME,
    REPLY_NEW_LOBBY,

    //messaggi per iniziare il gioco
    REPLY_BEGIN_GAME,
    REPLY_SECRET_GC,
    REPLY_END_GAME,

    //messaggi per richiedere/ricevere il punteggio dei giocatori
    REQUEST_PLAYERS_POINT,
    REPLY_POINTS_UPDATE,
    TEST_MESSAGE,
    DISCONNECTION, //generic message

    //messages to make a move
    PLAYER_MOVE,
    REPLY_UPDATED_SCORE,

    WINNER,

}
