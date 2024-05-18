package it.polimi.ingsw.view.TUI.GameElements.Views;

import it.polimi.ingsw.view.TUI.GameState.Views;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class BottomRow {
    HandCards handCards= new HandCards();
    Objective goalCards= new Objective();


    public BottomRow() throws IOException, ParseException {
    }

    // hand (1...2..3..)+ goal(common...common...private)
    public void showBottomRow(String[] uuidHand, String[]uuidGoals) throws IOException, ParseException {
        ArrayList<String> hand = handCards.showHand(uuidHand);
        ArrayList<String> goal = goalCards.showObjective(uuidGoals);
        //clear the screen before printing something
        Views.clearScreen();
        // prints hand card + goal card
        for (int i = 0; i < 10; i++) {
            System.out.println(hand.get(i)+goal.get(i));
        };
    }
}
