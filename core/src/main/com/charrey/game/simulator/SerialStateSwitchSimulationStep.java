package com.charrey.game.simulator;

import com.charrey.game.model.simulatable.Simulatable;

import java.util.HashSet;
import java.util.Set;

/**
 * This class performs the switch from current state to next state serially (in one thread).
 */
public class SerialStateSwitchSimulationStep implements StateSwitchSimulationStep {

    @Override
    public void nextStep(Set<Simulatable> simulatables) {
        new HashSet<>(simulatables).forEach(Simulatable::stateSwitchStep);
    }
}
