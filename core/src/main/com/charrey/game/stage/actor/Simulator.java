package com.charrey.game.stage.actor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;

public class Simulator {

    private final GameFieldBlock[][] columns;
    private final Lock readLock;
    private final Set<GameFieldBlock> canAct;
    private Thread simulatorThread;

    public Simulator(GameFieldBlock[][] columns, Set<GameFieldBlock> canAct, Lock readLock) {
        this.columns = columns;
        this.canAct = canAct;
        this.readLock = readLock;
    }


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
                            GameField.forEachBlock(columns, block -> block.getSimulation().step());
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



    public void stop() throws InterruptedException {
        simulatorThread.interrupt();
        simulatorThread.join();
    }
}
