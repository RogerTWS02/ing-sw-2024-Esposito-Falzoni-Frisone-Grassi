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
import java.util.Optional;

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
        InputStream inputresource = getClass().getResourceAsStream("/" + "resourceDeck.json");
        InputStream inputgold = getClass().getResourceAsStream("/" + "goldenDeck.json");

        JSONParser parserGold = new JSONParser();
        BufferedReader buffergold = new BufferedReader(new InputStreamReader(inputgold));
        Object JSONObjectGold = parserGold.parse(buffergold);
        JSONArray goldJSONArray = (JSONArray) JSONObjectGold;
        JSONParser parserResource = new JSONParser();
        BufferedReader bufferResource = new BufferedReader(new InputStreamReader(inputresource));
        Object JSONObjectResource = parserResource.parse(bufferResource);
        JSONArray resourceJSONArray = (JSONArray) JSONObjectResource;
    }

    public  ArrayList<String> showHand(String[] uuid) throws IOException, ParseException {
        String[][] stringCard = new String[10][3];

        // create the cards from the UUID
            /*
        stringCard[][3 cards in the hand]
        stringCard[0][] = Type
        stringCard[1][] = Points
        stringCard[2][] = Permanent Resource
        stringCard[3][] = Corner 0
        stringCard[4][] = Corner 1
        stringCard[5][] = Corner 2
        stringCard[6][] = Corner 3
        stringCard[7][] = RULE to obtain the points
        stringCard[8][] = Required Resources
        stringCard[9][] = Border color + Background color
         */

        for(int x =0; x< 3; x++){
            int index = Integer.parseInt(uuid[x].substring(3, uuid[x].length()));
            JSONObject JSONCard;
            if(uuid[x].charAt(0)=='R'){
                // create the resource card and set the color
                JSONCard = (JSONObject) resourceJSONArray.get(index-1);
                stringCard[0][x]="Resource Card";
                stringCard[9][x]=ANSI_WHITE;}
            else { // create the golden card and set the color
                    JSONCard = (JSONObject) goldJSONArray.get(index-1);
                    stringCard[0][x]="Golden Card";
                    stringCard[9][x]=ANSI_YELLOW;
            }
                stringCard[1][x]=(String) JSONCard.get("points");
                stringCard[2][x]=Views.stringToEmoji((String) JSONCard.get("permRes"));
                for(int i = 0; i < 4; i++){
                        stringCard[i+3][x] =Views.stringToEmoji((String)((JSONArray)JSONCard.get("corners")).get(i));
                }
                stringCard[7][x]= (String) JSONCard.get("rule");
                JSONArray JSONRequire = (JSONArray) JSONCard.get("require");
                 for(int i = 0; i < JSONRequire.size(); i++){
                    stringCard[8][x].concat((Views.stringToEmoji((String) JSONRequire.get(i))));
                    }
                 stringCard[8][x].concat(" ".repeat(5-JSONRequire.size()));
            switch (stringCard[2][x]){

                case "WOLF" -> stringCard[9][x].concat(ANSI_BLUE_BACKGROUND);
                case "LEAF" -> stringCard[9][x].concat(ANSI_GREEN_BACKGROUND);
                case "MUSHROOM" -> stringCard[9][x].concat(ANSI_RED_BACKGROUND);
                case "BUTTERFLY" -> stringCard[9][x].concat(ANSI_PURPLE_BACKGROUND);

            }


        }


        // actively create the3 cards to print with color and attributes
        ArrayList<String> cards= new ArrayList<String>();

        cards.add("        ┌─────────────────────────────────────────────────────────────────────────────────────────┐ ");
        cards.add("        │ Hand cards                                                                              │ ");
        cards.add("        ├─────────────────────────────────────────────────────────────────────────────────────────┤ ");
         cards.add("        │ "+stringCard[9][0]+"╔═══════════════════════════╗"+stringCard[9][1]+"╔═══════════════════════════╗"+stringCard[9][2]+"╔═══════════════════════════╗"+ANSI_RESET+" │ ");

        String t ="        │ "+
                stringCard[9][0]+"║"+ANSI_RESET+stringCard[3][0]+ "           "+Views.cardToPoint(stringCard[1][0],stringCard[7][0])+"        " + stringCard[4][0]+stringCard[9][0]+"║"+
                stringCard[9][1]+"║"+ANSI_RESET+stringCard[3][1]+ "           "+Views.cardToPoint(stringCard[1][1],stringCard[7][1])+"        " + stringCard[4][1]+stringCard[9][1]+"║"+
                stringCard[9][2]+"║"+ANSI_RESET+stringCard[3][2]+ "           "+Views.cardToPoint(stringCard[1][2],stringCard[7][2])+"        " + stringCard[4][2]+stringCard[9][2]+"║"+ANSI_RESET+" │ "
                ;
        cards.add(t);
        for (int i = 0; i < 3; i++) {
            cards.add("        │ "+stringCard[9][0]+"║                           ║"+stringCard[9][1]+"║                           ║"+stringCard[9][2]+"║                           ║"+ANSI_RESET+" │ ");
        }
        String b ="        │ "+
                stringCard[9][0]+"║"+ANSI_RESET+stringCard[5][0] + "          "+stringCard[8][0]+"        " + stringCard[6][0]+stringCard[9][0]+"║"+
                stringCard[9][1]+"║"+ANSI_RESET+stringCard[5][1] + "          "+stringCard[8][1]+"        " + stringCard[6][1]+stringCard[9][1]+"║"+
                stringCard[9][2]+"║"+ANSI_RESET+stringCard[5][2] + "          "+stringCard[8][2]+"        " + stringCard[6][2]+stringCard[9][2]+"║  "
                ;
        cards.add(b);
        cards.add("        │ "+stringCard[9][0]+"╚═══════════════════════════╝"+stringCard[9][1]+"╚═══════════════════════════╝"+stringCard[9][2]+"╚═══════════════════════════╝"+ANSI_RESET+" │ ");
        cards.add("        └─────────────────────────────────────────────────────────────────────────────────────────┘ ");




        return cards;
    }











    }




