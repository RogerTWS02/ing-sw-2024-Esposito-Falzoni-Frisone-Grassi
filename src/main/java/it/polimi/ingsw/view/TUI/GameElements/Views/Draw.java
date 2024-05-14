package it.polimi.ingsw.view.TUI.GameElements.Views;
import it.polimi.ingsw.model.PlayableCard;
import it.polimi.ingsw.view.TUI.GameState.Views;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Draw implements Views {
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

    /**
     public void showDrawable throws IOException, void ParseException(String[] Guuids, String[] Ruuids, String[] resources){
          // uuid gold card, uuid resource card, color covered card, first gold then resource one


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

     public void showDraw(String[] uuid) throws IOException, ParseException {
          String[][] gStringCard = new String[10][2];
          String[][] rStringCard = new String[10][2];

          // create the cards from the UUID
            /*
        gStringCard[][3 cards in the hand]
        gStringCard[0][] = Type
        gStringCard[1][] = Points
        gStringCard[2][] = Permanent Resource
        gStringCard[3][] = Corner 0
        gStringCard[4][] = Corner 1
        gStringCard[5][] = Corner 2
        gStringCard[6][] = Corner 3
        gStringCard[7][] = RULE to obtain the points
        gStringCard[8][] = Required Resources
        gStringCard[9][] = Border color + Background color

          PlayableCard[] hand = new PlayableCard[3];
                 for(int x =0; x< 2; x++){

                      {
                     int index = Integer.parseInt(uuid[x].substring(3, uuid[x].length()));
                    JSONObject JSONCard;
                    //create the golden card and set the color
                         JSONCard = (JSONObject) goldJSONArray.get(index-1);
                         gStringCard[0][x]="Golden Card";
                         gStringCard[9][x]=ANSI_YELLOW;
               
                    gStringCard[1][x]=(String) JSONCard.get("points");
                    gStringCard[2][x]=Views.stringToEmoji((String) JSONCard.get("permRes"));
                    for(int i = 0; i < 4; i++){
                         gStringCard[i+3][x] =Views.stringToEmoji((String)((JSONArray)JSONCard.get("corners")).get(i));
                    }
                    gStringCard[7][x]= (String) JSONCard.get("rule");
                    JSONArray JSONRequire = (JSONArray) JSONCard.get("require");
                    for(int i = 0; i < JSONRequire.size(); i++){
                         gStringCard[8][x].concat((Views.stringToEmoji((String) JSONRequire.get(i))));
                    }
                    gStringCard[8][x].concat(" ".repeat(5-JSONRequire.size()));
                    switch (gStringCard[2][x]){

                         case "WOLF" -> gStringCard[9][x].concat(ANSI_BLUE_BACKGROUND);
                         case "LEAF" -> gStringCard[9][x].concat(ANSI_GREEN_BACKGROUND);
                         case "MUSHROOM" -> gStringCard[9][x].concat(ANSI_RED_BACKGROUND);
                         case "BUTTERFLY" -> gStringCard[9][x].concat(ANSI_PURPLE_BACKGROUND);

                         }


          };

               for(x =0; x< 3; x++){
                         int index = Integer.parseInt(uuid[x].substring(3, uuid[x].length()));
                         JSONObject JSONCard;
                         if(uuid[x].charAt(0)=='R'){
                              // create the resource card and set the color
                              JSONCard = (JSONObject) resourceJSONArray.get(index-1);
                              rStringCard[0][x]="Resource Card";
                              rStringCard[9][x]=ANSI_WHITE;}
                         else { // create the golden card and set the color
                              JSONCard = (JSONObject) goldJSONArray.get(index-1);
                              rStringCard[0][x]="Golden Card";
                              rStringCard[9][x]=ANSI_YELLOW;
                         }
                         rStringCard[1][x]=(String) JSONCard.get("points");
                         rStringCard[2][x]=Views.stringToEmoji((String) JSONCard.get("permRes"));
                         for(int i = 0; i < 4; i++){
                              rStringCard[i+3][x] =Views.stringToEmoji((String)((JSONArray)JSONCard.get("corners")).get(i));
                         }
                         rStringCard[7][x]= (String) JSONCard.get("rule");
                         JSONArray JSONRequire = (JSONArray) JSONCard.get("require");
                         for(int i = 0; i < JSONRequire.size(); i++){
                              rStringCard[8][x].concat((Views.stringToEmoji((String) JSONRequire.get(i))));
                         }
                         rStringCard[8][x].concat(" ".repeat(5-JSONRequire.size()));
                         switch (rStringCard[2][x]){

                              case "WOLF" -> rStringCard[9][x].concat(ANSI_BLUE_BACKGROUND);
                              case "LEAF" -> rStringCard[9][x].concat(ANSI_GREEN_BACKGROUND);
                              case "MUSHROOM" -> rStringCard[9][x].concat(ANSI_RED_BACKGROUND);
                              case "BUTTERFLY" -> rStringCard[9][x].concat(ANSI_PURPLE_BACKGROUND);

                         }


                    }};}



     }
     */
}
