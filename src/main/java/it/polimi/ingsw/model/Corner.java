package it.polimi.ingsw.model;

import java.util.Optional;

/**
 * The class Corner represents the corners of the cards.
 */
public class Corner {
    private final int id;
    private final PlayableCard refCard;

    private boolean isCovered = false;

    private final Optional<Resource> cornerResource;

    /**
     * The constructor creates a corner with the given id, reference card and corner resource.
     *
     * @param id It identifies the position of the corner in the card.
     * @param refCard It is the card to which the corner belongs.
     * @param cornerResource It is the resource that the corner provides.
     */
    public Corner(int id, PlayableCard refCard, Optional<Resource> cornerResource) {
        this.id = id;
        this.refCard = refCard;
        this.cornerResource = cornerResource;
    }

    /**
     * Returns the id of the corner.
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the reference card of the corner.
     *
     * @return The card to which the corner belongs.
     */
    public PlayableCard getRefCard() {
        return refCard;
    }

    /**
     * Returns the resource that the corner provides.
     *
     * @return The resource that the corner provides.
     */
    public Optional<Resource> getCornerResource() {
        return cornerResource;
    }

    /**
     * Returns the covering status of the corner.
     *
     * @return True if the corner is covered, false otherwise.
     */
    public boolean isCovered() {
        return isCovered;
    }

    /**
     * Sets the covering status of the corner.
     *
     * @param covered True if the corner is covered, false otherwise.
     */
    public void setCovered(boolean covered) {
        isCovered = covered;
    }
}
