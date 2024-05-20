package it.polimi.ingsw.view.TUI.GameState;
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
import java.util.ArrayList;

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




     public Draw() throws IOException, ParseException {

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
     // uuid gold card, uuid resource card, firstly the covered one
     public void showDrawable (String[] gUuids, String[] rUuids, String[] resources)throws IOException, ParseException {
          String[][] gStringCard = new String[10][3];
          String[][] rStringCard = new String[10][3];

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
          */


          // generating golden cards
          for(int x =0; x< 3; x++){


               int index = Integer.parseInt(gUuids[x].substring(3));
               JSONObject JSONCard;
               //create the golden card and set the color
               JSONCard = (JSONObject) goldJSONArray.get(index-1);
               gStringCard[0][x] = "Golden Card" + "[" + (2-x) + "]";
               gStringCard[9][x] = ANSI_YELLOW;

               gStringCard[1][x]=(String) JSONCard.get("points");
               gStringCard[2][x]=Views.stringToEmoji((String) JSONCard.get("permRes"));
               for(int i = 0; i < 4; i++){
                    gStringCard[i+3][x] =Views.stringToEmoji((String)((JSONArray)JSONCard.get("corners")).get(i));
               }
               JSONArray JSONRequire = (JSONArray) JSONCard.get("require");

               for (Object o : JSONRequire) {

                    gStringCard[8][x] += (Views.stringToEmoji((String) o));
               }
               int spaces= (21-gStringCard[8][x].length())/2;
               gStringCard[8][x] = " ".repeat(spaces).concat(gStringCard[8][x]).concat(" ".repeat(spaces));
               gStringCard[7][x]= (String) JSONCard.get("rule");
               switch (gStringCard[2][x]){

                    case "WOLF" -> gStringCard[9][x]=gStringCard[9][x].concat(ANSI_BLUE_BACKGROUND);
                    case "LEAF" -> gStringCard[9][x]=gStringCard[9][x].concat(ANSI_GREEN_BACKGROUND);
                    case "MUSHROOM" -> gStringCard[9][x]=gStringCard[9][x].concat(ANSI_RED_BACKGROUND);
                    case "BUTTERFLY" -> gStringCard[9][x]=gStringCard[9][x].concat(ANSI_PURPLE_BACKGROUND);
               }
          }

          // generating resource cards
          for(int x =0; x< 3; x++){

               int index = Integer.parseInt(rUuids[x].substring(3));
               JSONObject JSONCard;
               //create the golden card and set the color
               JSONCard = (JSONObject) goldJSONArray.get(index - 1);
               rStringCard[0][x] = "Resource Card" + "[" +(2-x) + "]";
               rStringCard[9][x] = ANSI_WHITE;

               rStringCard[1][x] = (String) JSONCard.get("points");
               rStringCard[2][x] = Views.stringToEmoji((String) JSONCard.get("permRes"));
               for(int i = 0; i < 4; i++){
                    rStringCard[i+3][x] = Views.stringToEmoji((String)((JSONArray)JSONCard.get("corners")).get(i));
               }
               JSONArray JSONRequire = (JSONArray) JSONCard.get("require");

               for (Object o : JSONRequire) {

                    rStringCard[8][x] += (Views.stringToEmoji((String) o));
               }
               int spaces= (21-rStringCard[8][x].length())/2;
               rStringCard[8][x] = " ".repeat(spaces).concat(rStringCard[8][x]).concat(" ".repeat(spaces));
               rStringCard[7][x]= (String) JSONCard.get("rule");
               switch (rStringCard[2][x]){

                    case "WOLF" -> rStringCard[9][x]=rStringCard[9][x].concat(ANSI_BLUE_BACKGROUND);
                    case "LEAF" -> rStringCard[9][x]=rStringCard[9][x].concat(ANSI_GREEN_BACKGROUND);
                    case "MUSHROOM" -> rStringCard[9][x]=rStringCard[9][x].concat(ANSI_RED_BACKGROUND);
                    case "BUTTERFLY" -> rStringCard[9][x]=rStringCard[9][x].concat(ANSI_PURPLE_BACKGROUND);
               };
          };

          // print the cards
          StringBuilder cards = new StringBuilder();

          //adding gold cards
          cards
               .append("┌")
               .append("─".repeat(89))
               .append("┐")
               .append("\n")
               .append("│")
               .append(" ".repeat(35))
               .append("Draw form the table")
               .append(" ".repeat(35))
               .append("│")
               .append("\n")
               .append("├")
               .append("─".repeat(89))
               .append("┤")
               .append("\n")
               .append("│ ")
               .append(gStringCard[9][0]+"╔═══════════════════════════╗")
               .append(gStringCard[9][1]+"╔═══════════════════════════╗")
               .append(gStringCard[9][0]+"╔═══════════════════════════╗")
               .append(ANSI_RESET + " │ ")
               .append("\n")
               .append("│ ")
               .append( gStringCard[9][0] + "║" + ANSI_RESET)
               .append(" ".repeat(27))
               .append(gStringCard[9][0] + "║" + ANSI_RESET);

               for(int x = 1;x<3; x++){
                    cards
                          .append( gStringCard[9][x] + "║" + ANSI_RESET)
                          .append(gStringCard[3][x])
                          .append(" ".repeat(9))
                          .append(Views.cardToPoint(gStringCard[1][x],gStringCard[7][x]))
                          .append(" ".repeat(9))
                          .append(gStringCard[4][x])
                          .append(gStringCard[9][x] + "║");}
               cards.append(ANSI_RESET + " │ ")
                       .append("\n")
                       .append("│ ");
               for(int x =0; x<3; x++){
                    cards
                            .append( gStringCard[9][x] + "║" + ANSI_RESET)
                            .append(" ".repeat(27))
                            .append( gStringCard[9][x] + "║" + ANSI_RESET);
               }
               cards.append(ANSI_RESET + " │ ")
                      .append("\n");
               for(int x =0; x<3; x++){
                    cards
                             .append(gStringCard[9][x] + "║" + ANSI_RESET)
                             .append(" ".repeat(6))
                             .append(gStringCard[0][x])
                             .append(" ".repeat(5))
                             .append(gStringCard[9][x] + "║" + ANSI_RESET);
               }
               cards.append(ANSI_RESET + " │ ")
                      .append("\n")
                      .append("│ ");
               for(int x =0; x<3; x++){
                   cards
                        .append(gStringCard[9][x] + "║" + ANSI_RESET)
                        .append(" ".repeat(27))
                        .append(gStringCard[9][x] + "║" + ANSI_RESET);
               }

          cards
               .append(ANSI_RESET + " │ ")
               .append("\n")
               .append("│ ")
               .append("│ ")
               .append( gStringCard[9][0] + "║" + ANSI_RESET)
               .append(" ".repeat(27))
               .append( gStringCard[9][0] + "║" + ANSI_RESET)
               .append( gStringCard[9][1] + "║" + ANSI_RESET);
               for(int x = 1; x<3; x++){
                    cards
                          .append( gStringCard[9][1] + "║" + ANSI_RESET)
                          .append(gStringCard[5][x])
                          .append(" ".repeat(9))
                          .append(gStringCard[8][x])
                          .append(" ".repeat(8))
                          .append(gStringCard[6][x])
                          .append(gStringCard[9][x] + "║");
               };


          cards
               .append(ANSI_RESET + " │ ")
               .append("\n")
               .append("│ ")
               .append(gStringCard[9][0]+"╚═══════════════════════════╝")
               .append(gStringCard[9][1]+"╚═══════════════════════════╝")
               .append(gStringCard[9][0]+"╚═══════════════════════════╝")
               .append(ANSI_RESET + " │ ")
               .append("\n");


          // adding resource gards
          cards

                  .append("├")
                  .append("─".repeat(89))
                  .append("┤")
                  .append("\n")
                  .append("│ ")
                  .append(rStringCard[9][0] + "╔═══════════════════════════╗")
                  .append(rStringCard[9][1] + "╔═══════════════════════════╗")
                  .append(rStringCard[9][0] + "╔═══════════════════════════╗")
                  .append(ANSI_RESET + " │ ")
                  .append("\n")
                  .append("│ ")
                  .append(rStringCard[9][0] + "║" + ANSI_RESET)
                  .append(" ".repeat(27))
                  .append(rStringCard[9][0] + "║" + ANSI_RESET);

          for(int x = 1;x<3; x++){
               cards
                       .append(rStringCard[9][x] + "║" + ANSI_RESET)
                       .append(rStringCard[3][x])
                       .append(" ".repeat(9))
                       .append(Views.cardToPoint(rStringCard[1][x],rStringCard[7][x]))
                       .append(" ".repeat(9))
                       .append(rStringCard[4][x])
                       .append(rStringCard[9][x] + "║");
          }

          cards.append(ANSI_RESET + " │ ")
                  .append("\n")
                  .append("│ ");
          for(int x =0; x<3; x++){
               cards
                       .append(rStringCard[9][x] + "║" + ANSI_RESET)
                       .append(" ".repeat(27))
                       .append(rStringCard[9][x] + "║" + ANSI_RESET);
          }
          cards.append(ANSI_RESET + " │ ")
                  .append("\n");
          for(int x =0; x<3; x++){
               cards
                       .append(rStringCard[9][x] + "║" + ANSI_RESET)
                       .append(" ".repeat(6))
                       .append(rStringCard[0][x])
                       .append(" ".repeat(5))
                       .append(rStringCard[9][x] + "║" + ANSI_RESET);
          }
          cards.append(ANSI_RESET + " │ ")
                  .append("\n")
                  .append("│ ");
          for(int x =0; x<3; x++){
               cards
                       .append(rStringCard[9][x] + "║" + ANSI_RESET)
                       .append(" ".repeat(27))
                       .append(rStringCard[9][x] + "║" + ANSI_RESET);
          }
          cards
                  .append(ANSI_RESET + " │ ")
                  .append("\n")
                  .append("│ ")
                  .append("│ ")
                  .append(rStringCard[9][0] + "║" + ANSI_RESET)
                  .append(" ".repeat(27))
                  .append(rStringCard[9][0] + "║" + ANSI_RESET)
                  .append(rStringCard[9][1] + "║" + ANSI_RESET);
          for(int x = 1;x<3; x++){
               cards
                       .append(rStringCard[9][1] + "║" + ANSI_RESET)
                       .append(rStringCard[5][x])
                       .append(" ".repeat(9))
                       .append(rStringCard[8][x])
                       .append(" ".repeat(8))
                       .append(rStringCard[6][x])
                       .append(rStringCard[9][x] + "║");
          }


          cards
                  .append(ANSI_RESET + " │ ")
                  .append("\n")
                  .append("│ ")
                  .append(rStringCard[9][0] + "╚═══════════════════════════╝")
                  .append(rStringCard[9][1] + "╚═══════════════════════════╝")
                  .append(rStringCard[9][0] + "╚═══════════════════════════╝")
                  .append(ANSI_RESET + " │ ")
                  .append("\n")
                  .append("└")
                  .append("─".repeat(89))
                  .append("┘")
                  .append("\n");


          Views.clearScreen();
          System.out.println(cards);

     }

}
