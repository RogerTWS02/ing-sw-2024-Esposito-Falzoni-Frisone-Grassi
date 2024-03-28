package it.polimi.ingsw.controller;

import java.util.Random;

public class GameController {


    //Draws a card from the deck passed by parameter
    //TODO: da adeguare!
    public Object drawCard(Object[] deck){
        int lastCardIndex;
        for(lastCardIndex = deck.length - 1; deck[lastCardIndex] == null; lastCardIndex--);
        int bound = lastCardIndex + 1;
        Random random = new Random();
        int randomIndex = random.nextInt(bound);
        Object card = deck[randomIndex];
        deck[randomIndex] = deck[lastCardIndex];
        deck[lastCardIndex] = null;
        return card;
    }
}
