package it.polimi.ingsw.view.TUI.GameState;

import it.polimi.ingsw.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Optional;

public interface Views {


    // the view has only the UUID of the card, it needs to recreate it
    //craft resource card from the JSON
     static PlayableCard craftResourceCard(JSONObject JSONcard){
        String UUID = (String) JSONcard.get("UUID");
        int points = ((Number) JSONcard.get("points")).intValue();
        Resource permRes = stringToResource((String) JSONcard.get("permRes"));
        JSONArray JSONCorners = (JSONArray) JSONcard.get("corners");
        PlayableCard card = new ResourceCard(new Resource[]{permRes}, null, points, UUID);
        card.setCorners(craftCornerArray(JSONCorners, card));
        return card;
    }


    //craft corner from JSON
     static Corner[] craftCornerArray(JSONArray JSONCorners, PlayableCard card){
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
    //craft golden card from JSON
     static PlayableCard craftGoldenCard(JSONObject JSONcard){
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



    // converts the string from the JSON to the resource
     static Resource stringToResource(String resource){
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

    //converts the corner to a string to print in the TUI
     static String cornerToString(Corner corner){
        if (corner == null){return "  ";}
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


    //converts the resource to an emoji to print in the TUI for the permanent resource
     static String resourceToEmoji(Resource resource){
        return switch (resource.toString()){
            case "WOLF" -> "🐺 ";
            case "MUSHROOM" -> "🍄 ";
            case "LEAF" -> "🍃 ";
            case "BUTTERFLY" -> "🦋 ";
            default -> throw new IllegalStateException("Unexpected value: " + resource.toString());
        };
    }
    // converts a card points and its rule for the TUI
     static String cardToPoint(PlayableCard card){
        if(card instanceof GoldenCard){
            if(card.getRule().toString()=="NONE"){
                return card.getPoints()+" p ";
            }
            else return switch (card.getRule().toString()){
                case "CORNERS" -> card.getPoints()+" p ◲";
                case "SCROLL"  -> card.getPoints()+" p 📜";
                case "FEATHER" -> card.getPoints()+" p 🪶";
                case "GLASSVIAL" -> card.getPoints()+" p 🫙";
                default -> throw new IllegalStateException("Unexpected value: " + card.getRule().toString());

            };
        }

        return switch (card.getPoints()){
            case 0 -> "    ";
            default -> card.getPoints()+" p ";

        };
    }

    //converts the required resource to an emoji to print in the TUI
     static String cardToRequiredResource(PlayableCard card){
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


    // clear the screen
    static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
