package com.charrey.game.simulator;

import com.charrey.game.stage.actor.GameFieldBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Set;
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
    public void nextStep(@NotNull Set<GameFieldBlock> haveChanged) {
        parallelExecutionLock.lock();
        AtomicInteger remaining = new AtomicInteger(haveChanged.size());
        Condition done = parallelExecutionLock.newCondition();
        while (!haveChanged.isEmpty()) {
            Iterator<GameFieldBlock> iterator = haveChanged.iterator();
            GameFieldBlock block = iterator.next();
            iterator.remove();
            concurrentExecutor.execute(() -> {
                block.getSimulation().step();
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
