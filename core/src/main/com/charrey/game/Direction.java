package com.charrey.game;

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
    RIGHT,
    /**
     * Used as a direction for entities for which a direction makes no sense (e.g. a barricade).
     */
    NOT_APPLICCABLE;

    /**
     * Returns the direction 270 degrees to the right (or 90 degrees to the left) of the provided direction
     * @param direction a direction
     * @return the direction to its left
     */
    public static @NotNull Direction rotateLeft(Direction direction) {
        return switch (direction) {
            case UP -> LEFT;
            case DOWN -> RIGHT;
            case LEFT -> DOWN;
            case RIGHT -> UP;
            case NOT_APPLICCABLE -> NOT_APPLICCABLE;
        };
    }

    /**
     * Returns the direction 90 degrees to the right (or 270 degrees to the left) of the provided direction
     * @param direction a direction
     * @return the direction to its right
     */
    public static @NotNull Direction rotateRight(Direction direction) {
        return switch (direction) {
            case UP -> RIGHT;
            case DOWN -> LEFT;
            case LEFT -> UP;
            case RIGHT -> DOWN;
            case NOT_APPLICCABLE -> NOT_APPLICCABLE;
        };
    }

    /**
     * Returns the direction opposite to the provided direction
     * @param direction a direction
     * @return the opposite direction
     */
    public static @NotNull Direction opposite(Direction direction) {
        return switch (direction) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case NOT_APPLICCABLE -> NOT_APPLICCABLE;
        };
    }

    /**
     * Performs an operation for each wind direction (thus excluding NOT_APPLICABLE)
     * @param consumer the operation
     */
    public static void forEachConcrete(Consumer<Direction> consumer) {
        consumer.accept(UP);
        consumer.accept(DOWN);
        consumer.accept(LEFT);
        consumer.accept(RIGHT);
    }
}
