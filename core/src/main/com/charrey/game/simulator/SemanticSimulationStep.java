package com.charrey.game.simulator;

import com.charrey.game.stage.actor.GameFieldBlock;

import java.util.Set;

/**
 * This interface provides flexibility on how simulation is computed (for example, serially or parallel). This is
 * used by the Simulator to perform semantic simulation of blocks in queue to be simulated.
 */
public interface SemanticSimulationStep {

    /**
     * Simulates a set of block according to their semantics and sets the result in the next state
     * @param actingWaitList blocks that need to be simulated
     */
    void executeOneStep(Set<GameFieldBlock> actingWaitList);
}
