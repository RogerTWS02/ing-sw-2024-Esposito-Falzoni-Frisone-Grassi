package it.polimi.ingsw.network.message;

public enum MessageType {

    LOGIN_REQUEST, LOGIN_REPLY, //string message, string message
    NEW_LOBBY_REQUEST, //new lobby message, string message || ---NEW GAME---
    LOBBIES_REQUEST, AVAILABLE_LOBBIES, //generic message, lobby message || request and get available lobby ---JOIN GAME---
    CHOOSE_LOBBY, PLAYER_JOIN, //string message, string message
    DISCONNECTION, //generic message


    INIT_SEND,
}
