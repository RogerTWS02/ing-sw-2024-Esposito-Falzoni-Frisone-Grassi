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


    private final JSONArray resourceJSONArray;
    private final JSONArray goldJSONArray;
    private final JSONArray patternGoalJSONArray;
    private final JSONArray resourceGoalJSONArray;
    private final JSONArray startingJSONArray;



    // colors used in the cards
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";





    public InfoCard() throws IOException, ParseException {



        JSONParser parser = new JSONParser();
        // read the JSON file with the cards
        InputStream inputGold = getClass().getResourceAsStream("/goldenDeck.json");
        BufferedReader bufferGold = new BufferedReader(new InputStreamReader(inputGold));
        goldJSONArray = (JSONArray) parser.parse(bufferGold);

        InputStream inputResource = getClass().getResourceAsStream("/resourceDeck.json");
          BufferedReader bufferResource = new BufferedReader(new InputStreamReader(inputResource));
        resourceJSONArray = (JSONArray) parser.parse(bufferResource);

        InputStream inputPatternGoal = getClass().getResourceAsStream("/patternGoalDeck.json");
        BufferedReader bufferPatternGoal = new BufferedReader(new InputStreamReader(inputPatternGoal));
        patternGoalJSONArray = (JSONArray) parser.parse(bufferPatternGoal);

        InputStream inputResourceGoal = getClass().getResourceAsStream("/resourcesGoalDeck.json");
        BufferedReader bufferResourceGoal = new BufferedReader(new InputStreamReader(inputResourceGoal));
        resourceGoalJSONArray = (JSONArray) parser.parse(bufferResourceGoal);

        InputStream inputStarting = getClass().getResourceAsStream("/startingDeck.json");
        BufferedReader bufferStarting = new BufferedReader(new InputStreamReader(inputStarting));
        startingJSONArray = (JSONArray) parser.parse(bufferStarting);
    }


    // method to print the card and its info

    public void showInfoCard(String uuid) {
        StringBuilder printedCard = new StringBuilder();
        String[] stringCard = new String[14];
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
        stringCard[10]= bCorneer 0
        stringCard[11]= bCorneer 1
        stringCard[12]= bCorneer 2
        stringCard[13]= bCorneer 3

         */
        int index = Integer.parseInt(uuid.replaceAll("[A-Z]+_", ""));
        JSONObject JSONCard;

        // crafting a playable card
        if(uuid.charAt(1)=='C'){

            if (uuid.charAt(0)=='S'){
                JSONCard = (JSONObject) startingJSONArray.get(index - 1);
                stringCard[0] = "Starting Card";
                stringCard[9] = ANSI_WHITE;
                stringCard[1] = (String) JSONCard.get("points");

                JSONArray JSONResource = (JSONArray) JSONCard.get("permRes");
                stringCard[2] = "";
                for (Object o : JSONResource) {
                    stringCard[2] = stringCard[2].concat((Views.stringToEmoji((String) o)));
                }

                for (int i = 3; i < 7; i++) {
                    stringCard[i] = Views.stringToEmoji((String) ((JSONArray) JSONCard.get("backCorners")).get(i - 3));
                }

                stringCard[8]= " ".repeat(29);
                for (int i = 10; i < 14; i++) {
                    stringCard[i] = Views.stringToEmoji((String) ((JSONArray) JSONCard.get("frontCorners")).get(i - 10));
                }

            } else{

                if (uuid.charAt(0) == 'R') {
                    // create the resource card and set the color
                    JSONCard = (JSONObject) resourceJSONArray.get(index - 1);
                    stringCard[0] = "Resource Card";
                    stringCard[9] = ANSI_WHITE;
                } else {
                    // create the golden card and set the color
                    JSONCard = (JSONObject) goldJSONArray.get(index - 1);
                    stringCard[0] = "Golden Card";
                    stringCard[9] = ANSI_YELLOW;
                }


                stringCard[1]= String.valueOf(((Number) JSONCard.get("points")).intValue());
                stringCard[2] = (String) JSONCard.get("permRes");

                for (int i = 3; i < 7; i++) {
                    stringCard[i] = Views.stringToEmoji((String) ((JSONArray) JSONCard.get("corners")).get(i - 3));
                }

                for(int i = 10; i < 14; i++){
                    stringCard[i] = "░░";
                }

                stringCard[7] = (String) JSONCard.get("rule");
                JSONArray JSONRequire = (JSONArray) JSONCard.get("require");

                for (Object o : JSONRequire) {
                    stringCard[8] = stringCard[8].concat((Views.stringToEmoji((String) o)));
                }

                int spaces= (29-stringCard[8].length()*2)/2;
                stringCard[8]= " ".repeat(spaces+1).concat(stringCard[8]).concat(" ".repeat(spaces));

                switch (stringCard[2]) {

                    case "WOLF" ->stringCard[9]= stringCard[9].concat(ANSI_BLUE_BACKGROUND);
                    case "LEAF" ->stringCard[9]=stringCard[9].concat(ANSI_GREEN_BACKGROUND);
                    case "MUSHROOM" ->stringCard[9]= stringCard[9].concat(ANSI_RED_BACKGROUND);
                    case "BUTTERFLY" ->stringCard[9]= stringCard[9].concat(ANSI_PURPLE_BACKGROUND);

                }

                stringCard[2]=Views.stringToEmoji((String) JSONCard.get("permRes"));

            }

            int spaces= (34-stringCard[2].length())/2;
            stringCard[2]= " ".repeat(spaces+1).concat(stringCard[2]).concat(" ".repeat(spaces));

            // actively creating the array to print
            printedCard
                .append(stringCard[9] + "╔══════════════════════════════════╗╔══════════════════════════════════╗\n")
                //.append("\n")
                .append(stringCard[9] + "║ " + ANSI_RESET)
                .append(stringCard[3])
                .append(" ".repeat(13))
                .append(Views.cardToPoint(stringCard[1], stringCard[7]))
                .append(" ".repeat(12))
                .append(stringCard[4])
                .append(stringCard[9] + "║")
                .append(stringCard[9] + "║ " + ANSI_RESET)
                .append(stringCard[10])
                .append(" ".repeat(29))
                .append(stringCard[11])
                .append(stringCard[9] + "║\n")
                .append(stringCard[9] + "║ " + ANSI_RESET)
                .append(stringCard[10])
                .append(" ".repeat(29))
                .append(stringCard[11])
                .append(stringCard[9] + "║")
                .append(stringCard[9] + "║ " + ANSI_RESET)
                .append(stringCard[10])
                .append(" ".repeat(29))
                .append(stringCard[11])
                .append(stringCard[9] + "║\n")
                .append(stringCard[9] + "║ " + ANSI_RESET)
                .append(stringCard[10])
                .append(" ".repeat(29))
                .append(stringCard[11])
                .append(stringCard[9] + "║")
                .append(stringCard[2])
                .append(stringCard[9] + "║\n")
                .append(stringCard[9] + "║ " + ANSI_RESET)
                .append(stringCard[10])
                .append(" ".repeat(29))
                .append(stringCard[11])
                .append(stringCard[9] + "║")
                .append(stringCard[9] + "║ " + ANSI_RESET)
                .append(stringCard[10])
                .append(" ".repeat(29))
                .append(stringCard[11])
                .append(stringCard[9] + "║\n")
                .append(stringCard[9] + "║ " + ANSI_RESET)
                .append(stringCard[5])
                .append(stringCard[8])
                .append(stringCard[6])
                .append(stringCard[9] + "║")
                .append(stringCard[9] + "║ " + ANSI_RESET)
                .append(stringCard[12])
                .append(" ".repeat(29))
                .append(stringCard[13])
                .append(stringCard[9] + "║\n")
                .append(stringCard[9]+"╚══════════════════════════════════╝╚══════════════════════════════════╝\n")
                .append("               Front                                Back                \n");

        } else {
        //crafting goal card

        /*
        Goal card structure
        card[0]= type
        card[1]= point
        card[2]= pattern/decorator
        card[3]=pattern/resources
        card[4]=pattern/decorator
        */
            String[] stringObjective = new String[5];
            if (uuid.charAt(0) == 'P') {
                // create the resource card and set the color
                JSONCard = (JSONObject) patternGoalJSONArray.get(index - 1);
                stringObjective[0] = "Pattern goal card ";
                uuidToString(uuid, stringObjective);

            } else {
                // create the resource goal card and set the color
                JSONCard = (JSONObject) resourceGoalJSONArray.get(index - 1);
                String resources = "";
                for (int i = 0; i < 3; i++) {
                    resources  = resources.concat(" " + Views.stringToEmoji((String) ((JSONArray) JSONCard.get("resources")).get(i)));
                }
                if (resources.length() < 6) {
                    resources = resources + " ";
                }
                stringObjective[0] = "Resource goal card";
                stringObjective[2] = "┌────────────┐";
                stringObjective[3] = "│  " + resources + "   │";
                stringObjective[4] = "└────────────┘";
            }

            printedCard
                    .append("╔══════════════════════════════════╗╔══════════════════════════════════╗\n")
                    .append("║           "+stringObjective[0]+"         ║║"+" ".repeat(34)+"║\n")
                    .append("║           "+stringObjective[2]+"         ║║"+" ".repeat(34)+"║\n")
                    .append("║           "+stringObjective[3]+"         ║║       This card has no back      ║\n")
                    .append("║           "+stringObjective[4]+"         ║║"+" ".repeat(34)+"║\n")
                    .append("║           "+stringObjective[1]+"         ║║"+" ".repeat(34)+"║\n")
                    .append("╚══════════════════════════════════╝╚══════════════════════════════════╝\n")
                    .append("               Front                                Back                \n");

        }

        //clear the screen before printing something
        Views.clearScreen();
        //print the array
        System.out.println(printedCard);
    }




    private void uuidToString(String uuid,String[] card) {
        switch (uuid) {
            case "PGC_1" -> {
                card[2] = "  ░░  ░░ " + ANSI_RED + " ▇▇  "+ANSI_RESET;
                card[3] = "  ░░ " + ANSI_RED + " ▇▇ " + ANSI_RESET + " ░░  ";
                card[4] = ANSI_RED + "▇▇ " + ANSI_RESET + " ░░  ░░  ";
            }
            case "PGC_2" -> {
                card[4] = "  ░░  ░░ " + ANSI_GREEN + " ▇▇  "+ANSI_RESET;
                card[3] = "  ░░  " + ANSI_GREEN + "▇▇  " + ANSI_RESET + "░░  ";
                card[2] = ANSI_GREEN + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
            }
            case "PGC_3" -> {
                card[2] = "  ░░  ░░ " + ANSI_BLUE + " ▇▇  "+ANSI_RESET;
                card[3] = "  ░░  " + ANSI_BLUE + "▇▇  " + ANSI_RESET + "░░  ";
                card[4] = ANSI_BLUE + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
            }
            case "PGC_4" -> {
                card[4] = "  ░░  ░░ " + ANSI_PURPLE + " ▇▇  "+ANSI_RESET;
                card[3] = "  ░░  " + ANSI_PURPLE + "▇▇  " + ANSI_RESET + "░░  ";
                card[2] = ANSI_PURPLE + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
            }
            case "PGC_5" -> {
                card[2] = "  ░░  " + ANSI_RED + "▇▇ " + ANSI_RESET + " ░░  ";
                card[3] = "  ░░  " + ANSI_RED + " ▇▇ " + ANSI_RESET + " ░░  ";
                card[4] = "  ░░  ░░ " + ANSI_GREEN + " ▇▇  "+ANSI_RESET;
            }
            case "PGC_6" -> {
                card[2] = "  ░░  " + ANSI_GREEN + "▇▇ " + ANSI_RESET + " ░░  ";
                card[3] = "  ░░  " + ANSI_GREEN + "▇▇ " + ANSI_RESET + " ░░  ";
                card[4] = ANSI_PURPLE + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
            }
            case "PGC_7" -> {
                card[2] = "  ░░  ░░ " + ANSI_RED + " ▇▇  "+ANSI_RESET;
                card[3] = "  ░░  " + ANSI_BLUE + "▇▇ " + ANSI_RESET + " ░░  ";
                card[4] = "  ░░  " + ANSI_BLUE + "▇▇ " + ANSI_RESET + " ░░  ";

            }
            case "PGC_8" -> {
                card[3] = ANSI_BLUE + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
                card[4] = "  ░░  " + ANSI_PURPLE + "▇▇ " + ANSI_RESET + " ░░  ";
                card[5] = "  ░░  " + ANSI_PURPLE + "▇▇ " + ANSI_RESET + " ░░  ";
            }


        }


    }











}
