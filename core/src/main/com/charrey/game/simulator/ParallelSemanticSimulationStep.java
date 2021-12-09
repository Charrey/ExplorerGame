package com.charrey.game.simulator;

import com.charrey.game.stage.actor.GameFieldBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Performs the simulation step of the simulator in parallel (using many computation threads)
 */
public class ParallelSemanticSimulationStep implements SemanticSimulationStep {

    private final Lock parallelExecutionLock = new ReentrantLock();
    private final ThreadPoolExecutor concurrentExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();


    @Override
    public void executeOneStep(@NotNull Set<GameFieldBlock> actingWaitList) {
        parallelExecutionLock.lock();
        AtomicInteger remaining = new AtomicInteger(actingWaitList.size());
        Condition done = parallelExecutionLock.newCondition();
        for (GameFieldBlock block : actingWaitList) {
            concurrentExecutor.execute(() -> {
                block.getSimulation().simulateStep();
                if (remaining.decrementAndGet() == 0) {
                    parallelExecutionLock.lock();
                    done.signal();
                    parallelExecutionLock.unlock();
                }
            });
        }
        while (remaining.intValue() > 0) {
            done.awaitUninterruptibly();
        }
        parallelExecutionLock.unlock();
    }
}
