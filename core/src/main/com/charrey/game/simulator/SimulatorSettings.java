package com.charrey.game.simulator;

/**
 * This record contains settings on how a specification should be simulated.
 */
public record SimulatorSettings(ExecutionType simulationType, ExecutionType stepType) {

    /**
     * Types of simulation in terms of thread usage
     */
    public enum ExecutionType {
        /**
         * Using a single thread
         */
        SERIAL,
        /**
         * Using many threads
         */
        PARALLEL
    }
}
