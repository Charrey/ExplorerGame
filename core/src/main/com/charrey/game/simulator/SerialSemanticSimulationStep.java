package com.charrey.game.simulator;

import com.charrey.game.model.Grid;
import com.charrey.game.model.simulatable.Simulatable;

import java.util.HashSet;

/**
 * Performs the simulation step of the simulator in series (using a single computation thread)
 */
public class SerialSemanticSimulationStep implements SemanticSimulationStep {

    @Override
    public void executeOneStep(Grid grid) {
        new HashSet<>(grid.getSimulatables()).forEach(Simulatable::simulateStep);
    }
}
