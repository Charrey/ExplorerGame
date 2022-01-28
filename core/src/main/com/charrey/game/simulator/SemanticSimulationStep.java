package com.charrey.game.simulator;


import com.charrey.game.model.simulatable.Simulatable;

import java.util.Set;

/**
 * This interface provides flexibility on how simulation is computed (for example, serially or parallel). This is
 * used by the Simulator to perform semantic simulation of blocks in queue to be simulated.
 */
public interface SemanticSimulationStep {

    /**
     * Simulates a model according to its semantics and sets the result in the next state
     *
     * @param simulatables simulatables that need to be simulated
     */
    void executeOneStep(Set<Simulatable> simulatables);
}
