package com.charrey.game.simulator;

import com.charrey.game.model.Grid;

/**
 * Interface used by the simulator to set the next state of simulated blocks to the current state. This is an interface
 * to allow flexibility on how this is done (e.g. serially or parallely).
 */
public interface StateSwitchSimulationStep {

    /**
     * Sets the current step of a model to be equal to the computed step.
     * @param grid model being simulated
     */
    void nextStep(Grid grid);
}
