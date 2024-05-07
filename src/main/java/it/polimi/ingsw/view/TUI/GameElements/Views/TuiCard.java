package it.polimi.ingsw.view.TUI.GameElements.Views;

import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TuiCard {
    public void printCard(String[] uuid) {
        InputStream inputresource = getClass().getResourceAsStream("/" + "ResourceDeck.json");
        InputStream inputgold = getClass().getResourceAsStream("/" + "ResourceDeck.json");

        JSONParser parser = new JSONParser();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
        Object JSONObject = parser.parse(buffer);
        String cards = """
                        ╔═════════════════════╗╔═════════════════════╗╔═════════════════════╗
                        ║                     ║║                     ║║                     ║
                        ║                     ║║                     ║║                     ║
                        ║                     ║║                     ║║                     ║
                        ║                     ║║                     ║║                     ║
                        ╚═════════════════════╝╚═════════════════════╝╚═════════════════════╝
                     
                            
                """;
        String corner = " ▒▒▒";

    }
}
