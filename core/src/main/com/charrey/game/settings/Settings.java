package com.charrey.game.settings;

import com.charrey.game.model.Direction;
import com.charrey.game.model.simulatable.Simulatable;

import static com.charrey.game.settings.ExecutionType.SERIAL;

/**
 * Monitors global state of the program and stores user-set parameters
 */
public class Settings {

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
    public static NewBlockFactory<? extends Simulatable> newBlockFactory = null;
    /**
     * Direction of block that should be placed if the user clicks on the game field while no simulation is running.
     */
    public static Direction newBlockDirection = Direction.UP;
    /**
     * Whether a simulation is currently taking place
     */
    public static boolean currentlySimulating = false;
    /**
     * Default number of cells in a single row of the grid
     */
    public static int defaultWidth = 8;
    /**
     * Default number of cells in a single column of the grid
     */
    public static int defaultHeight = 8;
    private Settings() {
    }
}
