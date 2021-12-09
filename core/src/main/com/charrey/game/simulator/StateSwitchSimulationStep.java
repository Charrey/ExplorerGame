package com.charrey.game.simulator;

import com.charrey.game.stage.actor.GameFieldBlock;

import java.util.Set;

/**
 * Interface used by the simulator to set the next state of simulated blocks to the current state. This is an interface
 * to allow flexibility on how this is done (e.g. serially or parallely).
 */
public interface StateSwitchSimulationStep {

    /**
     * Sets each block's next state to be the current state
     * @param haveChanged set of blocks that have their 'next' state changed
     */
    void nextStep(Set<GameFieldBlock> haveChanged);
}
