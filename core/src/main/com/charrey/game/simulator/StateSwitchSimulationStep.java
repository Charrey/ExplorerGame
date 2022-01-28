package com.charrey.game.simulator;

import com.charrey.game.model.simulatable.Simulatable;

import java.util.Set;

/**
 * Interface used by the simulator to set the next state of simulated blocks to the current state. This is an interface
 * to allow flexibility on how this is done (e.g. serially or parallely).
 */
public interface StateSwitchSimulationStep {

    /**
     * Sets the current step of a model to be equal to the computed step.
     *
     * @param simulatables simulatables being simulated
     */
    void nextStep(Set<Simulatable> simulatables);
}
