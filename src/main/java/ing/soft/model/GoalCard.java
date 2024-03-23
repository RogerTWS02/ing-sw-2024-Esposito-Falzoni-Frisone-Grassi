package ing.soft.model;

public abstract class GoalCard {
    private int points;

    public GoalCard(int points) {
        this.points = points;
    }

    public int getPoints(){
        return points;
   }
}
