package it.polimi.ingsw.network.message;

/**
 * Enumeration which contains all the possible types of messages that can be sent between client and server.
 */
public enum MessageType {

    //For every request that the client sends to the server
    REPLY_BAD_REQUEST,
    REPLY_OK,

    //Messages for requesting/receiving playable cards
    REQUEST_INFO_CARD,
    REPLY_INFO_CARD,
    //Ask for a card between the visible ones or from the decks
    REQUEST_CARD,
    //Get the card requested
    REPLY_HAND_UPDATE,
    REPLY_EMPTY_DECK,

    //Messages to enter a lobby/create a new lobby
    REQUEST_LOGIN,
    REQUEST_NEW_LOBBY,
    REPLY_LOBBY_INFO,
    REPLY_NEW_LOBBY,

    //Messages to start the game
    REPLY_BEGIN_GAME,
    NOTIFY_CHOICES_MADE,
    REPLY_END_GAME,
    REPLY_STARTING_PLAYER,

    //Messages to request/receive the score of the players
    REQUEST_PLAYERS_POINT,
    REPLY_POINTS_UPDATE,
    TEST_MESSAGE,

    DISCONNECTION,

    //messages to make a move
    REQUEST_PLAYER_MOVE,
    REPLY_UPDATED_SCORE,
    NOTIFY_LAST_TURN,
    REPLY_LAST_TURN,

    WINNER, NOTIFY_GAME_STARTING, REPLY_CHOICES_MADE, REPLY_YOUR_TURN, NOTIFY_END_GAME, REQUEST_INTERRUPT_GAME, REPLY_INTERRUPT_GAME, REQUEST_VIEWABLE_CARDS, REPLY_VIEWABLE_CARDS, NEW_CHAT_MESSAGE, REPLY_CHAT_MESSAGE, TEST_END_GAME,
}
