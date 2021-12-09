package com.charrey.game.stage.actor;

import com.charrey.game.model.ModelEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Provides information which block type is visibile on a block that might have multiple entities at once.
 */
public interface BlockContent {

    /**
     * Returns which entity should be rendered on this block.
     * @return which entity should be rendered on this block.
     */
    @Nullable ModelEntity getVisibleEntity();

}
