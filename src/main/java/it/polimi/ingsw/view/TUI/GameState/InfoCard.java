package it.polimi.ingsw.view.TUI.GameState;

import it.polimi.ingsw.model.Corner;
import it.polimi.ingsw.model.GoldenCard;
import it.polimi.ingsw.model.PlayableCard;

import java.util.ArrayList;

public class InfoCard  implements Views{
    // colors used in the cards
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_WHITE = "\u001B[37m";


    // method to print the card and its info

    public ArrayList<String> showInfoCard(PlayableCard card){

        //create attribute for the info
        String[] cardInfo= new String[8];
        // its useful later
        cardInfo[3]= "Required Resources: ";


        // state if it's a golden card or a resource card ant the required resource
        if (card instanceof GoldenCard){
            cardInfo[0]= "Golden Card";
            cardInfo[6]=ANSI_YELLOW;
            for (int i=0; i<((GoldenCard) card).getRequiredResource().size(); i++){
                cardInfo[3]= cardInfo[3]+Views.resourceToEmoji(((GoldenCard) card).getRequiredResource().get(i))+" ";
            }

        }
        else {cardInfo[0]= "Resource Card";
            cardInfo[3]= cardInfo[3]+" NONE";
            cardInfo[6]= ANSI_WHITE;
        }

        // set the color of the card based on the permanent resource
        switch (card.getPermResource()[0]){

            case WOLF -> cardInfo[7]=ANSI_BLUE_BACKGROUND;
            case LEAF -> cardInfo[7]=ANSI_GREEN_BACKGROUND;
            case MUSHROOM -> cardInfo[7]=ANSI_RED_BACKGROUND;
            case BUTTERFLY -> cardInfo[7]=ANSI_PURPLE_BACKGROUND;

        }

        // set the info of the card like points, rule to obtain those points, permanent resource and corners
        cardInfo[1]= "Points: "+((GoldenCard) card).getPoints();
        cardInfo[4]= "Rule: "+((GoldenCard) card).getRule().toString();
        cardInfo[2]= "Permanent Resource: "+Views.resourceToEmoji(((GoldenCard) card).getPermResource()[0]);
        cardInfo[5]= "Corners: ";
        for (int i=0; i<4; i++){
            if (((GoldenCard) card).getCardCorners()[i]==null){cardInfo[5]= cardInfo[5]+"EMPTY ";
            }
            else {cardInfo[5]= cardInfo[5]+i+": "+Views.cornerToString(card.getCardCorners()[i])+"  ";
            }
        };




        // actively creating the array to print
        ArrayList<String> printedCard= new ArrayList<String>();
        printedCard.add("   "+cardInfo[7]+cardInfo[6]+"╔══════════════════════════════════╗");
        String t= "   "+cardInfo[7]+cardInfo[6]+"║ "+ANSI_RESET+Views.cornerToString(card.getCardCorners()[0])+"            "+Views.cardToPoint(card)+"            "+Views.cornerToString(card.getCardCorners()[1])+cardInfo[7]+cardInfo[6]+"║";
        printedCard.add(t);
        for(int x=0;x<5;x++) {
            printedCard.add("   " + cardInfo[7] + cardInfo[6] + "║" + ANSI_RESET + "                                  " + cardInfo[7] + cardInfo[6] + "║"+cardInfo[x]);
        };
        String b= "   "+cardInfo[7]+cardInfo[6]+"║ "+ANSI_RESET+Views.cornerToString(card.getCardCorners()[2])+"            "+Views.cardToRequiredResource(card)+"            "+Views.cornerToString(card.getCardCorners()[3])+cardInfo[7]+cardInfo[6]+"║";
        printedCard.add(b);
        printedCard.add("   "+cardInfo[7]+cardInfo[6]+"╚══════════════════════════════════╝");



        //clear the screen before printing something
        Views.clearScreen();
        //print the array
        for(int i = 0; i < printedCard.size(); i++) {
            System.out.print(printedCard.get(i));
        }



        // return the array in case of future changes
        return printedCard;
    };













}
