package com.charrey.game.simulator;

import com.charrey.game.model.Grid;
import com.charrey.game.model.Simulatable;

import java.util.HashSet;

/**
 * This class performs the switch from current state to next state serially (in one thread).
 */
public class SerialStateSwitchSimulationStep implements StateSwitchSimulationStep {

    @Override
    public void nextStep(Grid grid) {
        new HashSet<>(grid.getSimulatables()).forEach(Simulatable::stateSwitchStep);
        grid.updateMapAndDeduplicate();
    }
}
