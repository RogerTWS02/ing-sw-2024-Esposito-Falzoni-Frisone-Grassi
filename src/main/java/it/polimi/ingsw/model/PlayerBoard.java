package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import static it.polimi.ingsw.model.State.*;

/**
 * This class represents the player's board in the game.
 */
public class PlayerBoard implements Serializable{
    protected PlayableCard[][] grid = new PlayableCard[81][81];
    public Pawn pawn;

    private boolean firstcard = true;

    /**
     * The constructor creates a player board, given the panw of the player.
     *
     * @param pawn The pawn of the player.
     */
    public PlayerBoard(Pawn pawn) {
        this.pawn = pawn;
    }

    /**
     * Checks if the player has a card in the given position and returns the state of the cell.
     *
     * @param x The X coordinate of the cell.
     * @param y The Y coordinate of the cell.
     * @return The state of the cell.
     */
    public State getState(int x, int y){
        /* If the cell is empty then the player hasn't placed a card there yet */
        if(grid[x][y] == null) return UNPLAYED;

        return grid[x][y].getState();
    }

    /**
     * Checks if the given card can be placed on the board, by checking the requirements of the card.
     *
     * @param card The card to be placed.
     * @return True if the card can be placed, false otherwise.
     */
    public boolean checkGoldenCardRequirements(GoldenCard card) {
        for (Resource resource : card.getRequiredResource()) {
            if (countNumberEqual(card.getRequiredResource(), resource) > countNumberEqual(this.getResources(), resource))
                return false;
        }
        return true;
    }

    /**
     * Returns the coordinates of the corners of the card in the given position.
     *
     * @param index The index of the corner of the card.
     * @return The coordinates of the corner of the card.
     */
    public int[] cornerCoordLookup(int index) {
        int coord[] = new int[2];
        switch (index) {
            //NOTA BENE: LA PRIMA SONO LE Y, LA SECONDA SONO LE X
            case 0:
                coord[0] = -1;
                coord[1] = -1;
                break;
            case 1:
                coord[0] = -1;
                coord[1] = 1;
                break;
            case 2:
                coord[0] = 1;
                coord[1] = -1;
                break;
            case 3:
                coord[0] = 1;
                coord[1] = 1;
                break;
        }
        return coord;
    }

    /**
     * Places a card on the board in the given position.
     *
     * @param card The card to place.
     * @param x The X coordinate of the cell.
     * @param y The Y coordinate of the cell.
     * @return The number of corners that get covered by the card.
     */
    public int placeCard(PlayableCard card, int x, int y) {
        //If the card is a GoldenCard, check if the player has the required resources
        if(card instanceof GoldenCard){
            if(!checkGoldenCardRequirements((GoldenCard) card)){
                throw new IllegalArgumentException("You don't have the required resources to place this card!");
            }
        }

        //Place the card
        grid[x][y] = card;
        grid[x][y].setState(State.OCCUPIED);

        int coveredCorners = 0;

        //PER DEBUGGING
        /*
        if(card instanceof  StartingCard && card.isFlipped()){
            System.out.println(card.getUUID());
            for(Corner r: ((StartingCard) card).getBackCardCorners()){
                System.out.println(r.getCornerResource().isPresent());
            }
        }
        */

        //Update the state of the neighbouring cells
        for(int i = 0; i < 4; i++) {
            int[] tempCoord = cornerCoordLookup(i);
            if(grid[x + tempCoord[0]][y + tempCoord[1]] == null) {
                grid[x + tempCoord[0]][y + tempCoord[1]] = new ResourceCard(new Resource[]{}, new Corner[]{}, 0, null);

                //se l'angolo della carta che sto piazzando non è nullo
                if(card.getCardCorners()[i] != null) {
                    grid[x + tempCoord[0]][y + tempCoord[1]].setState(AVAILABLE);
                } else {
                    grid[x + tempCoord[0]][y + tempCoord[1]].setState(UNAVAILABLE);
                }

            } else {
                grid[x + tempCoord[0]][y + tempCoord[1]].getCardCorners()[Math.abs(i - 3)].setCovered(true);
                grid[x + tempCoord[0]][y + tempCoord[1]].setState(State.OCCUPIED);
                coveredCorners++;
            }
        }

        return coveredCorners;
    }

    /**
     * Returns the card in the given position.
     *
     * @param x The X coordinate of the cell.
     * @param y The Y coordinate of the cell.
     * @return The card in the given position.
     */
    public PlayableCard getCard(int x, int y){
        return grid[x][y];
    }

    /**
     * Returns all the viewable resources present on the board.
     *
     * @return All the viewable resources present on the board.
     */
    public ArrayList<Resource> getResources(){
        ArrayList<Resource> res = new ArrayList<>();
        for(int i = 0; i < 81; i++){
            for(int j = 80; j >=0; j--){
                /* skips the empty cells */
                if(grid[i][j] == null || grid[i][j].getState() == AVAILABLE || grid[i][j].getState() == UNAVAILABLE) continue;

                /* if the card is flipped, only the permRes are added otherwise
                 * all the resource on the not covered corners are returned */
                if(grid[i][j].isFlipped()) {
                    res.addAll(Arrays.asList(grid[i][j].getPermResource()));
                }else{
                    for(Corner c: grid[i][j].getCardCorners()){
                        if(c == null || c.isCovered()) continue;
                        c.getCornerResource().ifPresent(res::add);
                    }
                }
            }
        }
        return res;
    }

    /**
     * Checks the number of times a resource is present in a list.
     *
     * @param itemList The list of resources.
     * @param itemToCheck The resource to check.
     * @return The number of times the resource is present in the list.
     */
    private int countNumberEqual(ArrayList<Resource> itemList, Resource itemToCheck) {
        int count = 0;
        for (Resource i : itemList) {
            if (i.equals(itemToCheck)) {
                count++;
            }
        }
        return count;
    }
}
