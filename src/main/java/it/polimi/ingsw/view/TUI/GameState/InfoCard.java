package it.polimi.ingsw.view.TUI.GameState;

import it.polimi.ingsw.model.Corner;
import it.polimi.ingsw.model.PlayableCard;

public class InfoCard {

    public String getInfoCard(PlayableCard card){
        String infoCard=("""
                
                
                
                
                
                
                
                
                
                
                
                
                """);

        return infoCard;
    };









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
    private String cardToPoint(PlayableCard card){
        return switch (card.getPoints()){
            case 0 -> "   ";
            default -> card.getPoints()+" p";
        };



}
