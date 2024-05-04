package it.polimi.ingsw.network.message;

public enum MessageType {

    REPLY_BAD_REQUEST, //Per ogni richiesta del client che non va bene
    REPLY_OK, //Per ogni richiesta semplice andata a buon fine

    //messaggi per richiedere/ricevere playable clards
    REQUEST_CARD,
    REPLY_HAND_UPDATE,

    //messaggi per entrare in lobby/avviare la partita/creare una nuova lobby
    REQUEST_LOGIN,
    REQUEST_NEW_LOBBY,
    REPLY_NEW_LOBBY,

    //messaggi per richiedere/ricevere il punteggio dei giocatori
    REQUEST_PLAYERS_POINT,
    REPLY_POINTS_UPDATE,
    TEST_MESSAGE,
    DISCONNECTION, //generic message

}
