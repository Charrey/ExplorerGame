package com.charrey.game.simulator;

import com.charrey.game.model.simulatable.Simulatable;

import java.util.HashSet;
import java.util.Set;

/**
 * Performs the simulation step of the simulator in series (using a single computation thread)
 */
public class SerialSemanticSimulationStep implements SemanticSimulationStep {

    @Override
    public void executeOneStep(Set<Simulatable> simulatables) {
        new HashSet<>(simulatables).forEach(Simulatable::simulateStep);
    }
}
