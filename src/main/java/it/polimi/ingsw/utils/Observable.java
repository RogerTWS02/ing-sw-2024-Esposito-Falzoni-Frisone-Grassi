package it.polimi.ingsw.utils;

/**
 * Interface for observable objects.
 */
public interface Observable {
    /**
     * Adds an observer to the list of observers.
     *
     * @param observer The observer to add.
     */
    void addObserver(Observer observer);

    /**
     * Removes an observer from the list of observers.
     *
     * @param observer The observer to remove.
     */
    void removeObserver(Observer observer);

    /**
     * Notifies all the observers.
     */
    void notifyObservers();
}
