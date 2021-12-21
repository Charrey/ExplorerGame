package com.charrey.game.model;

import com.charrey.game.model.condition.False;
import com.charrey.game.model.simulatable.*;
import com.charrey.game.util.GridItem;

/**
 * Enum that lists different block types that have different interactions.
 */
public enum BlockType {
    /**
     * Block that prevents other blocks from moving through it.
     */
    BARRIER,

    /**
     * Block that prevents other blocks from moving through it unless a condition is met.
     */
    CONDITIONAL_BARRIER,

    /**
     * Block that moves in a specific direction at each simulation step. When it encounters a barrier, it
     * disappears and (if not blocked by a barrier) spawns two split explorers to its left and right. This block never
     * reverses.
     */
    SPLIT_EXPLORER,
    /**
     * Block that moves in a specific direction at each simulation step. When it encounters a barrier, it chooses a random
     * non-blocked direction to go in (except for the direction it came from). If no such direction exists, it disappears.
     */
    RANDOM_EXPLORER;

    /**
     * Creates a simulatable of this type
     * @param direction direction that this simulatable faces (or null if direction is not applicable)
     * @param location location of the simulatable in the grid it will be added to
     * @return a simulatable
     */
    public Simulatable getSimple(Direction direction, GridItem location) {
        return switch (this) {
            case BARRIER -> new DefaultBarrier(location);
            case CONDITIONAL_BARRIER -> new ConditionalBarrier(location, new False());
            case SPLIT_EXPLORER -> new SplitExplorer(direction, location);
            case RANDOM_EXPLORER -> new RandomExplorer(direction, location);
        };
    }
}
