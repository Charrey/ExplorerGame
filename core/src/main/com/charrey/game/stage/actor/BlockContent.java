package com.charrey.game.stage.actor;

import com.charrey.game.BlockType;

/**
 * Provides information which block type is visibile on a block that might have multiple entities at once.
 */
public interface BlockContent {

    /**
     * Returns which block type should be rendered on this block.
     * @return which block type should be rendered on this block.
     */
    BlockType getVisibleBlockType();
}
