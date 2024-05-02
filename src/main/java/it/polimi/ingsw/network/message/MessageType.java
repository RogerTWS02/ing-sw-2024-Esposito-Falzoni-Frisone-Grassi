package it.polimi.ingsw.network.message;

public enum MessageType {

    //messaggi del client per il server
    REQUEST_CARD,

    TEST_MESSAGE,
    REQUEST_GOAL_CARD, //send a goal card
    REQUEST_PLAYABLE_CARD, //send a playable card
    LOGIN_REQUEST, LOGIN_REPLY, //string message, string message
    NEW_LOBBY, //new lobby message, string message || ---NEW GAME---
    JOINABLE_LOBBY, AVAILABLE_LOBBIES, //generic message, lobby message || request and get available lobby ---JOIN GAME---
    CHOOSE_LOBBY, PLAYER_JOIN, //string message, string message
    DISCONNECTION, //generic message

}
