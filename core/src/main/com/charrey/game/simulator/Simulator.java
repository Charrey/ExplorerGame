package com.charrey.game.simulator;

import com.charrey.game.settings.Settings;
import com.charrey.game.model.Grid;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class that simulates the specified game field
 */
public class Simulator {

    private final Grid grid;
    private Thread simulatorThread;

    private long lastLog = 0;
    private long stepCount = 0;
    private double average = 0;
    private double sampleCount = 0;

    private final SemanticSimulationStep semanticStep;
    private final StateSwitchSimulationStep stateSwitchStep;

    private static final int CORRECTION_SAMPLES = 100;

    /**
     * Creates a new simulator that simulates the provided model
     * @param grid model to be simulated
     */
    public Simulator(Grid grid) {
        this.grid = grid;
        this.semanticStep = switch(Settings.simulationStep) {
            case SERIAL -> new SerialSemanticSimulationStep();
            case PARALLEL -> new ParallelSemanticSimulationStep();
        };
        this.stateSwitchStep = switch(Settings.stateSwitchStep) {
            case SERIAL -> new SerialStateSwitchSimulationStep();
            case PARALLEL -> new ParallelStateSwitchSimulationStep();
        };
    }

    private double simsPerSecondCache = Settings.requestedSimulationsPerSecond;
    private final List<Long> lastStartTimes = new LinkedList<>();

    /**
     * Starts the simulation
     */
    public void start() {
        simulatorThread = new Thread(() -> {
            stepCount = 0;
            average = 0;
            sampleCount = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    initializeIteration();
                    synchronized (grid) {
                        semanticStep.executeOneStep(grid);
                        stateSwitchStep.nextStep(grid);
                        grid.updateMapAndDeduplicate();
                    }
                    finalizeIteration();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        simulatorThread.start();
    }

    private void finalizeIteration() throws InterruptedException {
        if (System.currentTimeMillis() - lastLog > 1000) {
            lastLog = System.currentTimeMillis();
            average = ((average * sampleCount) + stepCount) / (sampleCount + 1);
            sampleCount++;
            Settings.actualSimulationsPerSecond = stepCount;
            Logger.getLogger(getClass().getName()).info( () -> "Simulating at " + stepCount + " steps per second (average = " + average + ").");
            stepCount = 0;
        }
        simsPerSecondCache = stopClock(simsPerSecondCache);
        stepCount++;
    }

    private void initializeIteration() {
        lastStartTimes.add(System.nanoTime());
        while (lastStartTimes.size() > CORRECTION_SAMPLES) {
            lastStartTimes.remove(0);
        }
    }


    private double stopClock(double lastRequestedSpeed) throws InterruptedException {
        long mostRecentStart = lastStartTimes.get(lastStartTimes.size() - 1);
        if (lastRequestedSpeed != Settings.requestedSimulationsPerSecond) {
            sampleCount = 0;
            average = 0;
            stepCount = 0;
            lastRequestedSpeed = Settings.requestedSimulationsPerSecond;
            lastStartTimes.clear();
        }
        long end = System.nanoTime();
        if (lastStartTimes.size() < CORRECTION_SAMPLES) {
            sleepBasedOnSpentTime(end - mostRecentStart);
        } else {
            sleepBasedOnLastSamples(lastStartTimes.get(0));
        }
        return lastRequestedSpeed;
    }

    private void sleepBasedOnSpentTime(long duration) throws InterruptedException {
        double shouldSleep = ((1000000000d / Settings.requestedSimulationsPerSecond) - duration);
        if (shouldSleep > 0) {
            long millis = (long) (shouldSleep / 1000000);
            int nanos = Math.floorMod((long) shouldSleep, 1000000);
            Thread.sleep(millis, nanos);
        }
    }

    private void sleepBasedOnLastSamples(long startOfEarliestSample) throws InterruptedException {
        double expectedDurationNanos = CORRECTION_SAMPLES *  (1000000000d / Settings.requestedSimulationsPerSecond);
        double actualDurationNanos = System.nanoTime() - (double) startOfEarliestSample;
        double shouldSleep = expectedDurationNanos - actualDurationNanos;
        if (shouldSleep > 0) {
            long millis = (long) (shouldSleep / 1000000);
            int nanos = Math.floorMod((long) shouldSleep, 1000000);
            Thread.sleep(millis, nanos);
        }
    }

    /**
     * Stops the simulation
     */
    public void stop() {
        try {
            if (simulatorThread != null) {
                simulatorThread.interrupt();
                simulatorThread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            Settings.actualSimulationsPerSecond = null;
        }
    }
}
