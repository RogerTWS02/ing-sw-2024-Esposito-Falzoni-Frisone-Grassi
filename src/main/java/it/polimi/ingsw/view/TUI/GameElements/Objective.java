package it.polimi.ingsw.view.TUI.GameElements;


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

/**
 * Class that draws the objective cards.
 */
public class Objective implements  Views {

    /**
     * JSON array that contains the goal cards.
     */
    private final JSONArray resourceGoalJSONArray;

    /**
     * JSON array that contains the pattern cards.
     */
    private final JSONArray patternGoalJSONArray;

    /**
     * colors used in the TUI.
     */
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[38;5;88m";
    public static final String ANSI_GREEN = "\u001B[38;5;22m";
    public static final String ANSI_BLUE = "\u001B[38;5;26m";
    public static final String ANSI_PURPLE = "\u001B[38;5;91m";

    /**
     * Constructor of the class.
     *
     * @throws IOException If an I/O error occurs.
     * @throws ParseException If there is an error in the parsing.
     */
    public Objective() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        // read the JSON file with the cards
        InputStream inputPatternGoal = getClass().getResourceAsStream("/patternGoalDeck.json");
        BufferedReader bufferPatternGoal = new BufferedReader(new InputStreamReader(inputPatternGoal));
        patternGoalJSONArray = (JSONArray) parser.parse(bufferPatternGoal);

        InputStream inputResourceGoal = getClass().getResourceAsStream("/resourcesGoalDeck.json");
        BufferedReader bufferResourceGoal = new BufferedReader(new InputStreamReader(inputResourceGoal));
        resourceGoalJSONArray = (JSONArray) parser.parse(bufferResourceGoal);
    }

    /**
     * Draw the goal cards.
     *
     * @param uuid Array of the UUID of the goal cards to be drawn.
     * @return Array list of strings that represent the cards.
     * @throws IOException If an I/O error occurs.
     * @throws ParseException If there is an error in the parsing.
     */
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

        for (int x = 0; x < uuid.length; x++) {
            // get the index from the UUID
            int index = Integer.parseInt(uuid[x].replaceAll("[A-Z]+_", ""));
            JSONObject JSONCard;

            if (uuid[x].charAt(0) == 'P') {
                // create the pattern card and the pattern required to obtain the points
                JSONCard = (JSONObject) patternGoalJSONArray.get(index - 1);
                stringObjective[x][0] = "Pattern goal card ";
                uuidToString(uuid[x], stringObjective[x]);
            } else {
                // create the resource goal card and set the color
                JSONCard = (JSONObject) resourceGoalJSONArray.get(index - 1);
                String resources = "";
                for (int i = 0; i < 3; i++) {
                    // convert the resources requested by the goal card to emoji
                    resources  = resources.concat(" " + Views.stringToEmoji((String) ((JSONArray) JSONCard.get("resources")).get(i)));
                }
                // some goal card require just 2 resources so we add a space to format the card
                if (resources.length() < 3) {
                    resources = resources + " ";
                }
                stringObjective[x][0] = "Resource goal card";
                stringObjective[x][2] = "┌────────────┐";
                stringObjective[x][3] = "│ " + resources + "  │";
                stringObjective[x][4] = "└────────────┘";
            }
            stringObjective[x][1]= "   "+(((Number)JSONCard.get("points")).intValue()) + " points   ";
        }

        if(uuid.length == 3){
            // actively create the cards to print with color and attributes
            ArrayList<String> cards = new ArrayList<>();
            cards.add("┌───────────────────────────────────────────────────────────┬───────────────────────────────┐");
            cards.add("│ Common goals                                              │ Secret goal                   │");
            cards.add("├───────────────────────────────────────────────────────────┼───────────────────────────────┤");
            cards.add("│╔═══════════════════════════╗╔═══════════════════════════╗ │ ╔═══════════════════════════╗ │");
            cards.add("│║      "+stringObjective[0][0]+"   ║║      "+stringObjective[1][0]+"   ║ │ ║      "+stringObjective[2][0]+"   ║ │"+ANSI_RESET);
            cards.add("│║        "+stringObjective[0][2]+"     ║║        "+stringObjective[1][2]+"     ║ │ ║        "+stringObjective[2][2]+"     ║ │"+ANSI_RESET);
            cards.add("│║        "+stringObjective[0][3]+"     ║║        "+stringObjective[1][3]+"     ║ │ ║        "+stringObjective[2][3]+"     ║ │"+ANSI_RESET);
            cards.add("│║        "+stringObjective[0][4]+"     ║║        "+stringObjective[1][4]+"     ║ │ ║        "+stringObjective[2][4]+"     ║ │"+ANSI_RESET);
            cards.add("│║        "+stringObjective[0][1]+"     ║║        "+stringObjective[1][1]+"     ║ │ ║        "+stringObjective[1][1]+"     ║ │"+ANSI_RESET);
            cards.add("│╚═══════════════════════════╝╚═══════════════════════════╝ │ ╚═══════════════════════════╝ │");
            cards.add("└───────────────────────────────────────────────────────────┴───────────────────────────────┘");

            //Views.clearScreen();
            return cards;
        } else {
            // actively create the cards to print with color and attributes
            ArrayList<String> cards = new ArrayList<>();
            cards.add("┌───────────────────────────────────────────────────────────┐");
            cards.add("│ Common goals                                              │");
            cards.add("├───────────────────────────────────────────────────────────┤");
            cards.add("│╔═══════════════════════════╗╔═══════════════════════════╗ │");
            cards.add("│║      "+stringObjective[0][0]+"   ║║      "+stringObjective[1][0]+"   ║ │"+ANSI_RESET);
            cards.add("│║        "+stringObjective[0][2]+"     ║║        "+stringObjective[1][2]+"     ║ │"+ANSI_RESET);
            cards.add("│║        "+stringObjective[0][3]+"     ║║        "+stringObjective[1][3]+"     ║ │"+ANSI_RESET);
            cards.add("│║        "+stringObjective[0][4]+"     ║║        "+stringObjective[1][4]+"     ║ │"+ANSI_RESET);
            cards.add("│║        "+stringObjective[0][1]+"     ║║        "+stringObjective[1][1]+"     ║ │"+ANSI_RESET);
            cards.add("│╚═══════════════════════════╝╚═══════════════════════════╝ │");
            cards.add("└───────────────────────────────────────────────────────────┘");

            //Views.clearScreen();
            for (int i = 0; i < 11; i++) {
                System.out.println(cards.get(i));
            }
            return null;
        }

    }


    // hard decode of the uuid of the pattern goal card

    /**
     * Convert the pattern of a pattern goal card to emoji, in order to be drawn.
     *
     * @param uuid The UUID of the card.
     * @param card The array of strings that represent the pattern of the card.
     */
    private void uuidToString(String uuid,String[] card) {
        switch (uuid) {
            case "PGC_1" -> {
                card[2] = "  ░░  ░░ " + ANSI_RED + " ▇▇  "+ANSI_RESET;
                card[3] = "  ░░ " + ANSI_RED + " ▇▇ " + ANSI_RESET + " ░░  ";
                card[4] = ANSI_RED + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
            }
            case "PGC_2" -> {
                card[2] = ANSI_GREEN + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
                card[3] = "  ░░  " + ANSI_GREEN + "▇▇  " + ANSI_RESET + "░░  ";
                card[4] = "  ░░  ░░ " + ANSI_GREEN + " ▇▇  "+ANSI_RESET;
            }
            case "PGC_3" -> {
                card[2] = "  ░░  ░░ " + ANSI_BLUE + " ▇▇  "+ANSI_RESET;
                card[3] = "  ░░  " + ANSI_BLUE + "▇▇  " + ANSI_RESET + "░░  ";
                card[4] = ANSI_BLUE + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
            }
            case "PGC_4" -> {
                card[2] = ANSI_PURPLE + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
                card[3] = "  ░░  " + ANSI_PURPLE + "▇▇  " + ANSI_RESET + "░░  ";
                card[4] = "  ░░  ░░ " + ANSI_PURPLE + " ▇▇  "+ANSI_RESET;
            }
            case "PGC_5" -> {
                card[2] = "  ░░  " + ANSI_RED + "▇▇ " + ANSI_RESET + " ░░  ";
                card[3] = "  ░░  " + ANSI_RED + "▇▇ " + ANSI_RESET + " ░░  ";
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
                card[2] = ANSI_BLUE + "  ▇▇ " + ANSI_RESET + " ░░  ░░  ";
                card[4] = "  ░░  " + ANSI_PURPLE + "▇▇ " + ANSI_RESET + " ░░  ";
                card[3] = "  ░░  " + ANSI_PURPLE + "▇▇ " + ANSI_RESET + " ░░  ";
            }
        }
    }
}
