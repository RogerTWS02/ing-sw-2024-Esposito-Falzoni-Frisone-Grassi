package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class GameController {
    private final Game currentGame;

    public GameController(Game currentGame) {
        this.currentGame = currentGame;
    }

    //Draws a card from the deck passed by parameter
    public Object drawCard(JSONArray deck){
        Random random = new Random();
        int randomIndex = random.nextInt(deck.size());
        JSONObject JSONcard = (JSONObject) deck.get(randomIndex);
        deck.remove(randomIndex);

        //TODO: parse the JSON object and craft the card

        return null; //So IDE doesn't give error
    }

    /* not very clear how we can add a new player if the array is private and there's no setPlayer function,
    as implemented now it overwrites all the names,
     not sure how we want to implement the port now it`s a random 50 just to have it working
     we need to choose the exception
     */
    public void addPlayer(String nickname) throws SecurityException{
        /* check if player already exists */
        if (currentGame.getPlayers().stream().map(Player::getNickname).toList().contains(nickname)){
            throw new SecurityException("player already present");
        }
        /* if does not exist adds it to the Arraylist */
        else {
            ArrayList<Player> players = currentGame.getPlayers();
            players.add(new Player(nickname,50));
            currentGame.setPlayers(players);
        }
    }
}
