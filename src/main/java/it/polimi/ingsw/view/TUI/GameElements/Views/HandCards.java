package it.polimi.ingsw.view.TUI.GameElements.Views;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.TUI.GameState.Views;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

public class HandCards implements Views {
    JSONArray resourceJSONArray;
    JSONArray goldJSONArray;



    // colors used in the TUI
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_WHITE = "\u001B[37m";





    public HandCards() throws IOException, ParseException {

        // read the JSON file with the cards
        InputStream inputresource = getClass().getResourceAsStream("/" + "ResourceDeck.json");
        InputStream inputgold = getClass().getResourceAsStream("/" + "ResourceDeck.json");

        JSONParser parserGold = new JSONParser();
        BufferedReader buffergold = new BufferedReader(new InputStreamReader(inputgold));
        Object JSONObjectGold = parserGold.parse(buffergold);
        JSONArray goldJSONArray = (JSONArray) JSONObjectGold;
        JSONParser parserResource = new JSONParser();
        BufferedReader bufferResource = new BufferedReader(new InputStreamReader(inputresource));
        Object JSONObjectResource = parserResource.parse(bufferResource);
        JSONArray resourceJSONArray = (JSONArray) JSONObjectResource;
    }

    public void showHand(String[] uuid) throws IOException, ParseException {

        // create the cards from the UUID
        PlayableCard[] hand = new PlayableCard[3];
        String[] cardColor = new String[3];
        String[] cardBackground = new String [3];
        for(int x =0; x< 3; x++){
            int index = Integer.parseInt(uuid[x].substring(3, uuid.length));
            if(uuid[x].charAt(0)=='R'){
                // create the resource card and set the color
                JSONObject JSONcard = (JSONObject) resourceJSONArray.get(index-1);
                hand[x]= Views.craftResourceCard(JSONcard);
                cardColor[x]=ANSI_WHITE;
            }
            else {
                // create the golden card and set the color
                JSONObject JSONcard = (JSONObject) goldJSONArray.get(index-1);
                hand[x]= Views.craftGoldenCard(JSONcard);
                cardColor[x]=ANSI_YELLOW;
                
            }
            switch (hand[x].getPermResource()[0]){

                case WOLF -> cardBackground[x]=ANSI_BLUE_BACKGROUND;
                case LEAF -> cardBackground[x]=ANSI_GREEN_BACKGROUND;
                case MUSHROOM -> cardBackground[x]=ANSI_RED_BACKGROUND;
                case BUTTERFLY -> cardBackground[x]=ANSI_PURPLE_BACKGROUND;

            }


        }

        // actively create the3 cards to print with color and attributes
        ArrayList<String> cards= new ArrayList<String>();
         cards.add(cardBackground[0]+cardColor[0]+"          ╔═══════════════════════════╗"+cardBackground[1]+cardColor[1]+"╔═══════════════════════════╗"+cardBackground[2]+cardColor[2]+"╔═══════════════════════════╗");

        String t = cardBackground[0]+cardColor[0]+"          ║ "+ANSI_RESET+ Views.cornerToString(hand[0].getCardCorners()[0]) + "          "+Views.cardToPoint(hand[0])+"        " + Views.cornerToString(hand[0].getCardCorners()[1])+cardBackground[0]+cardColor[0]+"║"+
                   cardBackground[1]+cardColor[1]+"          ║ "+ANSI_RESET+Views.cornerToString(hand[1].getCardCorners()[0]) + "          "+Views.cardToPoint(hand[1])+"        " + Views.cornerToString(hand[1].getCardCorners()[1])+cardBackground[1]+cardColor[1]+"║"+
                   cardBackground[2]+cardColor[2]+"          ║ "+ANSI_RESET+Views.cornerToString(hand[2].getCardCorners()[0]) + "          "+Views.cardToPoint(hand[2])+"        " + Views.cornerToString(hand[2].getCardCorners()[1])+cardBackground[2]+cardColor[2]+"║"
                ;
        cards.add(t);
        for (int i = 0; i < 2; i++) {
            cards.add(cardBackground[0]+cardColor[0]+"          ║                           ║"+cardBackground[1]+cardColor[1]+"║                           ║"+cardBackground[2]+cardColor[2]+"║                           ║");
        }
        String b = cardBackground[0]+cardColor[0]+"          ║ "+ANSI_RESET+Views.cornerToString(hand[0].getCardCorners()[2]) + "         "+Views.cardToRequiredResource(hand[0])+"        " + Views.cornerToString(hand[0].getCardCorners()[3])+cardBackground[0]+cardColor[0]+"║"+
                   cardBackground[1]+cardColor[1]+"          ║ "+ANSI_RESET+Views.cornerToString(hand[1].getCardCorners()[2]) + "         "+Views.cardToRequiredResource(hand[1])+"        " + Views.cornerToString(hand[1].getCardCorners()[3])+cardBackground[1]+cardColor[1]+"║"+
                   cardBackground[2]+cardColor[2]+"          ║ "+ANSI_RESET+Views.cornerToString(hand[2].getCardCorners()[2]) + "         "+Views.cardToRequiredResource(hand[2])+"        " + Views.cornerToString(hand[2].getCardCorners()[3])+cardBackground[2]+cardColor[2]+"║"
                ;
        cards.add(b);
        cards.add(cardBackground[0]+cardColor[0]+"          ╚═══════════════════════════╝"+cardBackground[1]+cardColor[1]+"╚═══════════════════════════╝"+cardBackground[2]+cardColor[2]+"╚═══════════════════════════╝");



        // prints the card
        for(int i = 0; i < cards.size(); i++) {
            System.out.print(cards.get(i));
        }
    }










};
