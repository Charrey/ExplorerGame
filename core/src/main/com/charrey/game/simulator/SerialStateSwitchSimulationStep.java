package com.charrey.game.simulator;

import com.charrey.game.stage.actor.GameFieldBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * This class performs the switch from current state to next state serially (in one thread).
 */
public class SerialStateSwitchSimulationStep implements StateSwitchSimulationStep {
    @Override
    public void nextStep(@NotNull Set<GameFieldBlock> haveChanged) {
        for (GameFieldBlock block : haveChanged) {
            block.getSimulation().step();
        }
        haveChanged.clear();
    }
}
