package com.charrey.game.model.simulatable;

/**
 * Enum to denote for a specific block and direction whether it's the edge of the SubGrid, and if so, what kind.
 */
public enum EdgeType {
    /**
     * No edge
     */
    EMPTY,
    /**
     * Exported edge
     */
    EXPORT,
    /**
     * Padded edge
     */
    PAD,
    /**
     * Edge not marked as exported or padded
     */
    UNMARKED
}
