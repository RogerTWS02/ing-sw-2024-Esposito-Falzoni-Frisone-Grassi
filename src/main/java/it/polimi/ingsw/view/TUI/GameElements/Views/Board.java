package it.polimi.ingsw.view.TUI.GameElements.Views;

import it.polimi.ingsw.view.TUI.GameState.Views;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Board {
    private final String startingCard;
    private final JSONArray resourceJSONArray;
    private final JSONArray goldJSONArray;
    private final JSONArray startingJSONArray;

    private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    private static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    private static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public Board(String startingCard) throws IOException, ParseException {
        //The UUID of the starting card
        this.startingCard = startingCard;

        InputStream inputresource = getClass().getResourceAsStream("/resourceDeck.json");
        InputStream inputgold = getClass().getResourceAsStream("/goldenDeck.json");
        InputStream inputstart = getClass().getResourceAsStream("/startingDeck.json");

        JSONParser parser = new JSONParser();

        BufferedReader bufferGold = new BufferedReader(new InputStreamReader(inputgold));
        goldJSONArray = (JSONArray) parser.parse(bufferGold);
        BufferedReader bufferResource = new BufferedReader(new InputStreamReader(inputresource));
        resourceJSONArray = (JSONArray) parser.parse(bufferResource);
        BufferedReader bufferStart = new BufferedReader(new InputStreamReader(inputstart));
        startingJSONArray = (JSONArray) parser.parse(bufferResource);
    }

    //The isFlipped parameter is used only to determine whether the starting card is flipped or not
    public void drawBoardCard(String uuid, int positionx, int positiony, boolean isFlipped) {

        int index = Integer.parseInt(uuid.replaceAll("[A-Z]+_", ""));
        JSONObject JSONCard;
        String type;
        String background;
        String border;
        String resource;

        if(uuid.equals(startingCard)){
            background = ANSI_WHITE_BACKGROUND;
            JSONCard = (JSONObject) startingJSONArray.get(index-1);
            JSONArray JSONresources = (JSONArray) JSONCard.get("permRes");

            String[] permanentResources = new String[JSONresources.size()];
            for(int i = 0; i < JSONresources.size(); i++){
                permanentResources[i] = Views.stringToEmoji((String) JSONresources.get(i));
            }

            StringBuilder sb = new StringBuilder();
            sb
                    .append("┌")
                    .append("─".repeat(7))
                    .append("┐")
                    .append("\n")
                    .append("│")
                    .append(background)
                    .append(" ");

            for (String permanentResource : permanentResources) {
                sb.append(permanentResource);
            }
            sb
                    .append(" ")
                    .append(ANSI_RESET)
                    .append("│")
                    .append("\n")
                    .append("└")
                    .append("─".repeat(7))
                    .append("┘")
                    .append(ANSI_RESET);

            JSONArray JSONcorners = (JSONArray) JSONCard.get("frontCorners");
            if (isFlipped) {
                JSONcorners = (JSONArray) JSONCard.get("backCorners");
            }

            //Here we have to create the cards around this one depending on the corners
            for (int i = 0; i < 4; i++) {
                //TODO
            }

            return;
        }

        if(uuid.charAt(0)=='R'){
            // create the resource card
            JSONCard = (JSONObject) resourceJSONArray.get(index-1);
            type = "resource";

        } else {
            // create the golden card
            JSONCard = (JSONObject) goldJSONArray.get(index - 1);
            type = "golden";
        }

        StringBuilder sb = new StringBuilder();


        border = type.equals("resource") ? ANSI_WHITE : ANSI_YELLOW;
        resource = switch (JSONCard.get("permRes").toString()) {
            case "MUSHROOM" -> ANSI_RED_BACKGROUND;
            case "LEAF" -> ANSI_GREEN_BACKGROUND;
            case "WOLF" -> ANSI_BLUE_BACKGROUND;
            case "BUTTERFLY" -> ANSI_PURPLE_BACKGROUND;
            default -> "";
        };

        if(positionx > 9 && positiony > 9){
            sb
                    .append(border)
                    .append("┌")
                    .append("─".repeat(7))
                    .append("┐")
                    .append("\n")
                    .append("│")
                    .append(resource)
                    .append(" ")
                    .append(positionx)
                    .append(" ")
                    .append(positiony)
                    .append(" ")
                    .append(ANSI_RESET)
                    .append(border)
                    .append("│")
                    .append("\n")
                    .append("└")
                    .append("─".repeat(7))
                    .append("┘")
                    .append(ANSI_RESET);

            System.out.println(sb);
        }
        if(positionx < 10 && positiony < 10){
            sb
                    .append(border)
                    .append("┌")
                    .append("─".repeat(7))
                    .append("┐")
                    .append("\n")
                    .append("│")
                    .append(resource)
                    .append(" ")
                    .append(1)
                    .append(" ".repeat(3))
                    .append(1)
                    .append(" ")
                    .append(ANSI_RESET)
                    .append(border)
                    .append("│")
                    .append("\n")
                    .append("└")
                    .append("─".repeat(7))
                    .append("┘")
                    .append(ANSI_RESET);

            System.out.println(sb);
        }
        if(positionx > 9 && positiony < 10 || positionx < 9 && positiony > 10){
            sb
                    .append(border)
                    .append("┌")
                    .append("─".repeat(7))
                    .append("┐")
                    .append("\n")
                    .append("│")
                    .append(resource)
                    .append(" ")
                    .append(positionx)
                    .append("  ")
                    .append(positiony)
                    .append(" ")
                    .append(ANSI_RESET)
                    .append(border)
                    .append("│")
                    .append("\n")
                    .append("└")
                    .append("─".repeat(7))
                    .append("┘")
                    .append(ANSI_RESET);

            System.out.println(sb);
        }

    }

}

