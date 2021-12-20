package com.charrey.game.simulator;

import com.charrey.game.model.Grid;
import com.charrey.game.model.Simulatable;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class performs the switch from current state to next state parallelly.
 */
public class ParallelStateSwitchSimulationStep implements StateSwitchSimulationStep {

    private final Lock parallelExecutionLock = new ReentrantLock();
    private final ThreadPoolExecutor concurrentExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();


    @Override
    public void nextStep(Grid grid) {
        parallelExecutionLock.lock();
        AtomicInteger remaining = new AtomicInteger(grid.getSimulatables().size());
        Condition done = parallelExecutionLock.newCondition();
        for (Simulatable simulatable : new HashSet<>(grid.getSimulatables())) {
            concurrentExecutor.execute(() -> {
                simulatable.stateSwitchStep();
                if (remaining.decrementAndGet() == 0) {
                    parallelExecutionLock.lock();
                    done.signal();
                    parallelExecutionLock.unlock();
                }
            });
        }
        while (remaining.intValue() != 0) {
            done.awaitUninterruptibly();
        }
        parallelExecutionLock.unlock();
    }
}
