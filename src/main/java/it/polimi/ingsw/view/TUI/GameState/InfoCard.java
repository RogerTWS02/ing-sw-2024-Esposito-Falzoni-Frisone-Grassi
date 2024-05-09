package it.polimi.ingsw.view.TUI.GameState;

import it.polimi.ingsw.model.Corner;
import it.polimi.ingsw.model.GoldenCard;
import it.polimi.ingsw.model.PlayableCard;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class InfoCard  implements Views{
    // colors used in the cards
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_WHITE = "\u001B[37m";

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

    public InfoCard() throws IOException, ParseException {
    }


    // method to print the card and its info

    public ArrayList<String> showInfoCard(String uuid){
        String[] stringCard = new String[10];
        // create the card from the UUID
            /*
        stringCard[0] = Type
        stringCard[1] = Points
        stringCard[2] = Permanent Resource
        stringCard[3] = Corner 0
        stringCard[4] = Corner 1
        stringCard[5] = Corner 2
        stringCard[6] = Corner 3
        stringCard[7] = RULE to obtain the points
        stringCard[8] = Required Resources
        stringCard[9] = Border color + Background color
         */
        int index = Integer.parseInt(uuid.substring(3, uuid.length()));
        JSONObject JSONCard;
        if(uuid.charAt(0)=='R'){
            // create the resource card and set the color
            JSONCard = (JSONObject) resourceJSONArray.get(index-1);
            stringCard[0]="Resource Card";
            stringCard[9]=ANSI_WHITE;}
        else { // create the golden card and set the color
            JSONCard = (JSONObject) goldJSONArray.get(index-1);
            stringCard[0]="Golden Card";
            stringCard[9]=ANSI_YELLOW;
        }
        stringCard[1]=(String) JSONCard.get("points");
        stringCard[2]=Views.stringToEmoji((String) JSONCard.get("permRes"));
        for(int i = 0; i < 4; i++){
            stringCard[i+3] =Views.stringToEmoji((String)((JSONArray)JSONCard.get("corners")).get(i));
        }
        stringCard[7]= (String) JSONCard.get("rule");
        JSONArray JSONRequire = (JSONArray) JSONCard.get("require");
        for(int i = 0; i < JSONRequire.size(); i++){
            stringCard[8].concat((Views.stringToEmoji((String) JSONRequire.get(i))));
        }
        stringCard[8].concat(" ".repeat(5-JSONRequire.size()));
        switch (stringCard[2]){

            case "WOLF" -> stringCard[9].concat(ANSI_BLUE_BACKGROUND);
            case "LEAF" -> stringCard[9].concat(ANSI_GREEN_BACKGROUND);
            case "MUSHROOM" -> stringCard[9].concat(ANSI_RED_BACKGROUND);
            case "BUTTERFLY" -> stringCard[9].concat(ANSI_PURPLE_BACKGROUND);

        }


        // actively creating the array to print
        ArrayList<String> printedCard= new ArrayList<String>();
        printedCard.add("   "+stringCard[9]+"╔══════════════════════════════════╗");
        String t= "   "+stringCard[9]+"║ "+ANSI_RESET+stringCard[3]+"             "+Views.cardToPoint(stringCard[1], stringCard[7])+"            "+stringCard[4]+stringCard[9]+" ║";
        printedCard.add(t);
        for(int x=0;x<5;x++) {
            printedCard.add("   " + stringCard[9]+ "║" + ANSI_RESET + "                                  " + stringCard[9] + "║");
        };
        String b= "   "+stringCard[9]+"║ "+ANSI_RESET+stringCard[5]+"            "+stringCard[8]+"            "+stringCard[5]+stringCard[9]+"║";
        printedCard.add(b);
        printedCard.add("   "+stringCard[9]+"╚══════════════════════════════════╝");



        //clear the screen before printing something
        Views.clearScreen();
        //print the array
        for(int i = 0; i < printedCard.size(); i++) {
            System.out.print(printedCard.get(i));
        }



        // return the array in case of future changes
        return printedCard;
    };













}
