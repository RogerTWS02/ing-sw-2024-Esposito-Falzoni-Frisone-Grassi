package it.polimi.ingsw.view.TUI.GameState;

import it.polimi.ingsw.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Optional;

public interface Views {


    //converts the resource to a string to print in the TUI
    static String stringToEmoji(String resource){
        if(resource == null) return "  ";
        return switch (resource){
            case "EMPTY"         -> "░░";
            case "WOLF"          -> "🐺";
            case "MUSHROOM"      -> "🍄";
            case "LEAF"          -> "🍃";
            case "BUTTERFLY"     -> "🦋";
            case "FEATHER"       -> "🪶";
            case "SCROLL"        -> "📜";
            case "GLASSVIAL"     -> "🫙";
            default              -> "  ";
        };
    }



    // converts points and rule for the TUI
     static String cardToPoint(String point, String rule){
            if (point == null || rule == null){return "     ";}
            if (point.equals("0")){return "     ";}
            else{
              return point + switch (rule){
                    case "CORNERS"   -> " p ◲";
                    case "SCROLL"    -> " p📜";
                    case "FEATHER"   -> " p🪶";
                    case "GLASSVIAL" -> " p🫙";
                    default          -> " p  ";
             };
            }
        }


    // clear the screen
        static void clearScreen()
        {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

