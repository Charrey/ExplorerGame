package com.charrey.game.model;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Enum listing the directions a block can face.
 */
public enum Direction {
    /**
     * Pointing upwards
     */
    UP,
    /**
     * Pointing downwards
     */
    DOWN,
    /**
     * Pointing to the left
     */
    LEFT,
    /**
     * Pointing to the right
     */
    RIGHT;

    /**
     * Returns the direction 270 degrees to the right (or 90 degrees to the left) of this direction
     * @return the direction to its left
     */
    public @NotNull Direction rotateLeft() {
        return switch (this) {
            case UP -> LEFT;
            case DOWN -> RIGHT;
            case LEFT -> DOWN;
            case RIGHT -> UP;
        };
    }

    /**
     * Returns the direction 90 degrees to the right (or 270 degrees to the left) of this direction
     * @return the direction to its right
     */
    public @NotNull Direction rotateRight() {
        return switch (this) {
            case UP -> RIGHT;
            case DOWN -> LEFT;
            case LEFT -> UP;
            case RIGHT -> DOWN;
        };
    }

    /**
     * Returns the direction opposite to this direction
     * @return the opposite direction
     */
    public @NotNull Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    /**
     * Performs an operation for each wind direction (thus excluding NOT_APPLICABLE)
     * @param consumer the operation
     */
    public static void forEachConcrete(@NotNull Consumer<Direction> consumer) {
        consumer.accept(UP);
        consumer.accept(DOWN);
        consumer.accept(LEFT);
        consumer.accept(RIGHT);
    }
}
