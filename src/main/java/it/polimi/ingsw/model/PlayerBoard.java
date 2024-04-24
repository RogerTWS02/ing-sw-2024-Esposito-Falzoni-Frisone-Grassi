package it.polimi.ingsw.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.polimi.ingsw.model.State.*;

public class PlayerBoard implements Serializable{
    protected PlayableCard[][] grid = new PlayableCard[81][81];
    public Pawn pawn;

    private boolean firstcard = true;

    public PlayerBoard(Pawn pawn) {
        this.pawn = pawn;
    }

    public State getState(int x, int y){
        /* If the cell is empty then the player hasn't placed a card there yet */
        if(grid[x][y] == null) return UNPLAYED;

        return grid[x][y].getState();
    }

    /* When I place a card on the board I have to update the state of the corresponding cell
     * and also the state of the corners of the neighbouring cards that get covered */
    public void placeCard(PlayableCard card, int x, int y){

        if(firstcard){
            grid[x][y] = card;
            /* updates the state of the card when placed */
            grid[x][y].setState(OCCUPIED);


        /*
        grid[x-1][y-1] =>  corner 0     grid[x-1][y+1] =>  corner 2
        grid[x+1][y-1] =>  corner 1     grid[x+1][y+1] =>  corner 3
        */
            int id = -1;
            for(int i = -1; i < 3; i += 2){
                for(int j = -1; j < 3; j += 2){
                    id++;
                    try{
                        /* if the neighbouring cell is empty, it needs to be instantiated to a dummy
                         * ResourceCard to update the state of the PlayerBoard */
                        if(grid[x+i][y+j] == null){
                            grid[x+i][y+j] = new ResourceCard(
                                    new Resource[]{},
                                    new Corner[]{},
                                    0,
                                    null
                            );

                            /* if the neighbouring cell is empty then its state needs to be
                             * changed according to the presence of the corresponding corner */
                            if(grid[x][y].getCardCorners()[id] == null){
                                grid[x+i][y+j].setState(UNAVAILABLE);
                            }else{
                                grid[x+i][y+j].setState(AVAILABLE);
                            }
                            continue;
                        }
                        /* if the neighbouring corner is present then it has to be set covered */
                        grid[x+i][y+j].getCardCorners()[id].setCovered(true);

                    }catch(ArrayIndexOutOfBoundsException ignored){}
                }
            }

            firstcard = false;

        }else{

            if(grid[x][y] == null || grid[x][y].getState() == UNAVAILABLE || grid[x][y].getState() == OCCUPIED){
                throw new IllegalArgumentException("Unavailable Position!!!");
            }

            grid[x][y] = card;
            /* updates the state of the card when placed */
            grid[x][y].setState(OCCUPIED);


        /*
        grid[x-1][y-1] =>  corner 0     grid[x-1][y+1] =>  corner 2
        grid[x+1][y-1] =>  corner 1     grid[x+1][y+1] =>  corner 3
        */
            int id = -1;
            for(int i = -1; i < 3; i += 2){
                for(int j = -1; j < 3; j += 2){
                    id++;
                    try{
                        /* if the neighbouring cell is empty, it needs to be instantiated to a dummy
                         * ResourceCard to update the state of the PlayerBoard */
                        if(grid[x+i][y+j] == null){
                            grid[x+i][y+j] = new ResourceCard(
                                    new Resource[]{},
                                    new Corner[]{},
                                    0,
                                    null
                            );

                            /* if the neighbouring cell is empty then its state needs to be
                             * changed according to the presence of the corresponding corner */
                            if(grid[x][y].getCardCorners()[id] == null){
                                grid[x+i][y+j].setState(UNAVAILABLE);
                            }else{
                                grid[x+i][y+j].setState(AVAILABLE);
                            }
                            continue;
                        }
                        /* if the neighbouring corner is present then it has to be set covered */
                        grid[x+i][y+j].getCardCorners()[id].setCovered(true);

                    }catch(ArrayIndexOutOfBoundsException ignored){}
                }
            }
        }
    }

    public PlayableCard getCard(int x, int y){
        return grid[x][y];
    }

    /* Returns all the viewable resources present on the board*/
    public List<Resource> getResources(){
        List<Resource> res = new ArrayList<>();
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
}
