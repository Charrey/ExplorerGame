package com.charrey.game.simulator;

import com.charrey.game.stage.actor.GameField;
import org.jetbrains.annotations.NotNull;

/**
 * This class provides a method to obtain a simulator for the specification in a game field.
 */
public class SimulatorFactory {

    private SimulatorFactory() {}

    /**
     * Returns a simulator for a specified game field
     * @param gamefield game field
     * @return a simulator
     */
    public static @NotNull Simulator get(@NotNull GameField gamefield) {
        Simulator res = new Simulator();
        res.canAct = gamefield.getToBeSimulatedNextStep();
        res.haveChanged = gamefield.getHaveChangedSinceLastStep();
        res.simsPerSecond = gamefield.getSimsPerSecond();
        res.semanticSimulationStep = switch (gamefield.getSimulatorSettings().simulationType()) {
            case SERIAL -> new SerialSemanticSimulationStep();
            case PARALLEL -> new ParallelSemanticSimulationStep();
        };
        res.stateSwitchSimulationStep = switch (gamefield.getSimulatorSettings().stepType()) {
            case SERIAL -> new SerialStateSwitchSimulationStep();
            case PARALLEL -> new ParallelStateSwitchSimulationStep();
        };
        return res;
    }
}
