package com.charrey.game.model.condition;

import com.charrey.game.model.Grid;
import com.charrey.game.util.GridItem;

/**
 * Condition that refers to a different location in the same model to determine whether it's true.
 */
public abstract class ConditionWithReference extends Condition {

    private final Grid grid;
    private final GridItem location;

    /**
     * Creates a new ConditionWithReference
     * @param grid the model
     * @param location location of the reference in the model
     */
    public ConditionWithReference(Grid grid, GridItem location) {
        this.grid = grid;
        this.location = location;
    }

    /**
     * Returns the location of the reference
     * @return the location
     */
    public GridItem getLocation() {
        return location;
    }

    /**
     * Returns the model that the reference is in
     * @return the model
     */
    protected Grid getGrid() {
        return grid;
    }
}
