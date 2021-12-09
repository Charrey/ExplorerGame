package com.charrey.game.simulator;

import com.charrey.game.stage.actor.GameFieldBlock;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Class that simulates the specified game field
 */
public class Simulator {

    final Lock readLock = new ReentrantLock();
    Set<GameFieldBlock> canAct;
    Set<GameFieldBlock> haveChanged;
    Supplier<Long> simsPerSecond;
    private Thread simulatorThread;


    Simulator() {}

    private long lastLog = 0;
    private long stepCount = 0;
    private double average = 0;
    private double sampleCount = 0;

    private static final int CORRECTION_SAMPLES = 100;

    SemanticSimulationStep semanticSimulationStep;
    StateSwitchSimulationStep stateSwitchSimulationStep;


    /**
     * Start simulating (concurrently)
     */
    public void start() {
        simulatorThread = new Thread(() -> {
            stepCount = 0;
            average = 0;
            sampleCount = 0;
            Queue<Long> lastStartTimes = new LinkedList<>();
            long simsPerSecondCache = simsPerSecond.get();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    long start = startClock(lastStartTimes);
                    Set<GameFieldBlock> actingWaitList = new HashSet<>(canAct);
                    if (readLock.tryLock(1, TimeUnit.MILLISECONDS)) {
                        canAct.clear();
                        semanticSimulationStep.executeOneStep(actingWaitList);
                        stateSwitchSimulationStep.nextStep(haveChanged);
                        readLock.unlock();
                        stepCount++;
                        if (System.currentTimeMillis() - lastLog > 1000) {
                            lastLog = System.currentTimeMillis();
                            average = ((average * sampleCount) + stepCount) / (sampleCount + 1);
                            sampleCount++;
                            Logger.getLogger(getClass().getName()).fine( () -> "Simulating at " + stepCount + " steps per second (average = " + average + ").");
                            stepCount = 0;
                        }
                    }
                    simsPerSecondCache = stopClock(lastStartTimes, simsPerSecondCache, start);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        simulatorThread.start();
    }

    private long stopClock(@NotNull Queue<Long> lastStartTimes, long lastRequestedSpeed, long start) throws InterruptedException {
        if (lastRequestedSpeed != simsPerSecond.get()) {
            sampleCount = 0;
            average = 0;
            stepCount = 0;
            lastRequestedSpeed = simsPerSecond.get();
            lastStartTimes.clear();
        }
        long end = System.nanoTime();
        if (lastStartTimes.size() < CORRECTION_SAMPLES) {
            sleepBasedOnSpentTime(end - start);
        } else {
            sleepBasedOnLastSamples(lastStartTimes.peek());
        }
        return lastRequestedSpeed;
    }

    private long startClock(@NotNull Queue<Long> lastStartTimes) {
        long start = System.nanoTime();
        lastStartTimes.add(start);
        while (lastStartTimes.size() > CORRECTION_SAMPLES) {
            lastStartTimes.poll();
        }
        return start;
    }

    private void sleepBasedOnSpentTime(long duration) throws InterruptedException {
        double shouldSleep = ((1000000000d / simsPerSecond.get()) - duration);
        if (shouldSleep > 0) {
            long millis = (long) (shouldSleep / 1000000);
            int nanos = Math.floorMod((long) shouldSleep, 1000000);
            Thread.sleep(millis, nanos);
        }
    }

    private void sleepBasedOnLastSamples(long startOfEarliestSample) throws InterruptedException {
        double expectedDurationNanos = CORRECTION_SAMPLES *  (1000000000d / simsPerSecond.get());
        double actualDurationNanos = System.nanoTime() - (double) startOfEarliestSample;
        double shouldSleep = expectedDurationNanos - actualDurationNanos;
        if (shouldSleep > 0) {
            long millis = (long) (shouldSleep / 1000000);
            int nanos = Math.floorMod((long) shouldSleep, 1000000);
            Thread.sleep(millis, nanos);
        }
    }


    /**
     * Stop simulating and wait for the simulation thread to stop
     * @throws InterruptedException thrown when the wait is interrupted
     */
    public void stop() throws InterruptedException {
        simulatorThread.interrupt();
        simulatorThread.join();
    }

    /**
     * Returns a lock that must be held when reading the gamefield's contents to avoid
     * concurrency problems.
     * @return the lock
     */
    public @NotNull Lock getReadLock() {
        return readLock;
    }
}
