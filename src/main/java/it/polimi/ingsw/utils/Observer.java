package it.polimi.ingsw.utils;

/**
 * Interface for being notified by an observable object.
 */
public interface Observer {
    /**
     * Notifies observers of a change, specified in the message parameter.
     *
     * @param message The message containing the information about the change.
     */
    default void update(Object message) {
        System.err.println("Message type not supported: " + message.getClass().getName());
    }
}
