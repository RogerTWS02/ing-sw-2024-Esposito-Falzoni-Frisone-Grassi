package it.polimi.ingsw.view.TUI.GameElements.Views;

import it.polimi.ingsw.model.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TuiCard {
    JSONArray resourceJSONArray;
    JSONArray goldJSONArray;

    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_WHITE = "\u001B[37m";





    public TuiCard() throws IOException, ParseException {


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

    public void printCard(String[] uuid) throws IOException, ParseException {
        PlayableCard[] hand = new PlayableCard[3];
        String[] cardColor = new String[3];
        String[] cardBackground = new String [3];
        for(int x =0; x< 3; x++){
            int index = Integer.parseInt(uuid[x].substring(3, uuid.length));
            if(uuid[x].charAt(0)=='R'){
                JSONObject JSONcard = (JSONObject) resourceJSONArray.get(index-1);
                hand[x]= craftResourceCard(JSONcard);
                cardColor[x]=ANSI_WHITE;
            }
            else {
                JSONObject JSONcard = (JSONObject) goldJSONArray.get(index-1);
                hand[x]= craftResourceCard(JSONcard);
                cardColor[x]=ANSI_YELLOW;
                
            }
            switch (hand[x].getPermResource()[0]){

                case WOLF -> cardBackground[x]=ANSI_BLUE_BACKGROUND;
                case LEAF -> cardBackground[x]=ANSI_GREEN_BACKGROUND;
                case MUSHROOM -> cardBackground[x]=ANSI_RED_BACKGROUND;
                case BUTTERFLY -> cardBackground[x]=ANSI_PURPLE_BACKGROUND;

            }


        }


        ArrayList<String> cards= new ArrayList<String>();
         cards.add(cardBackground[0]+cardColor[0]+"          ╔═══════════════════════════╗"+cardBackground[1]+cardColor[1]+"╔═══════════════════════════╗"+cardBackground[2]+cardColor[2]+"╔═══════════════════════════╗");

        String t = cardBackground[0]+cardColor[0]+"          ║ "+ANSI_RESET+cornerToString(hand[0].getCardCorners()[0]) + "          "+cardToPoint(hand[0])+"        " + cornerToString(hand[0].getCardCorners()[1])+cardBackground[0]+cardColor[0]+"║"+
                   cardBackground[1]+cardColor[1]+"          ║ "+ANSI_RESET+cornerToString(hand[1].getCardCorners()[0]) + "          "+cardToPoint(hand[1])+"        " + cornerToString(hand[1].getCardCorners()[1])+cardBackground[1]+cardColor[1]+"║"+
                   cardBackground[2]+cardColor[2]+"          ║ "+ANSI_RESET+cornerToString(hand[2].getCardCorners()[0]) + "          "+cardToPoint(hand[2])+"        " + cornerToString(hand[2].getCardCorners()[1])+cardBackground[2]+cardColor[2]+"║"
                ;
        cards.add(t);
        for (int i = 0; i < 2; i++) {
            cards.add(cardBackground[0]+cardColor[0]+"          ║                           ║"+cardBackground[1]+cardColor[1]+"║                           ║"+cardBackground[2]+cardColor[2]+"║                           ║");
        }
        String b = cardBackground[0]+cardColor[0]+"          ║ "+ANSI_RESET+cornerToString(hand[0].getCardCorners()[2]) + "         "+cardToRequiredResource(hand[0])+"        " + cornerToString(hand[0].getCardCorners()[3])+cardBackground[0]+cardColor[0]+"║"+
                   cardBackground[1]+cardColor[1]+"          ║ "+ANSI_RESET+cornerToString(hand[1].getCardCorners()[2]) + "         "+cardToRequiredResource(hand[1])+"        " + cornerToString(hand[1].getCardCorners()[3])+cardBackground[1]+cardColor[1]+"║"+
                   cardBackground[2]+cardColor[2]+"          ║ "+ANSI_RESET+cornerToString(hand[2].getCardCorners()[2]) + "         "+cardToRequiredResource(hand[2])+"        " + cornerToString(hand[2].getCardCorners()[3])+cardBackground[2]+cardColor[2]+"║"
                ;
        cards.add(b);
        cards.add(cardBackground[0]+cardColor[0]+"          ╚═══════════════════════════╝"+cardBackground[1]+cardColor[1]+"╚═══════════════════════════╝"+cardBackground[2]+cardColor[2]+"╚═══════════════════════════╝");


    }



    private PlayableCard craftResourceCard(JSONObject JSONcard){
        String UUID = (String) JSONcard.get("UUID");
        int points = ((Number) JSONcard.get("points")).intValue();
        Resource permRes = stringToResource((String) JSONcard.get("permRes"));
        JSONArray JSONCorners = (JSONArray) JSONcard.get("corners");
        PlayableCard card = new ResourceCard(new Resource[]{permRes}, null, points, UUID);
        card.setCorners(craftCornerArray(JSONCorners, card));
        return card;
    }



    private Corner[] craftCornerArray(JSONArray JSONCorners, PlayableCard card){
        Corner[] corners = new Corner[4];
        for(int i = 0; i < 4; i++){
            if(JSONCorners.get(i) == null){
                corners[i] = null;
            } else if(JSONCorners.get(i).equals("EMPTY")){
                corners[i] = new Corner(i, card, Optional.empty());
            } else {
                corners[i] = new Corner(i, card, Optional.ofNullable(stringToResource((String) JSONCorners.get(i))));
            }
        }
        return corners;
    }

    private PlayableCard craftGoldenCard(JSONObject JSONcard){
        Object rule;
        String UUID = (String) JSONcard.get("UUID");
        int points = ((Number) JSONcard.get("points")).intValue();
        Resource permRes = stringToResource((String) JSONcard.get("permRes"));
        JSONArray JSONCorners = (JSONArray) JSONcard.get("corners");
        String temp = (String) JSONcard.get("rule");
        if(temp != null && temp.equals("CORNERS")){
            rule = "CORNERS";
        } else {
            rule = stringToResource((String) JSONcard.get("rule"));
        }
        JSONArray JSONRequire = (JSONArray) JSONcard.get("require");
        ArrayList<Resource> require = new ArrayList<>();
        for(int i = 0; i < JSONRequire.size(); i++){
            require.add(stringToResource((String) JSONRequire.get(i)));
        }
        PlayableCard card = new GoldenCard(new Resource[]{permRes}, null, points, require, rule, UUID);
        card.setCorners(craftCornerArray(JSONCorners, card));
        return card;
    }
    private Resource stringToResource(String resource){
        if(resource == null){
            return null;
        }
        return switch (resource) {
            case "WOLF" -> Resource.WOLF;
            case "MUSHROOM" -> Resource.MUSHROOM;
            case "LEAF" -> Resource.LEAF;
            case "BUTTERFLY" -> Resource.BUTTERFLY;
            case "FEATHER" -> Resource.FEATHER;
            case "SCROLL" -> Resource.SCROLL;
            case "GLASSVIAL" -> Resource.GLASSVIAL;
            default -> null;
        };
    }

    private String cornerToString(Corner corner){
        if (corner == null){return "   ";}
        return switch (corner.getCornerResource().toString()){
            case "WOLF" -> "🐺 ";
            case "MUSHROOM" -> "🍄 ";
            case "LEAF" -> "🍃 ";
            case "BUTTERFLY" -> "🦋 ";
            case "FEATHER" -> "🪶 ";
            case "SCROLL" -> "📜 ";
            case "GLASSVIAL" -> "🫙 ";
            default -> "░░";
        };

    }



    private String resourceToEmoji(Resource resource){
        return switch (resource.toString()){
            case "WOLF" -> "🐺 ";
            case "MUSHROOM" -> "🍄 ";
            case "LEAF" -> "🍃 ";
            case "BUTTERFLY" -> "🦋 ";
            default -> throw new IllegalStateException("Unexpected value: " + resource.toString());
        };
    }
    private String cardToPoint(PlayableCard card){
        if(card instanceof GoldenCard){
            if(card.getRule().toString()=="NONE"){
                return card.getPoints()+" p ";
            }
            else return switch (card.getRule().toString()){
                case "CORNERS" -> card.getPoints()+"p ◲";
                case "SCROLL"  -> card.getPoints()+"p 📜";
                case "FEATHER" -> card.getPoints()+"p 🪶";
                case "GLASSVIAL" -> card.getPoints()+"p 🫙";
                default -> throw new IllegalStateException("Unexpected value: " + card.getRule().toString());

            };
        }

        return switch (card.getPoints()){
            case 0 -> "    ";
            default -> card.getPoints()+" p ";

        };
    }
    private String cardToRequiredResource(PlayableCard card){
        if (card instanceof GoldenCard){
            String required="     ";
            for(Resource resource: ((GoldenCard) card).getRequiredResource()){
                required=required+resourceToEmoji(resource);
            }
            required= required+" ".repeat((5-required.length()));
            return  required;

        }
        else return "     ";
    }






};
