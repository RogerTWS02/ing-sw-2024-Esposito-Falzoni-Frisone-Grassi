package ing.soft.model;

import java.util.Optional;

public class Corner {
    private final int id;
    private final PlayableCard refCard;

    private boolean isCovered = false;

    private final Optional<Resource> cornerResource;


    public Corner(int id, PlayableCard refCard, Optional<Resource> cornerResource) {
        this.id = id;
        this.refCard = refCard;
        this.cornerResource = cornerResource;
    }

    public int getId() {
        return id;
    }


    public PlayableCard getRefCard() {
        return refCard;
    }


    public Optional<Resource> getCornerResource() {
        return cornerResource;
    }

    public boolean isCovered() {
        return isCovered;
    }

    public void setCovered(boolean covered) {
        isCovered = covered;
    }
}
