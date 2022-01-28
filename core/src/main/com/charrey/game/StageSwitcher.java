package com.charrey.game;


import com.charrey.game.stage.ExploreStage;

/**
 * Interface that provides the possibility to switch which stage is presented in the window.
 */
public interface StageSwitcher {
    /**
     * Sets the provided stage as visible in the window.
     *
     * @param stage the stage
     */
    void changeToStage(ExploreStage stage);
}
