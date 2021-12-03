package com.charrey.game.stage.actor;

import com.charrey.game.BlockType;
import com.charrey.game.Direction;
import com.charrey.game.model.ModelEntity;
import com.charrey.game.util.Pair;
import com.charrey.game.util.random.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SimulatedBlockContent implements BlockContent {

    private final Runnable registerForSimulation;

    @NotNull
    private final SortedSet<ModelEntity> invariant = new TreeSet<>();
    @NotNull
    private final SortedSet<ModelEntity> currentState = new TreeSet<>();
    @NotNull
    private final SortedSet<ModelEntity> nextState = new TreeSet<>();

    SimulatedBlockContent left;
    SimulatedBlockContent right;
    SimulatedBlockContent up;
    SimulatedBlockContent down;

    public SimulatedBlockContent(Runnable registerForSimulation) {
        this.registerForSimulation = registerForSimulation;
    }

    private void addToNextStep(ModelEntity modelEntity) {
        nextState.add(modelEntity);
        registerForSimulation.run();
    }

    public void step() {
        currentState.clear();
        currentState.addAll(nextState);
        nextState.clear();
        nextState.addAll(invariant);
    }


    public void simulateStep() {
        Iterator<ModelEntity> iterator = currentState.iterator();
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

    public void setLeft(SimulatedBlockContent left) {
        this.left = left;
    }

    public void setRight(SimulatedBlockContent right) {
        this.right = right;
    }

    public void setUp(SimulatedBlockContent up) {
        this.up = up;
    }

    public void setDown(SimulatedBlockContent down) {
        this.down = down;
    }

    public void clear(@NotNull Collection<ModelEntity> specification) {
        currentState.clear();
        nextState.clear();
        invariant.clear();
        currentState.addAll(specification);
        specification.stream().filter(modelEntity -> modelEntity.type() == BlockType.BARRIER).forEach(invariant::add);
        nextState.addAll(invariant);
    }

    @Override
    public @Nullable BlockType getVisibleBlockType() {
        return currentState.isEmpty() ? null : currentState.first().type();
    }
}
