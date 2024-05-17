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
            case "EMPTY" -> "░░";
            case "WOLF" -> "🐺 ";
            case "MUSHROOM" -> "🍄 ";
            case "LEAF" -> "🍃 ";
            case "BUTTERFLY" -> "🦋 ";
            case "FEATHER" -> "\uD83E\uDEB6 ";
            case "SCROLL" -> "📜 ";
            case "GLASSVIAL" -> "\uD83E\uDED9 ";
            default -> "  ";
        };

    }



    // converts points and rule for the TUI
     static String cardToPoint(String point, String rule){
        if (point.equals("0") || rule == null){ return "     ";}
        else{
          return point + switch (rule){
                case "NONE" -> " p  ";
                case "CORNERS" -> " p ◲";
                case "SCROLL"  -> " p 📜";
                case "FEATHER" -> " p 🪶";
                case "GLASSVIAL" -> " p 🫙";
                default -> " p  ";
         };
        }
     }


    // clear the screen
    static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
