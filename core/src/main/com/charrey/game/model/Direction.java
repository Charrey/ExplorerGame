package com.charrey.game.model;

import com.badlogic.gdx.math.Vector2;
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
     * Provides the direction of a point in a 2D plane relative to another point.
     * @param from relative point
     * @param to point to retrieve the direction of
     * @return direction of the point
     */
    public static Direction relativeDirection(Vector2 from, Vector2 to) {
        Vector2 vector = new Vector2(to.x - from.x, from.y - to.y);
        int aboveFxisX = Float.compare(vector.y, vector.x);
        int aboveFxisMinusX = Float.compare(vector.y, -vector.x);
        if (aboveFxisX == 1) {
            if (aboveFxisMinusX == 1) {
                return UP;
            } else if (aboveFxisMinusX == -1) {
                return LEFT;
            }
        } else if (aboveFxisX == -1) {
            if (aboveFxisMinusX == 1) {
                return RIGHT;
            } else {
                return DOWN;
            }
        }
        return null;
    }

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
