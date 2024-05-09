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


public class Objective implements  Views {
    JSONArray resourceJSONArray;
    JSONArray patternJSONArray;

    // colors used in the TUI

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";



    public void Objective() throws IOException, ParseException {

        // read the JSON file with the cards
        InputStream inputPattern = getClass().getResourceAsStream("/" + "patternGoalDeck.json");
        InputStream inputResource = getClass().getResourceAsStream("/" + "resourceGoalDeck.json");

        JSONParser parserResource = new JSONParser();
        BufferedReader bufferResource = new BufferedReader(new InputStreamReader(inputResource));
        Object JSONObjectResource = parserResource.parse(bufferResource);
        JSONArray resourceJSONArray = (JSONArray) JSONObjectResource;
        JSONParser parserPattern = new JSONParser();
        BufferedReader bufferPattern = new BufferedReader(new InputStreamReader(inputPattern));
        Object JSONObjectPattern = parserPattern.parse(bufferPattern);
        JSONArray patternJSONArray = (JSONArray) JSONObjectPattern;

    }

    public ArrayList<String> showObjective(String[] uuid) throws IOException, ParseException {
        /*
        Goal card structure
        card[0]= type
        card[1]= point
        card[2]= pattern/decorator
        card[3]=pattern/resources
        card[4]=pattern/decorator
         */
        String[][] stringObjective = new String[3][5];
        for (int x = 0; x < 3; x++) {
            int index = Integer.parseInt(uuid[x].substring(3, uuid[x].length()));
            JSONObject JSONCard;
            
            if (uuid[x].charAt(0) == 'P') {
                // create the resource card and set the color
                JSONCard = (JSONObject) patternJSONArray.get(index - 1);
                stringObjective[x][0] = "Pattern goal card ";
                uuidToString(uuid[x], stringObjective[x]);

            } else { // create the resource goal card and set the color
                JSONCard = (JSONObject) resourceJSONArray.get(index - 1);
                String resources = "";
                for (int i = 0; i < 4; i++) {
                    resources.concat(" " + Views.stringToEmoji((String) ((JSONArray) JSONCard.get("resources")).get(i)));
                }
                if (resources.length() < 6) {
                    resources = resources + " ";
                }
                stringObjective[x][0] = "Resource goal card";
                stringObjective[x][2] = "┌────────────┐";
                stringObjective[x][3] = "│  " + resources + "   │";
                stringObjective[x][4] = "└────────────┘";
            }
            stringObjective [x][1]= (String)JSONCard.get("points")+ " points";
        }

            // actively create the3 cards to print with color and attributes
            ArrayList<String> cards = new ArrayList<String>();
            cards.add("         ┌───────────────────────────────────────────────────────────┬───────────────────────────────┐");
            cards.add("         │ Common goals                                              │ Private goal                  │");
            cards.add("         ├───────────────────────────────────────────────────────────┼───────────────────────────────┤");
            cards.add("         │╔═══════════════════════════╗╔═══════════════════════════╗ │ ╔═══════════════════════════╗ │");
            String t = "         │║    " + stringObjective[0][0] + "     ║║    " + stringObjective[1][0] + "     ║ │ ║    " + stringObjective[2][0] + "     ║ │";
            cards.add(t);
            cards.add("         │║       "+stringObjective[0][2]+"      ║║       "+stringObjective[1][2]+"      ║ │ ║       "+stringObjective[2][2]+"      ║ │");
            cards.add("         │║       "+stringObjective[0][3]+"      ║║       "+stringObjective[1][3]+"      ║ │ ║       "+stringObjective[2][3]+"      ║ │");
            cards.add("         │║       "+stringObjective[0][4]+"      ║║       "+stringObjective[1][4]+"      ║ │ ║       "+stringObjective[2][4]+"      ║ │");
            String b ="         │║          "+stringObjective[0][1]+"         ║║          "+stringObjective[1][1]+"         ║ │ ║          "+stringObjective[1][1]+"         ║ │";
            cards.add(b);
            cards.add("         │╚═══════════════════════════╝╚═══════════════════════════╝ │ ╚═══════════════════════════╝ │");







        return cards;
    };


    private void uuidToString(String uuid,String[] card) {
        switch (uuid) {
            case "PGC_1" -> {
                card[3] = "  ░░  ░░ " + ANSI_RED + " ▇▇  ";
                card[4] = "  ░░ " + ANSI_RED + " ▇▇ " + ANSI_RESET + " ░░  ";
                card[5] = ANSI_RED + "▇▇ " + ANSI_RESET + " ░░  ░░  ";
            }
            case "PGC_2" -> {
                card[5] = "  ░░  ░░ " + ANSI_GREEN + " ▇▇  ";
                card[4] = "  ░░  " + ANSI_GREEN + "▇▇  " + ANSI_RESET + "░░  ";
                card[3] = ANSI_GREEN + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
            }
            case "PGC_3" -> {
                card[3] = "  ░░  ░░ " + ANSI_BLUE + " ▇▇  ";
                card[4] = "  ░░  " + ANSI_BLUE + "▇▇  " + ANSI_RESET + "░░  ";
                card[5] = ANSI_BLUE + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
            }
            case "PGC_4" -> {
                card[5] = "  ░░  ░░ " + ANSI_PURPLE + " ▇▇  ";
                card[4] = "  ░░  " + ANSI_PURPLE + "▇▇  " + ANSI_RESET + "░░  ";
                card[3] = ANSI_PURPLE + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
            }
            case "PGC_5" -> {
                card[3] = "  ░░  " + ANSI_RED + "▇▇ " + ANSI_RESET + " ░░  ";
                card[4] = "  ░░  " + ANSI_RED + " ▇▇ " + ANSI_RESET + " ░░  ";
                card[5] = "  ░░  ░░ " + ANSI_GREEN + " ▇▇  ";
            }
            case "PGC_6" -> {
                card[3] = "  ░░  " + ANSI_GREEN + "▇▇ " + ANSI_RESET + " ░░  ";
                card[4] = "  ░░  " + ANSI_GREEN + "▇▇ " + ANSI_RESET + " ░░  ";
                card[5] = ANSI_PURPLE + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
            }
            case "PGC_7" -> {
                card[3] = "  ░░  ░░ " + ANSI_RED + " ▇▇  ";
                card[4] = "  ░░  " + ANSI_BLUE + "▇▇ " + ANSI_RESET + " ░░  ";
                card[5] = "  ░░  " + ANSI_BLUE + "▇▇ " + ANSI_RESET + " ░░  ";

            }
            case "PGC_8" -> {
                card[3] = ANSI_BLUE + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
                card[4] = "  ░░  " + ANSI_PURPLE + "▇▇ " + ANSI_RESET + " ░░  ";
                card[5] = "  ░░  " + ANSI_PURPLE + "▇▇ " + ANSI_RESET + " ░░  ";
            }


        }


    }




    }
