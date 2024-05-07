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
        for(int x =0; x< 3; x++){
            int index = Integer.parseInt(uuid[x].substring(3, uuid.length));
            if(uuid[x].charAt(0)=='R'){
                JSONObject JSONcard = (JSONObject) resourceJSONArray.get(index-1);
                hand[x]= craftResourceCard(JSONcard);
            }
            else {
                JSONObject JSONcard = (JSONObject) goldJSONArray.get(index-1);
                hand[x]= craftResourceCard(JSONcard);
                
            }
        }



        String cards = """
                        ╔═════════════════════╗╔═════════════════════╗╔═════════════════════╗
                        ║                   W ║║                     ║║                     ║
                        ║                     ║║                     ║║                     ║
                        ║                     ║║                     ║║                     ║
                        ║ G                 L ║║                     ║║                     ║
                        ╚═════════════════════╝╚═════════════════════╝╚═════════════════════╝
                     
                            
                """;
        String corner = " ▒▒▒";

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

    private String cornerToImage(Corner corner){
        if (corner == null){return "   ";}
        return switch (corner.getCornerResource().toString()){
            case "WOLF" -> "W ";
            case "MUSHROOM" -> "M ";
            case "LEAF" -> "L";
            case "BUTTERFLY" -> "B";
            case "FEATHER" -> "F";
            case "SCROLL" -> "S";
            case "GLASSVIAL" -> "I";
            default -> "░░";
        };
    }




};
