package it.polimi.ingsw.network.message;

/**
 * Enumeration which contains all the possible types of messages that can be sent between client and server.
 */
public enum MessageType {
    REPLY_BAD_REQUEST,

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

    //Messages to request/receive the score of the players
    REPLY_POINTS_UPDATE,
    TEST_MESSAGE,

    //Messages to request/receive the status of the game
    HEARTBEAT,
    HEARTBEAT_ACK,


    //messages to make a move
    REQUEST_PLAYER_MOVE,
    REPLY_UPDATED_SCORE,
    NOTIFY_LAST_TURN,
    REPLY_LAST_TURN,

    NOTIFY_GAME_STARTING, REPLY_CHOICES_MADE, REPLY_YOUR_TURN, NOTIFY_END_GAME, REQUEST_INTERRUPT_GAME, REPLY_INTERRUPT_GAME, REQUEST_VIEWABLE_CARDS, REPLY_VIEWABLE_CARDS, NEW_CHAT_MESSAGE, REPLY_CHAT_MESSAGE, TEST_END_GAME, REQUEST_AVAILABLE_LOBBIES, REQUEST_PLAYER_BOARD, REQUEST_PLAYER_BOARD_INFOS, REPLY_PLAYER_BOARD_INFOS, REPLY_PLAYER_BOARD, REQUEST_PLAYER_CARD, REQUEST_PLAYER_CARD_INFOS, REPLY_PLAYER_CARD, REPLY_AVAILABLE_LOBBIES
}
