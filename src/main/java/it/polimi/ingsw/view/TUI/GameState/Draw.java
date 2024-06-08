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
import java.util.Objects;


public class Draw implements Views {





     JSONArray resourceJSONArray;
     JSONArray goldJSONArray;
     // colors used in the TUI
     public static final String ANSI_RED_BACKGROUND = "\u001B[48;5;88m";
     public static final String ANSI_GREEN_BACKGROUND = "\u001B[48;5;22m";
     public static final String ANSI_BLUE_BACKGROUND = "\u001B[48;5;26m";
     public static final String ANSI_PURPLE_BACKGROUND = "\u001B[48;5;91m";
     public static final String ANSI_RESET = "\u001B[0m";
     public static final String ANSI_YELLOW = "\u001B[33m";
     public static final String ANSI_WHITE = "\u001B[38;5;231m";


     public Draw() throws IOException, ParseException {

          JSONParser parser = new JSONParser();
          // read the JSON file with the cards
          InputStream inputGold = getClass().getResourceAsStream("/goldenDeck.json");
          BufferedReader bufferGold = new BufferedReader(new InputStreamReader(inputGold));
          goldJSONArray = (JSONArray) parser.parse(bufferGold);

          InputStream inputResource = getClass().getResourceAsStream("/resourceDeck.json");
          BufferedReader bufferResource = new BufferedReader(new InputStreamReader(inputResource));
          resourceJSONArray = (JSONArray) parser.parse(bufferResource);



     }
     // uuid gold card, uuid resource card, last one: the covered one
     public void showDrawable (String[] gUuids, String[] rUuids){
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
          for(int x =0; x<3; x++){
               if(Objects.equals(gUuids[2-x], "")){
                    gStringCard[0][x]= " No card here ";
                    gStringCard[1][x]="0";
                    gStringCard[2][x]="  ";
                    for(int i =3; i<9; i++) gStringCard[i][x]="  ";
                    gStringCard[8][x]=" ".repeat(21);
                    gStringCard[9][x]=ANSI_RESET;
                    continue;
               }


               int index = Integer.parseInt(gUuids[2-x].replaceAll("[A-Z]+_", ""));
               JSONObject JSONCard;
               //create the golden card and set the color
               JSONCard = (JSONObject) goldJSONArray.get(index-1);
               gStringCard[0][x] = "Golden Card" + "[" + (x) + "]";
               if(x==0){gStringCard[0][x] = " Golden Deck  " ;}
               gStringCard[9][x] = ANSI_YELLOW;

               gStringCard[1][x]=String.valueOf(((Number) JSONCard.get("points")).intValue());
               gStringCard[2][x]=Views.stringToEmoji((String) JSONCard.get("permRes"));
               for(int i = 0; i < 4; i++){
                    gStringCard[i+3][x] =Views.stringToEmoji((String)((JSONArray)JSONCard.get("corners")).get(i));
               }
               JSONArray JSONRequire = (JSONArray) JSONCard.get("require");
               gStringCard[8][x]="";
               for (Object o : JSONRequire) {

                    gStringCard[8][x]= gStringCard[8][x].concat(Views.stringToEmoji((String) o));
               }
               int spaces= (23-gStringCard[8][x].length())/2;
               gStringCard[8][x] = " ".repeat(spaces).concat(gStringCard[8][x]).concat(" ".repeat(spaces+1));
               gStringCard[7][x]= (String) JSONCard.get("rule");
               gStringCard[2][x] = (String) JSONCard.get("permRes");
               switch (gStringCard[2][x]){

                    case "WOLF" -> gStringCard[9][x]=gStringCard[9][x].concat(ANSI_BLUE_BACKGROUND);
                    case "LEAF" -> gStringCard[9][x]=gStringCard[9][x].concat(ANSI_GREEN_BACKGROUND);
                    case "MUSHROOM" -> gStringCard[9][x]=gStringCard[9][x].concat(ANSI_RED_BACKGROUND);
                    case "BUTTERFLY" -> gStringCard[9][x]=gStringCard[9][x].concat(ANSI_PURPLE_BACKGROUND);
               }
               gStringCard[2][x] = Views.stringToEmoji((String) JSONCard.get("permRes"));

          }

          // generating resource cards
          for(int x =0; x<3; x++){
               if(Objects.equals(gUuids[2-x], "")){
                    rStringCard[0][x]= "  No card here  ";
                    rStringCard[1][x]="0";
                    rStringCard[2][x]="  ";
                    for(int i =3; i<9; i++) rStringCard[i][x]="  ";
                    rStringCard[8][x]=" ".repeat(21);
                    rStringCard[9][x]=ANSI_RESET;
                    continue;
               }

               int index = Integer.parseInt(rUuids[2-x].replaceAll("[A-Z]+_", ""));
               JSONObject JSONCard;
               //create the golden card and set the color
               JSONCard = (JSONObject) resourceJSONArray.get(index - 1);
               rStringCard[0][x] = "Resource Card" + "[" +(x) + "]";
               if(x==0){rStringCard[0][x] = " Resource Deck  " ;}
               rStringCard[9][x] = ANSI_WHITE;

               rStringCard[1][x] = String.valueOf(((Number) JSONCard.get("points")).intValue());

               for(int i = 0; i < 4; i++){
                    rStringCard[i+3][x] = Views.stringToEmoji((String)((JSONArray)JSONCard.get("corners")).get(i));
               }

               rStringCard[8][x] = " ".repeat(23);
               rStringCard[7][x]= (String) JSONCard.get("rule");
               rStringCard[2][x] = (String) JSONCard.get("permRes");
               switch (rStringCard[2][x]){

                    case "WOLF" -> rStringCard[9][x]=rStringCard[9][x].concat(ANSI_BLUE_BACKGROUND);
                    case "LEAF" -> rStringCard[9][x]=rStringCard[9][x].concat(ANSI_GREEN_BACKGROUND);
                    case "MUSHROOM" -> rStringCard[9][x]=rStringCard[9][x].concat(ANSI_RED_BACKGROUND);
                    case "BUTTERFLY" -> rStringCard[9][x]=rStringCard[9][x].concat(ANSI_PURPLE_BACKGROUND);
               }
               rStringCard[2][x] = Views.stringToEmoji((String) JSONCard.get("permRes"));
          }

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
               .append(gStringCard[9][2]+"╔═══════════════════════════╗")
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
          cards.append(ANSI_RESET + "│ ");
               for(int x =0; x<3; x++){
                    cards
                             .append(gStringCard[9][x] + "║" + ANSI_RESET)
                             .append(" ".repeat(7))
                             .append(gStringCard[9][x]+gStringCard[0][x]+ANSI_RESET)
                             .append(" ".repeat(6))
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
               .append( gStringCard[9][0] + "║" + ANSI_RESET)
               .append(" ".repeat(27))
               .append( gStringCard[9][0] + "║" + ANSI_RESET);
               for(int x = 1; x<3; x++){
                    cards
                          .append(gStringCard[9][x] + "║" + ANSI_RESET)
                          .append(gStringCard[5][x])
                          .append(gStringCard[8][x])
                          .append(gStringCard[6][x])
                          .append(gStringCard[9][x] + "║");
               };


          cards
               .append(ANSI_RESET + " │ ")
               .append("\n")
               .append("│ ")
               .append(gStringCard[9][0]+"╚═══════════════════════════╝")
               .append(gStringCard[9][1]+"╚═══════════════════════════╝")
               .append(gStringCard[9][2]+"╚═══════════════════════════╝")
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
                  .append(rStringCard[9][2] + "╔═══════════════════════════╗")
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
                  .append("\n")
                  .append("│ ");
          for(int x =0; x<3; x++){
               cards
                       .append(rStringCard[9][x] + "║" + ANSI_RESET)
                       .append(" ".repeat(6))
                       .append(rStringCard[9][x]+rStringCard[0][x]+ANSI_RESET)
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
                  .append(rStringCard[9][0] + "║" + ANSI_RESET)
                  .append(" ".repeat(27))
                  .append(rStringCard[9][0] + "║" + ANSI_RESET);
          for(int x = 1;x<3; x++){
               cards
                       .append(rStringCard[9][x] + "║" + ANSI_RESET)
                       .append(rStringCard[5][x])
                       .append(rStringCard[8][x])
                       .append(rStringCard[6][x])
                       .append(rStringCard[9][x] + "║");
          }


          cards
                  .append(ANSI_RESET + " │ ")
                  .append("\n")
                  .append("│ ")
                  .append(rStringCard[9][0] + "╚═══════════════════════════╝")
                  .append(rStringCard[9][1] + "╚═══════════════════════════╝")
                  .append(rStringCard[9][2] + "╚═══════════════════════════╝")
                  .append(ANSI_RESET + " │ ")
                  .append("\n")
                  .append("└")
                  .append("─".repeat(89))
                  .append("┘")
                  .append("\n");


          //Views.clearScreen();
          System.out.println(cards);

     }

}
