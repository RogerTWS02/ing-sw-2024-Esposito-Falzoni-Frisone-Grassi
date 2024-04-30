package it.polimi.ingsw.network.message;

public enum MessageType {

    TEST_MESSAGE,
    CARD_REQUEST, //send a card
    LOGIN_REQUEST, LOGIN_REPLY, //string message, string message
    NEW_LOBBY, //new lobby message, string message || ---NEW GAME---
    JOINABLE_LOBBY, AVAILABLE_LOBBIES, //generic message, lobby message || request and get available lobby ---JOIN GAME---
    CHOOSE_LOBBY, PLAYER_JOIN, //string message, string message
    DISCONNECTION, //generic message

}
