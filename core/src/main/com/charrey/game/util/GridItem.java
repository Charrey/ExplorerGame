package com.charrey.game.util;

import com.charrey.game.model.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.ToIntFunction;

/**
 * Stores the location of something inside a 2D grid.
 */
public record GridItem(int x, int y) implements Comparable<GridItem> {

    private static final Comparator<GridItem> comparator = Comparator.comparingInt((ToIntFunction<GridItem>) value -> value.x).thenComparingInt(value -> value.y);

    @Override
    public int compareTo(@NotNull GridItem o) {
        return comparator.compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GridItem gridItem = (GridItem) o;
        if (x != gridItem.x) return false;
        return y == gridItem.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    /**
     * Provides an adjacent griditem in the specified direction of this griditem
     *
     * @param direction direction of this griditem
     * @return adjacent griditem
     */
    public GridItem copyInDirection(Direction direction) {
        return switch (direction) {
            case UP -> new GridItem(x(), y() + 1);
            case DOWN -> new GridItem(x(), y() - 1);
            case LEFT -> new GridItem(x() - 1, y());
            case RIGHT -> new GridItem(x() + 1, y());
        };
    }
}
