package com.charrey.game.stage.actor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;

/**
 * Class that simulates the specified game field
 */
public class Simulator {

    private final GameField gameField;
    private final Lock readLock;
    private final Set<GameFieldBlock> canAct;
    private Thread simulatorThread;

    /**
     * Creates a new Simulator
     * @param gameField game field to simulate
     * @param canAct mutable set that is continuously modified to contain the set of blocks that require simulation.
     * @param readLock lock object restricting read access to the current simulated contents
     */
    public Simulator(GameField gameField, Set<GameFieldBlock> canAct, Lock readLock) {
        this.gameField = gameField;
        this.canAct = canAct;
        this.readLock = readLock;
    }

    /**
     * Start simulating (concurrently)
     */
    public void start() {
        simulatorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    Set<GameFieldBlock> actingWaitList = new HashSet<>(canAct);
                    Logger.getLogger(getClass().getName()).fine(() -> "Processing wait list " + actingWaitList.stream().map(GameFieldBlock::getName).reduce("", (s, s2) -> s + " " + s2, (s, s2) -> s + " " + s2));
                    try {
                        if (readLock.tryLock(1, TimeUnit.MILLISECONDS)) {
                            canAct.clear();
                            for (GameFieldBlock block : actingWaitList) {
                                block.getSimulation().simulateStep();
                            }
                            gameField.forEachBlock(block -> block.getSimulation().step());
                            readLock.unlock();
                        }
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });
        simulatorThread.start();
    }


    /**
     * Stop simulating and wait for the simulation thread to stop
     * @throws InterruptedException thrown when the wait is interrupted
     */
    public void stop() throws InterruptedException {
        simulatorThread.interrupt();
        simulatorThread.join();
    }
}
