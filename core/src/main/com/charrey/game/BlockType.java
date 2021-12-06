package com.charrey.game;

/**
 * Enum that lists different block types that have different interactions.
 */
public enum BlockType {
    /**
     * Block that prevents other blocks from moving through it.
     */
    BARRIER,

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
    RANDOM_EXPLORER
}
