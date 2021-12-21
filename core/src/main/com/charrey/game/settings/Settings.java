package com.charrey.game.settings;

import com.charrey.game.model.BlockType;
import com.charrey.game.model.Direction;

import static com.charrey.game.settings.ExecutionType.SERIAL;

/**
 * Monitors global state of the program and stores user-set parameters
 */
public class Settings {

    private Settings() {}

    /**
     * How many simulation per second the simulator should aim to achieve
     */
    public static double requestedSimulationsPerSecond = 1d;
    /**
     * How many simulation per second the simulator has achieved
     */
    public static Long actualSimulationsPerSecond = null;
    /**
     * How the simulator should compute the semantic step of the simulation
     */
    public static ExecutionType simulationStep = SERIAL;
    /**
     * How the simulator should compute the state switch step of the simulation
     */
    public static ExecutionType stateSwitchStep = SERIAL;
    /**
     * Type of block that should be placed if the user clicks on the game field while no simulation is running.
     */
    public static BlockType newBlockType = null;
    /**
     * Direction of block that should be placed if the user clicks on the game field while no simulation is running.
     */
    public static Direction newBlockDirection = null;
    /**
     * Whether a simulation is currently taking place
     */
    public static boolean currentlySimulating = false;
}
