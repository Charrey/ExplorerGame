package com.charrey.game.stage.actor;

import com.charrey.game.BlockType;
import com.charrey.game.Direction;
import com.charrey.game.model.ModelEntity;
import com.charrey.game.simulator.SimulatorSettings;
import com.charrey.game.util.Pair;
import com.charrey.game.util.random.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Class that keeps track of the simulated content of a single gamefieldblock.
 */
public class SimulatedBlockContent implements BlockContent {

    private final Runnable registerForSimulation;
    private final Runnable registerChanged;

    @NotNull
    private final SortedSet<ModelEntity> invariant = new TreeSet<>();
    @NotNull
    private SortedSet<ModelEntity> currentState;
    @NotNull
    private SortedSet<ModelEntity> nextState;


    SimulatedBlockContent left;
    SimulatedBlockContent right;
    SimulatedBlockContent up;
    SimulatedBlockContent down;


    /**
     * Creates a new SimulatedBlockContent.
     * @param registerForSimulation Called when this block should be simulated in the next step.
     * @param registerChanged Called when this block is different in the next step
     * @param executionType The type of simulation (serial or parallel). Decides whether thread safety is implemented.
     */
    public SimulatedBlockContent(Runnable registerForSimulation, Runnable registerChanged, SimulatorSettings.ExecutionType executionType) {
        this.registerForSimulation = registerForSimulation;
        this.registerChanged = registerChanged;
        this.nextState = switch (executionType) {
            case SERIAL -> new TreeSet<>();
            case PARALLEL -> new ConcurrentSkipListSet<>();
        };
        this.currentState = switch (executionType) {
            case SERIAL -> new TreeSet<>();
            case PARALLEL -> new ConcurrentSkipListSet<>();
        };
    }

    private void addToNextStep(ModelEntity modelEntity) {
        nextState.add(modelEntity);
        registerForSimulation.run();
        registerChanged.run();
    }

    /**
     * Processes any state changes initiated by other blocks.
     */
    public void step() {
        @NotNull SortedSet<ModelEntity> temp = currentState;
        currentState = nextState;
        nextState = temp;
        nextState.clear();
        nextState.addAll(invariant);
    }

    /**
     * Performs a single simulation step. This does not immediately affect other blocks: only after those blocks have
     * called their step() method.
     */
    public void simulateStep() {
        Iterator<ModelEntity> iterator = new TreeSet<>(currentState).iterator();
        ModelEntity simulating;
        while (iterator.hasNext()) {
            simulating = iterator.next();
            if (simulating.type() == BlockType.BARRIER) {
                nextState.add(simulating);
                continue;
            }
            SimulatedBlockContent lookingAt = getBlockAt(simulating.direction());
            SimulatedBlockContent blockLeft = getBlockAt(Direction.rotateLeft(simulating.direction()));
            SimulatedBlockContent blockRight = getBlockAt(Direction.rotateRight(simulating.direction()));
            iterator.remove();
            registerChanged.run();
            if (simulating.type() == BlockType.SPLIT_EXPLORER) {
                simulateSplitExplorer(simulating.direction(), lookingAt, blockLeft, blockRight);
            } else if (simulating.type() == BlockType.RANDOM_EXPLORER) {
                Direction directionBack = Direction.opposite(simulating.direction());
                simulateRandomExplorer(simulating.direction(), lookingAt, blockLeft, blockRight, getBlockAt(directionBack));
            }
        }
    }

    private void simulateSplitExplorer(Direction direction, @NotNull SimulatedBlockContent forward, @NotNull SimulatedBlockContent left, @NotNull SimulatedBlockContent right) {
        if (forward.hasBarrier()) {
            if (!left.hasBarrier()) {
                left.addToNextStep(new ModelEntity(BlockType.SPLIT_EXPLORER, Direction.rotateLeft(direction)));
            }
            if (!right.hasBarrier()) {
                right.addToNextStep(new ModelEntity(BlockType.SPLIT_EXPLORER, Direction.rotateRight(direction)));
            }
        } else {
            forward.addToNextStep(new ModelEntity(BlockType.SPLIT_EXPLORER, direction));
        }
    }

    private void simulateRandomExplorer(Direction direction, @NotNull SimulatedBlockContent forward, @NotNull SimulatedBlockContent left, @NotNull SimulatedBlockContent right, @NotNull SimulatedBlockContent back) {
        if (forward.hasBarrier()) {
            Set<Pair<SimulatedBlockContent, Direction>> candidates = new HashSet<>();
            if (!left.hasBarrier()) {
                candidates.add(new Pair<>(left, Direction.rotateLeft(direction)));
            }
            if (!right.hasBarrier()) {
                candidates.add(new Pair<>(right, Direction.rotateRight(direction)));
            }
            if (!back.hasBarrier()) {
                candidates.add(new Pair<>(back, Direction.opposite(direction)));
            }
            if (!candidates.isEmpty()) {
                Pair<SimulatedBlockContent, Direction> selected = RandomUtils.fromCollection(candidates);
                selected.first().addToNextStep(new ModelEntity(BlockType.RANDOM_EXPLORER, selected.second()));
            }
        } else {
            forward.addToNextStep(new ModelEntity(BlockType.RANDOM_EXPLORER, direction));
        }
    }

    private boolean hasBarrier() {
        return currentState.stream().anyMatch(modelEntity -> modelEntity.type() == BlockType.BARRIER);
    }

    private @NotNull SimulatedBlockContent getBlockAt(Direction direction) {
        return switch (direction) {
            case UP ->  up;
            case DOWN -> down;
            case LEFT -> left;
            case RIGHT -> right;
            case NOT_APPLICCABLE -> throw new IllegalStateException("This isn't a direction");
        };
    }

    /**
     * Sets which block is to the left of this one.
     * @param left the block
     */
    public void setLeft(SimulatedBlockContent left) {
        this.left = left;
    }

    /**
     * Sets which block is to the right of this one.
     * @param right the block
     */
    public void setRight(SimulatedBlockContent right) {
        this.right = right;
    }

    /**
     * Sets which block is directly above this one.
     * @param up the block
     */
    public void setUp(SimulatedBlockContent up) {
        this.up = up;
    }

    /**
     * Sets which block is directly below this one.
     * @param down the block
     */
    public void setDown(SimulatedBlockContent down) {
        this.down = down;
    }

    /**
     * Resets this block to the specification (when the simulation starts)
     * @param specification the specification
     */
    public void clear(@NotNull Collection<ModelEntity> specification) {
        currentState.clear();
        nextState.clear();
        invariant.clear();
        currentState.addAll(specification);
        specification.stream().filter(modelEntity -> modelEntity.type() == BlockType.BARRIER).forEach(invariant::add);
        nextState.addAll(invariant);
    }

    @Override
    public @Nullable ModelEntity getVisibleEntity() {
        return currentState.isEmpty() ? null : currentState.first();
    }

    @Override
    public String toString() {
        return currentState.toString();
    }
}
