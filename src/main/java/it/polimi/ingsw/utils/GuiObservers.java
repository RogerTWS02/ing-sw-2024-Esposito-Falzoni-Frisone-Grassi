package it.polimi.ingsw.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Abstract class for GUI observers.
 */
public abstract class GuiObservers {
    private final ArrayList<Observer> observers;

    /**
     * Constructor for the class.
     */
    public GuiObservers() {
        observers = new ArrayList<>();
    }

    /**
     * Adds an observer to the list of observers.
     *
     * @param observer The observer to add.
     */
    public synchronized void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Notifies all the observers.
     *
     * @param message The message which contains the changes to notify.
     */
    public void notifyObservers(Object message) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for (Observer observer : observers) {
            Method method = observer.getClass().getMethod("update", message.getClass());
            method.invoke(observer, message);
        }
    }
}
