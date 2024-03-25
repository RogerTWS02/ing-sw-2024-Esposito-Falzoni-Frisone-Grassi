package ing.soft.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ing.soft.model.State.*;

public class PlayerBoard {
    protected PlayableCard[][] grid = new PlayableCard[80][80];
    public Pawn pawn;

    public PlayerBoard(Pawn pawn) {
        this.pawn = pawn;
    }

    public State getState(int x, int y){
        /* If the cell is empty then the player hasn't placed a card there yet */
        if(grid[x][y] == null) return UNPLAYED;

        return grid[x][y].getState();
    }

    /* When I place a card on the board I have to update the state of the corresponding cell
    *  and also the state of the corners of the neighbouring cards that get covered*/
    public void placeCard(PlayableCard card, int x, int y){
        grid[x][y] = card;
        grid[x][y].setState(OCCUPIED);


        /*
        grid[x-1][y-1] =>  corner 0     grid[x-1][y+1] =>  corner 2
        grid[x+1][y-1] =>  corner 1     grid[x+1][y+1] =>  corner 3
        */
        for(int i = -1; i < 3; i += 2){
            for(int j = -1; j < 3; j += 2){
                int id = -1;
                id++;
                try{
                    if(grid[x+i][y+j] == null) continue;
                    grid[x+i][y+j].getCardCorners()[id].setCovered(true);
                }catch(ArrayIndexOutOfBoundsException ignored){}
            }
        }
    }

    public PlayableCard getCard(int x, int y){
        return grid[x][y];
    }

    /* Returns all the viewable resources present on the board*/
    public List<Resource> getResources(){
        List<Resource> res = new ArrayList<Resource>();
        for(int i = 0; i < 80; i++){
            for(int j = 0; j < 80; j++){
                /* skips the empty cells */
                if(grid[i][j] == null) continue;

                /* If the card is flipped, only the permRes are added otherwise
                *  all the resource on the not covered corners are returned*/
                if(grid[i][j].isFlipped()) {
                    res.addAll(Arrays.asList(grid[i][j].getPermResource()));
                }else{
                    for(Corner c: grid[i][j].getCardCorners()){
                        if(c.isCovered()) continue;
                        c.getCornerResource().ifPresent(res::add);
                    }
                }


            }
        }
        return res;
    }
}
