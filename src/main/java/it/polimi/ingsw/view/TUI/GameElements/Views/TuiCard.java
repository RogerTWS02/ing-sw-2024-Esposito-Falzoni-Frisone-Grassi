package it.polimi.ingsw.view.TUI.GameElements.Views;

import it.polimi.ingsw.model.PlayableCard;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TuiCard {


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
            if(uuid[x].charAt(0)=='R'){
                int index = Integer.parseInt(uuid[x].substring(3, uuid.length));

            }
            else {}
        }



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
};
