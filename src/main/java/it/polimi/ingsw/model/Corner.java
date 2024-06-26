package it.polimi.ingsw.model;

import java.util.Optional;

/**
 * The class Corner represents the corners of the cards.
 */
public class Corner {

    /**
     * The id of the corner, which identifies its position in the card.
     */
    private final int id;

    /**
     * The covering status of the corner.
     */
    private boolean isCovered = false;

    /**
     * The resource that the corner provides.
     */
    private final Optional<Resource> cornerResource;

    /**
     * The constructor creates a corner with the given id, reference card and corner resource.
     *
     * @param id It identifies the position of the corner in the card.
     *
     * @param cornerResource It is the resource that the corner provides.
     */
    public Corner(int id, Optional<Resource> cornerResource) {
        this.id = id;
        this.cornerResource = cornerResource;
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
