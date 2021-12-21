package com.charrey.game.model.condition;

import com.charrey.game.model.Grid;
import com.charrey.game.util.GridItem;

/**
 * Condition that is true if and only if at least one blocks is currently at a specified position
 */
public class BlockExists extends ConditionWithReference {

    /**
     * Creates a new BlockExists condition
     * @param grid model
     * @param location location in the model where no block should exist
     */
    public BlockExists(Grid grid, GridItem location) {
        super(grid, location);
    }


    @Override
    public boolean test() {
        return getGrid().getAtStrictGridLocation(getLocation()).size() > 0;
    }
}
