package it.polimi.ingsw.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Random;

public class GameController {


    //Draws a card from the deck passed by parameter
    public Object drawCard(JSONArray deck){
        Random random = new Random();
        int randomIndex = random.nextInt(deck.size());
        JSONObject JSONcard = (JSONObject) deck.get(randomIndex);
        deck.remove(randomIndex);

        //TODO: parse the JSON object and craft the card

        return null; //So IDE doesn't give error
    }
}
