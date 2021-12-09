package com.charrey.game.simulator;

import com.charrey.game.stage.actor.GameFieldBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Performs the simulation step of the simulator in series (using a single computation thread)
 */
public class SerialSemanticSimulationStep implements SemanticSimulationStep {
    @Override
    public void executeOneStep(@NotNull Set<GameFieldBlock> actingWaitList) {
        for (GameFieldBlock block : actingWaitList) {
            block.getSimulation().simulateStep();
        }
    }
}
