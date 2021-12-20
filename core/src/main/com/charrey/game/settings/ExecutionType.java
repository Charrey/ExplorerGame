package com.charrey.game.settings;

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