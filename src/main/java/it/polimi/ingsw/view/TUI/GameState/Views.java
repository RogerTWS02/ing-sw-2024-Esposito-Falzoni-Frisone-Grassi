package it.polimi.ingsw.view.TUI.GameState;

/**
 * Interface that contains the methods to be implemented by the classes that represent the TUI views of the game.
 */
public interface Views {

    /**
     * Converts the resource to an emoji.
     *
     * @param resource The resource to be converted.
     * @return The emoji representing the resource.
     */
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


    /**
     * Converts resources to emoji.
     *
     * @param point The points the card gives everytime the rule is matched.
     * @param rule The rule to get the points.
     * @return The string representing the points and the rule of the card.
     */
     static String cardToPoint(String point, String rule){
            if (point == null && rule == null){return "     ";}
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

        /**
        * Clears the screen of the terminal.
        */
        static void clearScreen() {
            try{
                final String os = System.getProperty("os.name");
                if (os.contains("Windows")) {
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                } else {
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                }
            }catch (final Exception e){
                System.out.println("Error in clearing the screen");
            }
        }
    }

